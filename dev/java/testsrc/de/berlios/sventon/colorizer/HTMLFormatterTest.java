package de.berlios.sventon.colorizer;


import junit.framework.*;
import de.berlios.sventon.colorizer.JavaFormatter;

import java.io.StringReader;

public class HTMLFormatterTest extends TestCase {

  public void testReadLineAndKeyword() throws Exception {
    String input = "<tag>something</tag>";
    Formatter formatter = new HTMLFormatter();
    assertEquals("<span class=\"srcKeyword\">&lt;tag&gt;</span>something<span class=\"srcKeyword\">&lt;/tag&gt;</span>", formatter.format(input));
  }

}