package de.berlios.sventon.svnsupport;

import junit.framework.TestCase;

public class LineNumberAppenderTest extends TestCase {

  public void testLineNumberAppender() throws Exception {
    String newline = System.getProperty("line.separator");
    String test = "Line one" + newline + "Line two";
    LineNumberAppender app = new LineNumberAppender();
    assertEquals("1: Line one" + newline + "2: Line two", app.appendTo(test));
  }

  public void testLineNumberAppenderEmbeddedNumbers() throws Exception {
    String newline = System.getProperty("line.separator");
    String test = "Line one" + newline + "Line two";
    LineNumberAppender app = new LineNumberAppender();
    app.setEmbedStart("<span class=test>");
    app.setEmbedEnd("</span>");
    assertEquals("<span class=test>1: </span>Line one" + newline + "<span class=test>2: </span>Line two", app.appendTo(test));
  }

  public void testLineNumberAppenderWithOffset() throws Exception {
    String newline = System.getProperty("line.separator");
    String test = "Line one" + newline + "Line two";
    LineNumberAppender app = new LineNumberAppender();
    app.setLineNumberOffset(10);
    assertEquals("11: Line one" + newline + "12: Line two", app.appendTo(test));
  }

}