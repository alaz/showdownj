package net.osinka.showdownj

import org.specs._
import org.specs.runner._
import test.TestUtils

class parsersTest extends JUnit4(parsersSpec) with Console
object parsersRunner extends ConsoleRunner(parsersSpec)

object parsersSpec extends Specification with SyntaxSpec {
    import net.osinka.showdownj.{ShowdownConfig, ShowdownImplJava6}

    include(
        doParserTest("Java6", new ShowdownImplJava6 with ShowdownConfig),
        doParserTest("Rhino", new ShowdownImplRhino with ShowdownConfig)
    )
}

trait SyntaxSpec extends TestUtils {

    def doParserTest(name: String, parser: ShowdownBase) = new Specification(name) {
        name should {
            "parse trivial Markdown" in {
                val markdownSource = """Heading
====
"""
                val supposedResult = "<h1>Heading</h1>"

                parser.convert(markdownSource) must be_==(supposedResult)
            }
            "parse more complex Markdown" in {
                val markdownSource = textFromResource("test1.md")
                val supposedResult = textFromResource("test1.txt").map{x => xmlWrap(x)}

                markdownSource.map(x => xmlWrap(parser.convert(x))) must beSome[scala.xml.Elem] .
                    which{ _ must ==/(supposedResult.get) }
            }
        }
    }
}
