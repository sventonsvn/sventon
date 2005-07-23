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
    assertEquals(
        "<a class=\"sventonLineNo\" name=\"1\" href=\"#1\">    1: </a><span class=reservedWord>public</span>\n" +
        "<a class=\"sventonLineNo\" name=\"2\" href=\"#2\">    2: </a><span class=reservedWord>class</span>\n" +
        "<a class=\"sventonLineNo\" name=\"3\" href=\"#3\">    3: </a><span class=identifier>HelloWorld</span>\n" +
        "<a class=\"sventonLineNo\" name=\"4\" href=\"#4\">    4: </a>\n",
        (colorer.getColorizedContent("public\nclass\nHelloWorld\n", "java", true)).trim());

  }

}