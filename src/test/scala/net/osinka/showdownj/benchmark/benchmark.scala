package net.osinka.showdownj.benchmark

import scala.testing.Benchmark
import org.specs.util.SimpleTimer

object benchmark {
    val benchmarks = List(java6, rhino)

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
