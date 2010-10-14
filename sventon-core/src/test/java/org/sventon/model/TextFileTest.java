package org.sventon.model;

import org.junit.Test;
import org.sventon.Colorer;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TextFileTest {

  private static final String BR = System.getProperty("line.separator");

  @Test
  public void testTextFilePlain() throws Exception {
    final String content = "Line one" + BR + "Line two";
    final TextFile textFile = new TextFile(content);
    assertEquals(2, textFile.size());
    assertEquals("Line one" + BR + "Line two" + BR, textFile.getContent());
  }

  @Test
  public void testTextFilePlainInitialBR() throws Exception {
    final String content = BR + "Line one" + BR + "Line two";
    final TextFile textFile = new TextFile(content);
    assertEquals(3, textFile.size());
    assertEquals(BR + "Line one" + BR + "Line two" + BR, textFile.getContent());
  }

  @Test
  public void testTextFilePlainWebSafe() throws Exception {
    final String content = "Line&one" + BR + "Line<two>";
    final TextFile textFile = new TextFile(content);
    assertEquals(2, textFile.size());
    assertEquals("Line&amp;one" + BR + "Line&lt;two&gt;" + BR, textFile.getContent());
  }

  @Test
  public void testTextFileColorized() throws Exception {
    final String content = "class Test {" + BR + "// <test> " + BR + "}";
    final TextFile textFile = new TextFile(content, "Test.java", "UTF-8", new Colorer() {
      @Override
      public String getColorizedContent(String content, String fileExtension, String encoding) throws IOException {
        return "<test>" + content + "</test>";
      }
    });
    assertEquals(3, textFile.size());
    assertEquals("<test>class Test {" + BR + "// <test> " + BR + "}</test>" + BR, textFile.getContent());
  }

}