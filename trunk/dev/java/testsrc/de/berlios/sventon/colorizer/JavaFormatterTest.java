package de.berlios.sventon.colorizer;


import junit.framework.*;
import de.berlios.sventon.colorizer.JavaFormatter;

import java.io.StringReader;

public class JavaFormatterTest extends TestCase {

  public void testReadLineAndKeyword() throws Exception {
    String input = "public one class Test";
    Formatter formatter = new JavaFormatter();
    assertEquals("<span class=\"srcKeyword\">public</span> one <span class=\"srcKeyword\">class</span> Test", formatter.format(input));
  }

  public void testReadLineAndOneLineComment() throws Exception {
    String input = "/* comment */";
    Formatter formatter = new JavaFormatter();
    assertEquals("<span class=\"srcComment\">/* comment */</span>", formatter.format(input));
  }

  public void testReadLineAndMultiLineComment() throws Exception {
    String input1 = "/* first line";
    String input2 = "second line */";
    Formatter formatter = new JavaFormatter();
    assertEquals("<span class=\"srcComment\">/* first line</span>", formatter.format(input1));
    assertEquals("<span class=\"srcComment\">second line */</span>", formatter.format(input2));
  }

}