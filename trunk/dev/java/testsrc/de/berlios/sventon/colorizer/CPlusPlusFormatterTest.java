package de.berlios.sventon.colorizer;


import junit.framework.*;
import de.berlios.sventon.colorizer.JavaFormatter;

import java.io.StringReader;

public class CPlusPlusFormatterTest extends TestCase {

  public void testReadLineAndKeyword() throws Exception {
    String input = "public class Test";
    Formatter formatter = new CPlusPlusFormatter();
    assertEquals("<span class=\"srcKeyword\">public</span> <span class=\"srcKeyword\">class</span> Test", formatter.format(input));
  }

}