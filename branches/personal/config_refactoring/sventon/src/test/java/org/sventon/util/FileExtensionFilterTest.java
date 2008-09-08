package org.sventon.util;

import org.sventon.model.RepositoryEntry;
import org.sventon.TestUtils;
import junit.framework.TestCase;

import java.util.List;

public class FileExtensionFilterTest extends TestCase {

  public void testFilter() throws Exception {

    final List<RepositoryEntry> list = TestUtils.getFileEntriesDirectoryList();

    List<RepositoryEntry> filteredList;
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