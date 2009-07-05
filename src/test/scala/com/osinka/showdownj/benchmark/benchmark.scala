package com.osinka.showdownj.benchmark

import scala.testing.Benchmark
import org.specs.util.SimpleTimer

object benchmark {
    val benchmarks = List(java6, rhino, markdownj)

    def main(args: Array[String]) {
        val repeat = args.firstOption.map(s => s.toInt).getOrElse(1)
        benchmarks map { benchmark => report(benchmark.prefix, benchmark.runBenchmark(repeat)) }
    }

    implicit def longToTimer(l: Long): SimpleTimer = { val t = new SimpleTimer; t.elapsed = l; t }

    def report(name: String, latencies: => List[Long]) {
        // The first run is taken out, it's a warm up
        val sorted = latencies.tail.sort( (a,b) => a < b )

        val (total, count, min, max) = (
            (0L /: sorted)((x, y) => x + y),
            sorted.size,
            sorted.head,
            sorted.last
        )
        val avg = total / count

        System.err.println(name + ", " + count + " iterations:"
                           + " total=[" + total.time + "]"
                           + ", min=["  + min.time   + "]"
                           + ", avg=["  + avg.time   + "]"
                           + ", max=["  + max.time   + "]")
    }
}

import scala.testing._
import com.osinka.showdownj.test.TestUtils
abstract class MarkdownBenchmark(override val prefix: String) extends Benchmark with TestUtils with SUnit.Assert {
    def parser(s: String): String

    val markdownSource = textFromResource("test1.md")
    val supposedResult = textFromResource("test1.txt").map{x => xmlWrap(x)}

    def run = {
        import org.specs.xml.NodeFunctions._

        val result = markdownSource.map(x => xmlWrap(parser(x)))
        assertTrue("Parsing is OK", isEqualIgnoringSpace(result.get, supposedResult.get))
    }
}

object markdownj extends MarkdownBenchmark("markdownj") {
    import com.petebevin.markdown._
    val parserImpl = new MarkdownProcessor
    def parser(x: String) = parserImpl.markdown(x)
}

object rhino extends MarkdownBenchmark("rhino") {
    import com.osinka.showdownj._
    val parserImpl = new ShowdownImplRhino with ShowdownConfig
    def parser(x: String) = parserImpl.convert(x)
}

object java6 extends MarkdownBenchmark("java6") {
    import com.osinka.showdownj._
    val parserImpl = new ShowdownImplJava6 with ShowdownConfig
    def parser(x: String) = parserImpl.convert(x)
}