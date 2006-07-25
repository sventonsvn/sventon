package de.berlios.sventon.web.ctrl.xml;

import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.repository.LogMessage;
import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.SVNRepositoryStub;
import de.berlios.sventon.repository.cache.CacheException;
import de.berlios.sventon.repository.cache.Cache;
import de.berlios.sventon.repository.cache.CamelCasePattern;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class CompletionControllerTest extends TestCase {

  public static final String XML_RESPONSE =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
          "<items>\r\n" +
          "  <item>/file1.java</item>\r\n" +
          "  <item>/file2.html</item>\r\n" +
          "  <item>/File3.java</item>\r\n" +
          "</items>";

  public void testSvnHandle() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    CompletionController ctrl = new CompletionController();
    ctrl.setEncoding("UTF-8");
    ctrl.setCache(new TestCache());

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter("complete", "file");
    request.addParameter("startDir", "/");

    MockHttpServletResponse response = new MockHttpServletResponse();
    ctrl.svnHandle(new TestRepository(), command, SVNRevision.HEAD, request, response, null);

    assertEquals(XML_RESPONSE, response.getContentAsString().trim());

    DocumentBuilder parser =
        DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document document = parser.parse(new InputSource(new StringReader(response.getContentAsString())));
    NodeList nodeList = document.getFirstChild().getChildNodes();

    for (int i = 0; i < nodeList.getLength(); i++) {
      if (!"".equals(nodeList.item(i).getTextContent().trim())) {
        System.out.println(nodeList.item(i).getTextContent().trim());
      }
    }
  }

  class TestRepository extends SVNRepositoryStub {
    public TestRepository() throws SVNException {
      super(SVNURL.parseURIDecoded("http://localhost/"), null);
    }
  }

  class TestCache implements Cache {

    public List<RepositoryEntry> findEntry(final String searchString) throws CacheException {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<RepositoryEntry> findEntryByCamelCase(final CamelCasePattern pattern, final String startDir) throws CacheException {
      return null;
    }

    public List<RepositoryEntry> findEntry(final String searchString, final String startDir) throws CacheException {
      return null;
    }

    public List<RepositoryEntry> findEntry(final String searchString, final String startDir, final Integer limit) throws CacheException {
      final List<RepositoryEntry> entries = new ArrayList<RepositoryEntry>();
      entries.add(new RepositoryEntry(new SVNDirEntry(null, "file1.java", SVNNodeKind.FILE, 64000, false, 1, new Date(), "jesper"), "/", null));
      entries.add(new RepositoryEntry(new SVNDirEntry(null, "file2.html", SVNNodeKind.FILE, 32000, false, 2, new Date(), "jesper"), "/", null));
      entries.add(new RepositoryEntry(new SVNDirEntry(null, "File3.java", SVNNodeKind.FILE, 16000, false, 3, new Date(), "jesper"), "/", null));
      return entries;
    }

    public List<RepositoryEntry> findDirectories(final String fromPath) throws CacheException {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<LogMessage> find(final String queryString) throws CacheException {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public SVNLogEntry getRevision(final long revision) throws CacheException {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

  }
}