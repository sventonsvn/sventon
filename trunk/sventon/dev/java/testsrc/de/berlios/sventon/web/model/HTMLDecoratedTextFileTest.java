package de.berlios.sventon.web.model;

import de.berlios.sventon.colorer.Colorer;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HTMLDecoratedTextFileTest extends TestCase {

  public void testHTMLDecoratedTextFile() throws Exception {
    final Colorer colorer = new Colorer() {
      public String getColorizedContent(final String content, final String fileExtension) throws IOException {
        return content;
      }
    };
    final String content = "test file content";
    final HTMLDecoratedTextFile file = new HTMLDecoratedTextFile(content, new HashMap(), "http://localhost/", "/test.file", colorer);
    final Map<String, Object> model = file.getModel();
    assertFalse((Boolean) model.get("isRawFormat"));
    assertEquals("<span class=\"sventonLineNo\">    1:&nbsp;</span>" + content, file.getContent());
  }
}