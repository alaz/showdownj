This tiny project is aimed to investigate ways of processing of [Markdown][] markup
language for Scala projects. It was inspired by the article of Brian Carper
["Clojure and Markdown (and Javascript and Java and...)"][clojs]

The project provides a trivial Scala API to process Markdown markup based on three
implementations:

* [MarkdownJ][] is a pure Java library
* [Showdown][] is a JavaScript implementation of Markdown, so it's possible to call
  it from Java using JavaScript implementations (this is the way Brian Carper proposed).
  I chose
  * [Mozilla Rhino][rhino] and
  * [Java6 Scripting][java6] (which is basically Rhino, according to [this article][j6scripts])

### Benchmark
Along with the library you may find a simple benchmark comparing all three methods
described above. Do not trust absolute numbers blindly, as every run does Markdown
source processing, transformation to XML and then compares resulting XML to the
reference one.

Average time to process long Markdown document (Markdown syntax description):

  * Java6 = 314 ms
  * Rhino = 316 ms
  * MarkdownJ = 187 ms

Tests run with JDK 1.6 on MacBookPro 2.53GHz

#### Special note for Apple VM
If you have trouble running JavaScript engine in Java6 under Mac OSX,
["How to include JavaScript engine in Apple's Java 6 VM"][applejvm] has a good
recipe.

[markdown]: http://daringfireball.net/projects/markdown/
[clojs]: http://briancarper.net/blog/clojure-and-markdown-and-javascript-and-java-and
[markdownj]: http://code.google.com/p/markdownj/
[showdown]: http://attacklab.net/showdown/
[rhino]: http://www.mozilla.org/rhino/
[java6]: http://java.sun.com/javase/6/docs/api/javax/script/package-summary.html
[j6scripts]: http://www.javalobby.org/java/forums/t87870.html
[applejvm]: http://jmesnil.net/weblog/2008/05/14/how-to-include-javascript-engine-in-apples-java-6-vm/