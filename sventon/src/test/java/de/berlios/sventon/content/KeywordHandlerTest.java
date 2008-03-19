package de.berlios.sventon.content;

import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNProperty;

import java.util.HashMap;
import java.util.Map;

public class KeywordHandlerTest extends TestCase {

  public void testSubstitute() throws Exception {
    final Map<String, String> keywordsMap = new HashMap<String, String>();
    keywordsMap.put(SVNProperty.KEYWORDS, "Author Date Revision URL");
    keywordsMap.put(SVNProperty.LAST_AUTHOR, "domain\\user");
    keywordsMap.put(SVNProperty.COMMITTED_DATE, "2005-09-05T18:27:48.718750Z");
    keywordsMap.put(SVNProperty.COMMITTED_REVISION, "33");

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
    assertTrue(result.indexOf("file.dat") > -1);
    assertTrue(result.indexOf("$Rev: 33 $") > -1);
  }

  public void testComputeKeywordsNull() throws Exception {
    final Map<String, String> keywordsMap = new HashMap<String, String>();
    assertEquals("No change", new KeywordHandler(keywordsMap, null).substitute("No change", "UTF-8"));
  }

}