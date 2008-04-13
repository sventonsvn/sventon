package de.berlios.sventon.web.ctrl.xml;

import de.berlios.sventon.TestUtils;
import junit.framework.TestCase;
import org.jdom.Document;

import java.text.SimpleDateFormat;
import java.util.Date;

public class XMLDocumentHelperTest extends TestCase {

  public void testCreateXML() throws Exception {

    final String datePattern = "yyyy-MM-dd";
    final SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
    final Date now = new Date();

    final String ref =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
            "<latestcommitinfo>\r\n" +
            "  <revision>123</revision>\r\n" +
            "  <author>TestAuthor</author>\r\n" +
            "  <date>" + sdf.format(now) + "</date>\r\n" +
            "  <message>TestMessage</message>\r\n" +
            "  <entries>\r\n" +
            "    <entry>\r\n" +
            "      <path>/file1.java</path>\r\n" +
            "      <type>M</type>\r\n" +
            "      <copypath />\r\n" +
            "      <copyrevision />\r\n" +
            "    </entry>\r\n" +
            "  </entries>\r\n" +
            "</latestcommitinfo>\r\n\r\n";

    final Document doc = XMLDocumentHelper.createXML(TestUtils.getLogEntryStub(now), datePattern);
    assertNotNull(doc);

    assertEquals(ref, XMLDocumentHelper.getAsString(doc, "UTF-8"));
  }
}