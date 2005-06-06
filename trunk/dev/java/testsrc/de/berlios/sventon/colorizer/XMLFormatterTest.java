package de.berlios.sventon.colorizer;


import junit.framework.*;
import de.berlios.sventon.colorizer.JavaFormatter;

import java.io.StringReader;

public class XMLFormatterTest extends TestCase {

  public void testReadLineAndKeyword() throws Exception {
    String input = "<tag>something</tag>";
    Formatter formatter = new XMLFormatter();
    assertEquals("<span class=\"srcKeyword\">&lt;tag&gt;</span>something<span class=\"srcKeyword\">&lt;/tag&gt;</span>", formatter.format(input));
  }

  public void testReadLineAndKeywordWithAttributes() throws Exception {
    String input = "<component name=\"ModuleRootManager\" />";
    Formatter formatter = new XMLFormatter();
    assertEquals("<span class=\"srcKeyword\">&lt;component name=&quot;ModuleRootManager&quot; /&gt;</span>", formatter.format(input));
  }
}