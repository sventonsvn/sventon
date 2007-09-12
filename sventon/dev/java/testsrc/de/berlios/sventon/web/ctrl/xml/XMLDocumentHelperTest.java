package de.berlios.sventon.web.ctrl.xml;

import junit.framework.TestCase;
import org.jdom.Document;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class XMLDocumentHelperTest extends TestCase {

  public void testCreateXML() throws Exception {

    final String datePattern = "yyyy-MM-dd";
    final SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
    final Date now = new Date();

    final String ref =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
            "<latestcommitinfo>\r\n" +
            "  <revision>123</revision>\r\n" +
            "  <author>jesper</author>\r\n" +
            "  <date>" + sdf.format(now) + "</date>\r\n" +
            "  <message>Message</message>\r\n" +
            "  <entries>\r\n" +
            "    <entry>\r\n" +
            "      <path>/file1.java</path>\r\n" +
            "      <type>M</type>\r\n" +
            "      <copypath />\r\n" +
            "      <copyrevision />\r\n" +
            "    </entry>\r\n" +
            "  </entries>\r\n" +
            "</latestcommitinfo>\r\n\r\n";

    final Map<String, SVNLogEntryPath> changedPaths1 = new HashMap<String, SVNLogEntryPath>();
    changedPaths1.put("/file1.java", new SVNLogEntryPath("/file1.java", 'M', null, 1));
    SVNLogEntry logEntry = new SVNLogEntry(changedPaths1, 123, "jesper", now, "Message");

    final Document doc = XMLDocumentHelper.createXML(logEntry, datePattern);
    assertNotNull(doc);

    assertEquals(ref, XMLDocumentHelper.getAsString(doc, "UTF-8"));
  }
}