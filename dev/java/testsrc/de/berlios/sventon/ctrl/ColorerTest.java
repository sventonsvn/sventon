package de.berlios.sventon.ctrl;

import junit.framework.TestCase;

public class ColorerTest extends TestCase {

  public void testGetColorizedContent() throws Exception {
    Colorer colorer = new Colorer();

    // Should produce colorized java code
    assertEquals("<pre><span class=reservedWord>public</span><span class=whitespace> </span><span class=reservedWord>class</span><span class=whitespace> </span><span class=identifier>HelloWorld</span></pre>",
        (colorer.getColorizedContent("public class HelloWorld", "java", false)).trim());

    // Should produce content in plain text mode
    assertEquals("<pre><span class=text>public</span><span class=whitespace> </span><span class=text>class</span><span class=whitespace> </span><span class=text>HelloWorld</span></pre>",
        (colorer.getColorizedContent("public class HelloWorld", "testing", false)).trim());

    // Should produce content in plain text mode
    assertEquals("<pre><span class=text>public</span><span class=whitespace> </span><span class=text>class</span><span class=whitespace> </span><span class=text>HelloWorld</span></pre>",
        (colorer.getColorizedContent("public class HelloWorld", "", false)).trim());

    // Should produce content in plain text mode
    assertEquals("<pre><span class=text>public</span><span class=whitespace> </span><span class=text>class</span><span class=whitespace> </span><span class=text>HelloWorld</span></pre>",
        (colorer.getColorizedContent("public class HelloWorld", null, false)).trim());

    try {
      assertEquals("public class HelloWorld",
          (colorer.getColorizedContent(null, null, false)).trim());
      fail("If content is null, NPE is expected.");
    } catch (NullPointerException npe) { /* expected */ }

    try {
      assertEquals("public class HelloWorld",
          (colorer.getColorizedContent(null, "java", false)).trim());
      fail("If content is null, NPE is expected.");
    } catch (NullPointerException npe) { /* expected */ }
  }

  public void testGetColorizedContentWithLineNumbers() throws Exception {
    Colorer colorer = new Colorer();

    // Should produce colorized java code with line numbers.
    assertEquals("<pre>\n" +
        "<span class=text>    1: </span><span class=reservedWord>public</span><span class=whitespace>\n" +
        "<span class=text>    2: </span></span><span class=reservedWord>class</span><span class=whitespace>\n" +
        "<span class=text>    3: </span></span><span class=identifier>HelloWorld</span><span class=whitespace>\n" +
        "<span class=text>    4: </span></span>\n" +
        "</pre>",
        (colorer.getColorizedContent("public\nclass\nHelloWorld\n", "java", true)).trim());

  }

}