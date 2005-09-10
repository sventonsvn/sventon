package de.berlios.sventon.svnsupport;

import junit.framework.TestCase;

import java.util.Map;

public class KeywordHandlerTest extends TestCase {
  KeywordHandler keywordHandler;

  public void testComputeKeywords() throws Exception {
    Map keywordsMap = null;
    String keywords = "Author Date Revision URL";
    String url = "http://server/file.dat";
    String author = null;
    String date = "2005-09-05T18:27:48.718750Z";
    String rev = "33";
    keywordsMap = KeywordHandler.computeKeywords(keywords, url, author, date, rev);
    assertEquals("33", keywordsMap.get("Revision"));
    assertEquals("", (String) keywordsMap.get("Author"));
    assertEquals("http://server/file.dat", keywordsMap.get("URL"));
  }
}