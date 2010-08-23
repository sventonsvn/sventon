package org.sventon.model;

import junit.framework.TestCase;
import org.sventon.TestUtils;

import java.util.ArrayList;
import java.util.List;

public class FileExtensionListTest extends TestCase {

  public void testGetExtensions() throws Exception {
    assertEquals(0, new FileExtensionList(new ArrayList<DirEntry>()).getExtensions().size());

    final List<DirEntry> entries = TestUtils.getFileEntriesDirectoryList();

    assertEquals(3, new FileExtensionList(entries).getExtensions().size());

    entries.add(new DirEntry("/", "anothertest.jpg", "", null, DirEntry.Kind.FILE, 0, 0));

    assertEquals(3, new FileExtensionList(entries).getExtensions().size());
  }
}