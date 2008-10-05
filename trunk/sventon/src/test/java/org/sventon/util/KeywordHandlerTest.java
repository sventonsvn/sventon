package org.sventon.util;

import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;

import java.util.HashMap;
import java.util.Map;

public class KeywordHandlerTest extends TestCase {

  public void testSubstitute() throws Exception {
    final SVNProperties keywordsMap = new SVNProperties();
    keywordsMap.put(SVNProperty.KEYWORDS, "Id Author Date Revision URL");
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
    assertTrue(result.contains("file.dat"));
    assertTrue(result.contains("$Rev: 33 $"));
    assertTrue(result.contains("Author: domain\\user $"));
  }

  public void testComputeKeywordsNull() throws Exception {
    final SVNProperties keywordsMap = new SVNProperties();
    assertEquals("No change", new KeywordHandler(keywordsMap, null).substitute("No change", "UTF-8"));
  }

}
