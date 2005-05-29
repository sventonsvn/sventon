package de.berlios.sventon.colorizer;


import junit.framework.*;
import de.berlios.sventon.colorizer.JavaFormatter;

import java.io.StringReader;

public class FormatterImplTest extends TestCase {

  public void testReadLine() throws Exception {
    String input = "public class Test";
    Formatter formatter = new FormatterImpl();
    assertEquals(input, formatter.format(input));
  }

  public void testReadLineAndEntityReplacement() throws Exception {
    String input = "<input type=\"text\" value=\"&\">";
    Formatter formatter = new JavaFormatter();
    assertEquals("&lt;input type=&quot;text&quot; value=&quot;&amp;&quot;&gt;", formatter.format(input));
  }
  
}