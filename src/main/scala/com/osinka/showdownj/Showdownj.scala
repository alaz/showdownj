/**
 * Links:
 * http://briancarper.net/blog/clojure-and-markdown-and-javascript-and-java-and
 * http://nkoksharov.blogspot.com/2008/12/rhino-janino.html
 * https://developer.mozilla.org/en/Rhino_documentation
 * http://java.sun.com/developer/technicalArticles/J2SE/Desktop/scripting/
 * http://www.javalobby.org/java/forums/t87870.html
 */

package com.osinka.showdownj

import scala.xml.{XML, Elem}

object Showdownj extends ShowdownImplJava6 with ShowdownConfig

trait ShowdownBase {
    // For invocation of JavaScript code
    val scriptName:     String
    val lineNo:         Int
    val securityDomain: AnyRef

    // Public API
    def xhtml(markdownString: String): Elem = XML.loadString(convert(markdownString))

    def convert(markdownString: String): String
}

trait ShowdownConfig {
    val scriptName:String       = "showdown.js"
    val lineNo: Int             = 1
    val securityDomain: AnyRef  = null
}

trait ShowdownImplHelpers {
    import java.io.InputStreamReader
    import java.net.URL

    val scriptName: String

    def legacy[T](t: T): Option[T] = if (t == null) None else Some(t)

    def resourceFinder(name: String): Option[URL] = legacy(getClass.getClassLoader.getResource(name))
    def scriptFileReader = resourceFinder(scriptName).map(url => new InputStreamReader(url.openStream))
}

abstract class ShowdownImplRhino extends ShowdownBase with ShowdownImplHelpers {
    import org.mozilla.javascript._

    @throws(classOf[java.io.FileNotFoundException])
    def convert(markdownString: String): String = contextFactory.call( new ContextAction() {
                @throws(classOf[java.io.FileNotFoundException])
                def run(ctx: Context): AnyRef = {
                    func(ctx, markdownString)
                }
            } ).toString

    @throws(classOf[java.io.FileNotFoundException])
    lazy val func = contextFactory.call(init).asInstanceOf[Function2[Context, String, AnyRef]]

    lazy val contextFactory: ContextFactory = new ContextFactory()

    @throws(classOf[java.io.FileNotFoundException])
    private object init extends ContextAction {
        @throws(classOf[java.io.FileNotFoundException])
        def run(ctx: Context): AnyRef = scriptFileReader match {
            case None =>
                throw new java.io.FileNotFoundException("Script " + scriptName + " could not be opened")
            case Some(reader) =>
                val scope = ctx.initStandardObjects
                ctx.evaluateReader(scope, reader, scriptName, 1, null)
                ctx.evaluateString(scope, "var converter = new Showdown.converter()", "new converter", 1, null)
                val f = ctx.compileFunction(scope, "function invoke(t) { return converter.makeHtml(t) }", "convert invocation", 1, null)
                (context: Context, markdownString: String) => f.call(context, scope, scope, List[AnyRef](markdownString).toArray)
        }
    }
}

abstract class ShowdownImplJava6 extends ShowdownBase with ShowdownImplHelpers {
    @throws(classOf[java.io.FileNotFoundException])
    @throws(classOf[RuntimeException])
    def convert(markdownString: String): String = showdownConverter(markdownString).toString

    import javax.script._

    lazy val scriptManager = new ScriptEngineManager()
    lazy val jsEngine      = legacy(scriptManager.getEngineByName("JavaScript"))

    @throws(classOf[java.io.FileNotFoundException])
    @throws(classOf[RuntimeException])
    lazy val showdownConverter = (scriptFileReader, jsEngine) match {
        case (None, _) =>
            throw new java.io.FileNotFoundException("Script " + scriptName + " could not be opened")
        case (Some(reader), Some(compiler: Compilable)) =>
            val bindings = compiler.createBindings

            compiler.compile(reader).eval
            bindings.put("converter", compiler.compile("new Showdown.converter()").eval)

            val script = compiler.compile("converter.makeHtml(text)")

            (markdownString: String) => {
                bindings.put("text", markdownString)
                script.eval(bindings)
            }
        case (Some(reader), Some(engine: Invocable)) =>
            engine.eval(reader)
            val script = engine.eval("new Showdown.converter()")
            
            (markdownString: String) => engine.invokeMethod(script, "makeHtml", markdownString)
        case (Some(_), None) =>
            throw new RuntimeException("No JavaScript engine found, suggest using Rhino directly")
    }
}
