package de.berlios.sventon.web.model;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

public class TextFileTest extends TestCase {

  public void testTextFile() throws Exception {
    final String content = "test file content";
    final TextFile file = new TextFile(content, new HashMap(), "http://localhost/", "/test.file");
    final Map<String, Object> model = file.getModel();
    assertFalse((Boolean) model.get("isRawFormat"));
    assertEquals(content, file.getContent());
  }
}
