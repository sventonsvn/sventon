package de.berlios.sventon.colorer;

import com.uwyn.jhighlight.renderer.JavaXhtmlRenderer;
import com.uwyn.jhighlight.renderer.XmlXhtmlRenderer;
import junit.framework.TestCase;

import java.util.Properties;

public class JHighlightColorerTest extends TestCase {
  private static final String ENCODING = "UTF-8";

  public void testGetColorizedContent() throws Exception {
    Colorer colorer = getColorer();

    // Should produce colorized java code
    assertEquals("<span class=\"java_keyword\">public</span><span class=\"java_plain\">&nbsp;</span><span class=\"java_keyword\">class</span><span class=\"java_plain\">&nbsp;</span><span class=\"java_type\">HelloWorld</span><span class=\"java_plain\"></span>",
        (colorer.getColorizedContent("public class HelloWorld", "java", ENCODING)));

    // Should produce content in plain text mode
    assertEquals("public class HelloWorld",
        (colorer.getColorizedContent("public class HelloWorld", "testing", ENCODING)));

    // Should produce content in plain text mode
    assertEquals("public class HelloWorld",
        (colorer.getColorizedContent("public class HelloWorld", "", ENCODING)));

    try {
      assertEquals("public class HelloWorld",
          (colorer.getColorizedContent("public class HelloWorld", null, ENCODING)));
      fail("If filename is null, IAE is expected.");
    } catch (IllegalArgumentException iae) { /* expected */ }

    try {
      assertEquals("public class HelloWorld",
          (colorer.getColorizedContent(null, null, ENCODING)));
      fail("If content is null, IAE is expected.");
    } catch (IllegalArgumentException iae) { /* expected */ }

    assertEquals("", colorer.getColorizedContent(null, "java", ENCODING));
  }

  public void testGetRenderer() throws Exception {
    JHighlightColorer col = (JHighlightColorer) getColorer();

    try {
      col.getRenderer(null);
      fail("If content is null, IAE is expected.");
    } catch (IllegalArgumentException iae) { /* expected */ }

    assertNull(col.getRenderer(""));
    assertNull(col.getRenderer("txt"));
    assertNull(col.getRenderer("htm"));
    assertTrue(col.getRenderer("java") instanceof JavaXhtmlRenderer);
    assertTrue(col.getRenderer("xml") instanceof XmlXhtmlRenderer);
  }

  private Colorer getColorer() {
    final JHighlightColorer col = new JHighlightColorer();
    final Properties mappings = new Properties();
    mappings.put("java", new com.uwyn.jhighlight.renderer.JavaXhtmlRenderer());
    mappings.put("html", new com.uwyn.jhighlight.renderer.XmlXhtmlRenderer());
    mappings.put("xml", new com.uwyn.jhighlight.renderer.XmlXhtmlRenderer());
    col.setRendererMappings(mappings);
    return col;
  }

}