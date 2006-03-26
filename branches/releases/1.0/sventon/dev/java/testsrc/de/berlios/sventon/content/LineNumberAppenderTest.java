package de.berlios.sventon.content;

import junit.framework.TestCase;
import de.berlios.sventon.content.LineNumberAppender;

public class LineNumberAppenderTest extends TestCase {

  public void testLineNumberAppender() throws Exception {
    String newline = System.getProperty("line.separator");
    String test = "Line one" + newline + "Line two";
    LineNumberAppender app = new LineNumberAppender();
    assertEquals("1Line one" + newline + "2Line two", app.appendTo(test));
  }

  public void testLineNumberAppenderWithPadding() throws Exception {
    String newline = System.getProperty("line.separator");
    String test = "Line one" + newline + "Line two";
    LineNumberAppender app = new LineNumberAppender();
    app.setPadding(5);
    assertEquals("    1Line one" + newline + "    2Line two", app.appendTo(test));
  }

  public void testLineNumberAppenderWithZeroPadding() throws Exception {
    String newline = System.getProperty("line.separator");
    String test = "Line one" + newline + "Line two";
    LineNumberAppender app = new LineNumberAppender();
    app.setPadding(5);
    app.setPaddingCharacter('0');
    assertEquals("00001Line one" + newline + "00002Line two", app.appendTo(test));
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

  public void testLineNumberAppenderEmbeddedNumbersZeroPaddedAndOffset() throws Exception {
    String newline = System.getProperty("line.separator");
    String test = "Line one" + newline + "Line two";
    LineNumberAppender app = new LineNumberAppender();
    app.setEmbedStart("<span class=test>");
    app.setEmbedEnd(":&nbsp;</span>");
    app.setLineNumberOffset(20);
    app.setPadding(6);
    app.setPaddingCharacter('0');
    assertEquals("<span class=test>000021:&nbsp;</span>Line one" + newline + "<span class=test>000022:&nbsp;</span>Line two", app.appendTo(test));
  }

}