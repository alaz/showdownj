Project
-----
This tiny project is aimed to investigate ways of processing of [Markdown] markup
language for Java projects. It was inspired by the article of Brian Carper
["Clojure and Markdown (and Javascript and Java and...)"][clojure-js]

The project provides a trivial API to process Markdown markup based on three
implementations:

  - [markdownj] is a Java library
  - [showdownj] is a JavaScript implementation of Markdown, so it's possible to call
it from Java using JavaScript implementations (this is the way Brian Carper proposed).
I chose
    - [Mozilla Rhino][rhino] and
    - [Java6 Scripting][java6] (which is basically Rhino, but without

   [clojure-js]: http://briancarper.net/blog/clojure-and-markdown-and-javascript-and-java-and
   [markdownj]: http://code.google.com/p/markdownj/ "MarkdownJ"
   [showdownj]: http://attacklab.net/showdown/
   [rhino]: http://www.mozilla.org/rhino/
   [java6]: http://java.sun.com/javase/6/docs/api/javax/script/package-summary.html

Benchmark
-----
Along with the library you may find a simple benchmark comparing all three methods
described above. Do not trust absolute numbers blindly, as every run does Markdown
source processing, transformation to XML and then compares resulting XML to the
reference one.