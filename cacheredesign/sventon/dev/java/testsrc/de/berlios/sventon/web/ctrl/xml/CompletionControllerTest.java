package de.berlios.sventon.web.ctrl.xml;

import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.repository.RepositoryConfiguration;
import de.berlios.sventon.repository.SVNRepositoryStub;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class CompletionControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    CompletionController ctrl = new CompletionController();

    MockHttpServletRequest req = new MockHttpServletRequest();
    req.addParameter("complete", "file");
    req.addParameter("startDir", "");

    MockHttpServletResponse res = new MockHttpServletResponse();

    SVNRepository repos = new TestRepository();
//    RevisionIndexer indexer = new RevisionIndexer(repos);
    RepositoryConfiguration config = new RepositoryConfiguration();
    config.setRepositoryRoot(repos.getLocation().toString());
    config.setSVNConfigurationPath(System.getProperty("java.io.tmpdir"));
    config.setCacheUsed(true);
//    indexer.setRepositoryConfiguration(config);
    //ctrl.setRevisionIndexer(indexer);
    ctrl.svnHandle(repos, command, SVNRevision.HEAD, req, res, null);

    DocumentBuilder parser =
        DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document document = parser.parse(new InputSource(new StringReader(res.getContentAsString())));
    NodeList nodeList = document.getFirstChild().getChildNodes();

    for (int i = 0; i < nodeList.getLength(); i++) {
      if (!"".equals(nodeList.item(i).getTextContent().trim())) {
        System.out.println(nodeList.item(i).getTextContent().trim());
      }
    }

    req = new MockHttpServletRequest();
    try {
      ctrl.svnHandle(repos, command, SVNRevision.HEAD, req, null, null);
      fail("Should throw IAE");
    } catch (IllegalArgumentException ex) {
      // expected
    }
  }

  protected void tearDown() throws Exception {
//    File tempIndex = new File(System.getProperty("java.io.tmpdir") + "/" + RevisionIndexer.INDEX_FILENAME);
//    if (tempIndex.exists()) {
//      tempIndex.delete();
//    }
  }

  class TestRepository extends SVNRepositoryStub {
    public TestRepository() throws SVNException {
      super(SVNURL.parseURIDecoded("http://localhost/"), null);
    }
  }

}