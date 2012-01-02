/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.colorer;

import com.uwyn.jhighlight.renderer.CSharpXhtmlRenderer;
import com.uwyn.jhighlight.renderer.JavaXhtmlRenderer;
import com.uwyn.jhighlight.renderer.Renderer;
import com.uwyn.jhighlight.renderer.XmlXhtmlRenderer;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class JHighlightColorerTest {

  private static final String ENCODING = "UTF-8";

  @Test
  public void testGetColorizedContent() throws Exception {
    final JHighlightColorer colorer = new JHighlightColorer();
    colorer.setRendererMappings(getRendererMappings());

    // Should produce colorized java code
    assertEquals("<span class=\"java_keyword\">public</span><span class=\"java_plain\">&#160;</span>" +
        "<span class=\"java_keyword\">class</span><span class=\"java_plain\">&#160;</span><span class=\"java_type\">" +
        "HelloWorld</span><span class=\"java_plain\"></span>",
        (colorer.getColorizedContent("public class HelloWorld", "java", ENCODING)));

    // Should produce colorized csharp code
    assertEquals("<span class=\"csharp_keyword\">private</span><span class=\"csharp_plain\">&#160;</span>" +
        "<span class=\"csharp_type\">void</span><span class=\"csharp_plain\">&#160;Build</span>" +
        "<span class=\"csharp_separator\">(</span><span class=\"csharp_plain\">XmlNode&#160;node</span>" +
        "<span class=\"csharp_separator\">)</span><span class=\"csharp_plain\"></span>",
        colorer.getColorizedContent("private void Build(XmlNode node)", "cs", ENCODING));

    // Should produce content in plain text mode
    assertEquals("public class HelloWorld", (colorer.getColorizedContent("public class HelloWorld", "testing", ENCODING)));

    // Should produce content in plain text mode
    assertEquals("public class HelloWorld", (colorer.getColorizedContent("public class HelloWorld", "", ENCODING)));

    try {
      assertEquals("public class HelloWorld", (colorer.getColorizedContent("public class HelloWorld", null, ENCODING)));
      fail("If filename is null, IAE is expected.");
    } catch (IllegalArgumentException iae) { /* expected */ }

    assertEquals("", (colorer.getColorizedContent(null, null, ENCODING)));

    assertEquals("", colorer.getColorizedContent(null, "java", ENCODING));
  }

  @Test
  public void testNoRenderer() throws Exception {
    final JHighlightColorer colorer = new JHighlightColorer();
    assertEquals(null, colorer.getRenderer("test"));

    try {
      colorer.setRendererMappings(null);
      fail("Should throw IAE");
    } catch (IllegalArgumentException iae) {
      // expected
    }
  }

  @Test
  public void testGetRenderer() throws Exception {
    final JHighlightColorer colorer = new JHighlightColorer();
    colorer.setRendererMappings(getRendererMappings());

    try {
      colorer.getRenderer(null);
      fail("If content is null, IAE is expected.");
    } catch (IllegalArgumentException iae) { /* expected */ }

    assertNull(colorer.getRenderer(""));
    assertNull(colorer.getRenderer("txt"));
    assertNull(colorer.getRenderer("htm"));
    assertTrue(colorer.getRenderer("java") instanceof JavaXhtmlRenderer);
    assertTrue(colorer.getRenderer("xml") instanceof XmlXhtmlRenderer);
    assertTrue(colorer.getRenderer("cs") instanceof CSharpXhtmlRenderer);
  }

  private Map<String, Renderer> getRendererMappings() {
    final Map<String, Renderer> mappings = new HashMap<String, Renderer>();
    mappings.put("java", new JavaXhtmlRenderer());
    mappings.put("html", new XmlXhtmlRenderer());
    mappings.put("xml", new XmlXhtmlRenderer());
    mappings.put("cs", new CSharpXhtmlRenderer());
    return mappings;
  }

}