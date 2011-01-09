/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import org.junit.Test;
import org.sventon.Colorer;

import java.io.IOException;
import java.util.Date;

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
    final AnnotatedTextFile file = new AnnotatedTextFile("test.java", "UTF-8", new Colorer() {
      @Override
      public String getColorizedContent(String content, String fileExtension, String encoding) throws IOException {
        final StringBuilder sb = new StringBuilder();
        for (String string : content.split(NL)) {
          sb.append("<test>");
          sb.append(string);
          sb.append("</test>");
          sb.append(NL);
        }
        return sb.toString();
      }
    });
    final String row1 = "public class Test {";
    final String row2 = "}";

    final String coloredRow1 = "<test>public class Test {</test>";
    final String coloredRow2 = "<test>}</test>";

    file.addRow(new Date(), 1, "jesper", row1);
    file.addRow(new Date(), 2, "jesper", row2);
    file.colorize();
    assertEquals(coloredRow1 + NL + coloredRow2 + NL, file.getContent());
  }

}