package net.osinka.showdownj.benchmark

import scala.testing._
import net.osinka.showdownj._
import net.osinka.showdownj.test.TestUtils

object rhino extends Benchmark with TestUtils with SUnit.Assert {
    val markdownSource = textFromResource("test1.md")
    val supposedResult = textFromResource("test1.txt")
    val parser = new ShowdownImplRhino with ShowdownConfig

    override def prefix = "rhino"

    def run = {
        assertEquals("Parsing is OK", markdownSource.map(parser.convert(_)), supposedResult)
    }
}
