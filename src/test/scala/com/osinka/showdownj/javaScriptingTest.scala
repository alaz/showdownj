package com.osinka.showdownj

import org.specs._
import org.specs.runner._

class javaScriptingTest extends JUnit4(javaScriptingSpec) with Console
object javaScriptingRunner extends ConsoleRunner(javaScriptingSpec)

object javaScriptingSpec extends Specification {
    import javax.script._
    import scala.collection.jcl.Conversions._

    "Java Scripting" should {
        val mgr = new ScriptEngineManager()

        "has engines" in {
            val factories = mgr.getEngineFactories
            factories.size aka "The number of scripting engines" must beGreaterThan(0)
        }
        "has a JavaScript engine" in {
            mgr.getEngineByName("JavaScript") must notBeNull
        }
        "has an engine for js extension" in {
            mgr.getEngineByExtension("js") must notBeNull
        }
    }
}
