package org.sventon.util;

import junit.framework.TestCase;
import org.sventon.TestUtils;
import org.sventon.model.DirEntry;

import java.util.List;

public class FileExtensionFilterTest extends TestCase {

  public void testFilter() throws Exception {

    final List<DirEntry> list = TestUtils.getFileEntriesDirectoryList();

    List<DirEntry> filteredList;
    filteredList = new FileExtensionFilter("jpg").filter(list);
    assertEquals(1, filteredList.size());

    filteredList = new FileExtensionFilter("gif").filter(list);
    assertEquals(1, filteredList.size());

    try {
      new FileExtensionFilter(null).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    try {
      new FileExtensionFilter("").filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }
  }

}