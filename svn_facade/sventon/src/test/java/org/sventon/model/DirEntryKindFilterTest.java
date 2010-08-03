package org.sventon.model;

import junit.framework.TestCase;
import org.sventon.TestUtils;

import java.util.List;

public class DirEntryKindFilterTest extends TestCase {

  public void testFilter() throws Exception {

    final List<RepositoryEntry> list = TestUtils.getDirectoryList();
    List<RepositoryEntry> filteredList;

    try {
      new DirEntryKindFilter(RepositoryEntry.Kind.NONE).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    try {
      new DirEntryKindFilter(RepositoryEntry.Kind.ANY).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    try {
      new DirEntryKindFilter(RepositoryEntry.Kind.UNKNOWN).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    filteredList = new DirEntryKindFilter(RepositoryEntry.Kind.DIR).filter(list);
    assertEquals(4, filteredList.size());

    filteredList = new DirEntryKindFilter(RepositoryEntry.Kind.FILE).filter(list);
    assertEquals(9, filteredList.size());

    try {
      new DirEntryKindFilter(null).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }
  }
}