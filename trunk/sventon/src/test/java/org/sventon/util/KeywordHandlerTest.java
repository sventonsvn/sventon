package org.sventon.util;

import junit.framework.TestCase;
import org.sventon.model.Properties;
import org.sventon.model.Property;
import org.sventon.model.PropertyValue;

public class KeywordHandlerTest extends TestCase {

  public void testSubstitute() throws Exception {
    final Properties keywordsMap = new Properties();
    keywordsMap.put(Property.KEYWORDS, new PropertyValue("Id Author Date Revision URL"));
    keywordsMap.put(Property.LAST_AUTHOR, new PropertyValue("domain\\user"));
    keywordsMap.put(Property.COMMITTED_DATE, new PropertyValue("2005-09-05T18:27:48.718750Z"));
    keywordsMap.put(Property.COMMITTED_REVISION, new PropertyValue("33"));

    final String url = "http://server/file.dat";
    final KeywordHandler handler = new KeywordHandler(keywordsMap, url);

    final StringBuilder sb = new StringBuilder();
    sb.append("/**\n");
    sb.append(" * $Author$\n");
    sb.append(" * $Revision$\n");
    sb.append(" * $URL$\n");
    sb.append(" */\n");
    sb.append("public String getRev {\n");
    sb.append(" return \"$Rev$\";\n");
    sb.append("}\n");

    final String result = handler.substitute(sb.toString(), "UTF-8");
    assertTrue(result.contains("file.dat"));
    assertTrue(result.contains("$Rev: 33 $"));
    assertTrue(result.contains("Author: domain\\user $"));
  }

  public void testComputeKeywordsNull() throws Exception {
    final Properties keywordsMap = new Properties();
    assertEquals("No change", new KeywordHandler(keywordsMap, null).substitute("No change", "UTF-8"));
  }

}
