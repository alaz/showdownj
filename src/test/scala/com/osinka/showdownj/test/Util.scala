package com.osinka.showdownj.test

trait TestUtils {
    import java.io._

    def xmlWrap(s: String) = scala.xml.XML.loadString(
        """<?xml version="1.0" encoding="UTF-8"?>
<root>
""" + s.trim + "\n</root>"
    )

    def textFromResource(r: String): Option[String] =
        readContents(getClass.getClassLoader.getResource(r)) .
        map { bytearr => new String(bytearr, "UTF-8").trim }

    def readContents(url: java.net.URL): Option[Array[Byte]] = {
        val in = url.openStream
        try {
            val buffer = new Array[Byte](2048)
            val out = new ByteArrayOutputStream

            def reader {
              val len = in.read(buffer)
              if (len < 0) return
              else if (len > 0) out.write(buffer, 0, len)
              reader
            }

            reader
            Some(out.toByteArray)
        } finally {
            in.close
        }
    }
}
