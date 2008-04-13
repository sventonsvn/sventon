package de.berlios.sventon.repository;

import de.berlios.sventon.TestUtils;
import junit.framework.TestCase;

import java.util.List;

public class RepositoryEntryKindFilterTest extends TestCase {

  public void testFilter() throws Exception {

    final List<RepositoryEntry> list = TestUtils.getDirectoryList();
    List<RepositoryEntry> filteredList;

    try {
      new RepositoryEntryKindFilter(RepositoryEntry.Kind.none).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    try {
      new RepositoryEntryKindFilter(RepositoryEntry.Kind.any).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    try {
      new RepositoryEntryKindFilter(RepositoryEntry.Kind.unknown).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    filteredList = new RepositoryEntryKindFilter(RepositoryEntry.Kind.dir).filter(list);
    assertEquals(4, filteredList.size());

    filteredList = new RepositoryEntryKindFilter(RepositoryEntry.Kind.file).filter(list);
    assertEquals(9, filteredList.size());

    try {
      new RepositoryEntryKindFilter(null).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }
  }
}