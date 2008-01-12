package de.berlios.sventon.web.support;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.tmatesoft.svn.core.io.SVNFileRevision;

import java.util.List;

public class RequestParameterParserTest extends TestCase {

  public void testParseEntriesWithDelimitersInPath() throws Exception {
    final String[] parameters = new String[]{
        "/trunk/test;;file.java;;123",
    };
    final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addParameter("entry", parameters);
    final RequestParameterParser requestParameterParser = new RequestParameterParser();
    final List<SVNFileRevision> entries = requestParameterParser.parseEntries(mockRequest);
    assertEquals(1, entries.size());

    final SVNFileRevision entry1 = entries.get(0);
    assertEquals("/trunk/test;;file.java", entry1.getPath());
    assertEquals(123, entry1.getRevision());
  }

  public void testParseEntries() throws Exception {
    final String[] parameters = new String[]{
        "/trunk/test.java;;2",
        "/trunk/test.java;;1"
    };
    final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addParameter("entry", parameters);
    final RequestParameterParser requestParameterParser = new RequestParameterParser();
    final List<SVNFileRevision> entries = requestParameterParser.parseEntries(mockRequest);
    assertEquals(2, entries.size());

    final SVNFileRevision entry1 = entries.get(0);
    assertEquals("/trunk/test.java", entry1.getPath());
    assertEquals(2, entry1.getRevision());

    final SVNFileRevision entry2 = entries.get(1);
    assertEquals("/trunk/test.java", entry2.getPath());
    assertEquals(1, entry2.getRevision());
  }
}