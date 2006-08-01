package de.berlios.sventon.web.model;

import junit.framework.TestCase;

import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;

public class ArchiveFileTest extends TestCase {

  public void testArchiveFile() throws Exception {
    // A zip file containing one empty file called 'a'.
    byte[] content = new byte[]{
        80, 75, 3, 4, 20, 0, 2, 0, 8, 0, 17, 12, 2, 53, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 97, 3,
        0, 80, 75, 1, 2, 20, 0, 20, 0, 2, 0, 8, 0, 17, 12, 2, 53, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 97, 80, 75, 5, 6, 0, 0, 0, 0, 1, 0, 1, 0, 47, 0, 0, 0, 33,
        0, 0, 0, 0, 0
    };
    final ArchiveFile file = new ArchiveFile(content);
    Map<String, Object> model = file.getModel();
    assertEquals(1, model.size());
    assertEquals(1, ((List<ZipEntry>) model.get("entries")).size());
    assertNull(model.get("fileContent"));

    try {
      file.getContent();
      fail("Should throw UnsupportedOperationException");
    } catch (UnsupportedOperationException uoe) {
      // expected
    }
  }
}