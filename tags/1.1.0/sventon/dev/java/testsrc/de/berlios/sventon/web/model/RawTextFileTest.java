package de.berlios.sventon.web.model;

import junit.framework.TestCase;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.Map;

public class RawTextFileTest extends TestCase {

  public void testRawTextFile() throws Exception {
    final String content = "test file content";
    final RawTextFile file = new RawTextFile(content);
    final Map<String, Object> model = file.getModel();
    assertEquals(2, model.size());
    assertEquals(content, model.get("fileContent"));
    assertEquals(content, file.getContent());
    assertTrue((Boolean) model.get("isRawFormat"));

    assertEquals(StringEscapeUtils.escapeHtml(content), new RawTextFile(content, true).getContent());
  }
}