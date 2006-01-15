package de.berlios.sventon.svnsupport;

import junit.framework.TestCase;

public class LineNumberAppenderTest extends TestCase {

  public void testLineNumberAppender() throws Exception {
    String newline = System.getProperty("line.separator");
    String test = "Line one" + newline + "Line two";
    LineNumberAppender app = new LineNumberAppender();
    assertEquals("1Line one" + newline + "2Line two", app.appendTo(test));
  }

  public void testLineNumberAppenderEmbeddedNumbers() throws Exception {
    String newline = System.getProperty("line.separator");
    String test = "Line one" + newline + "Line two";
    LineNumberAppender app = new LineNumberAppender();
    app.setEmbedStart("<span class=test>");
    app.setEmbedEnd(":&nbsp;</span>");
    assertEquals("<span class=test>1:&nbsp;</span>Line one" + newline + "<span class=test>2:&nbsp;</span>Line two", app.appendTo(test));
  }

  public void testLineNumberAppenderWithOffset() throws Exception {
    String newline = System.getProperty("line.separator");
    String test = "Line one" + newline + "Line two";
    LineNumberAppender app = new LineNumberAppender();
    app.setLineNumberOffset(10);
    assertEquals("11Line one" + newline + "12Line two", app.appendTo(test));
  }

}