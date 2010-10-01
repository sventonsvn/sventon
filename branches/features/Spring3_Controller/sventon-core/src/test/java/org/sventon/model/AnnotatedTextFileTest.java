package org.sventon.model;

import org.junit.Test;
import org.sventon.colorer.Colorer;
import org.sventon.colorer.JHighlightColorer;

import java.util.Date;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class AnnotatedTextFileTest {

  private static final String NL = System.getProperty("line.separator");

  @Test
  public void testAnnotatedTextFile() throws Exception {
    final AnnotatedTextFile file = new AnnotatedTextFile();
    final String row1 = "file row one";
    final String row2 = "file row two";

    file.addRow(new Date(), 1, "jesper", row1);
    file.addRow(new Date(), 2, "jesper", row2);
    assertEquals(row1 + NL + row2 + NL, file.getContent());
  }

  @Test
  public void testAnnotatedTextFileJava() throws Exception {
    final AnnotatedTextFile file = new AnnotatedTextFile("test.java", "UTF-8", getColorer());
    final String row1 = "public class Test {";
    final String row2 = "}";

    final String coloredRow1 = "<span class=\"java_keyword\">public</span><span class=\"java_plain\">&#160;</span><span class=\"java_keyword\">class</span><span class=\"java_plain\">&#160;</span><span class=\"java_type\">Test</span><span class=\"java_plain\">&#160;</span><span class=\"java_separator\">{</span><span class=\"java_plain\"></span>";
    final String coloredRow2 = "<span class=\"java_separator\">}</span><span class=\"java_plain\"></span>";

    file.addRow(new Date(), 1, "jesper", row1);
    file.addRow(new Date(), 2, "jesper", row2);
    file.colorize();
    assertEquals(coloredRow1 + NL + coloredRow2 + NL, file.getContent());
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