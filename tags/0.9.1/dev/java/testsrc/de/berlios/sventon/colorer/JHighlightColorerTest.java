package de.berlios.sventon.colorer;

import junit.framework.TestCase;

public class JHighlightColorerTest extends TestCase {

  public void testGetColorizedContent() throws Exception {
    Colorer colorer = new JHighlightColorer();

    // Should produce colorized java code
    assertEquals("<span class=\"java_keyword\">public</span><span class=\"java_plain\"> </span><span class=\"java_keyword\">class</span><span class=\"java_plain\"> </span><span class=\"java_type\">HelloWorld</span><span class=\"java_plain\">\n</span>",
        (colorer.getColorizedContent("public class HelloWorld", "file.java")).trim());

    // Should produce content in plain text mode
    assertEquals("public class HelloWorld",
        (colorer.getColorizedContent("public class HelloWorld", "testing")).trim());

    // Should produce content in plain text mode
    assertEquals("public class HelloWorld",
        (colorer.getColorizedContent("public class HelloWorld", "")).trim());

    try {
      assertEquals("public class HelloWorld",
        (colorer.getColorizedContent("public class HelloWorld", null)).trim());
      fail("If content is null, NPE is expected.");
    } catch (NullPointerException npe) { /* expected */ }

    try {
      assertEquals("public class HelloWorld",
          (colorer.getColorizedContent(null, null)).trim());
      fail("If content is null, NPE is expected.");
    } catch (NullPointerException npe) { /* expected */ }

    assertEquals("", colorer.getColorizedContent(null, "file.java"));

  }

}