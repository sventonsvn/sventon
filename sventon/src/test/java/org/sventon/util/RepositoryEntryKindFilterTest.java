package org.sventon.util;

import junit.framework.TestCase;
import org.sventon.TestUtils;
import org.sventon.model.RepositoryEntry;

import java.util.List;

public class RepositoryEntryKindFilterTest extends TestCase {

  public void testFilter() throws Exception {

    final List<RepositoryEntry> list = TestUtils.getDirectoryList();
    List<RepositoryEntry> filteredList;

    try {
      new RepositoryEntryKindFilter(RepositoryEntry.Kind.NONE).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    try {
      new RepositoryEntryKindFilter(RepositoryEntry.Kind.ANY).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    try {
      new RepositoryEntryKindFilter(RepositoryEntry.Kind.UNKNOWN).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    filteredList = new RepositoryEntryKindFilter(RepositoryEntry.Kind.DIR).filter(list);
    assertEquals(4, filteredList.size());

    filteredList = new RepositoryEntryKindFilter(RepositoryEntry.Kind.FILE).filter(list);
    assertEquals(9, filteredList.size());

    try {
      new RepositoryEntryKindFilter(null).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }
  }
}