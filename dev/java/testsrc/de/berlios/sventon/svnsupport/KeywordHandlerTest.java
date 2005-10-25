package de.berlios.sventon.svnsupport;

import junit.framework.TestCase;

import java.util.Map;
import java.util.Collections;

public class KeywordHandlerTest extends TestCase {

  public void testSubstitute() throws Exception {
    Map keywordsMap;
    String keywords = "Author Date Revision URL";
    String url = "http://server/file.dat";
    String author = null;
    String date = "2005-09-05T18:27:48.718750Z";
    String rev = "33";
    keywordsMap = KeywordHandler.computeKeywords(keywords, url, author, date, rev);
    assertEquals("33", keywordsMap.get("Revision"));
    assertEquals("", (String) keywordsMap.get("Author"));
    assertEquals("http://server/file.dat", keywordsMap.get("URL"));

    StringBuilder sb = new StringBuilder();
    sb.append("/**\n");
    sb.append(" * $Author$\n");
    sb.append(" * $Revision$\n");
    sb.append(" * $URL$\n");
    sb.append(" */\n");
    sb.append("public String getRev {\n");
    sb.append(" return \"$Rev$\";\n");
    sb.append("}\n");

    String result = KeywordHandler.substitute(keywordsMap, sb.toString());
    assertTrue(result.indexOf("file.dat") > -1);
    assertTrue(result.indexOf("$Rev: 33 $") > -1);
  }

  public void testComputeKeywordsNull() throws Exception {
    assertEquals(Collections.EMPTY_MAP, KeywordHandler.computeKeywords(null, null, null, null, null));
  }

}