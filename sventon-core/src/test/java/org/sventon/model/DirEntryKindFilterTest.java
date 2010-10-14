package org.sventon.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DirEntryKindFilterTest {

  private static final String AUTHOR_JESPER = "jesper";
  private static final String AUTHOR_PATRIK = "patrik";

  @Test
  public void testFilter() throws Exception {

    final List<DirEntry> list = getDirectoryList();
    List<DirEntry> filteredList;

    try {
      new DirEntryKindFilter(DirEntry.Kind.NONE).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    try {
      new DirEntryKindFilter(DirEntry.Kind.ANY).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    try {
      new DirEntryKindFilter(DirEntry.Kind.UNKNOWN).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    filteredList = new DirEntryKindFilter(DirEntry.Kind.DIR).filter(list);
    assertEquals(4, filteredList.size());

    filteredList = new DirEntryKindFilter(DirEntry.Kind.FILE).filter(list);
    assertEquals(9, filteredList.size());

    try {
      new DirEntryKindFilter(null).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }
  }

  private static List<DirEntry> getDirectoryList() {
    final List<DirEntry> entries = new ArrayList<DirEntry>();
    entries.add(new DirEntry("/", "trunk", AUTHOR_JESPER, new Date(), DirEntry.Kind.DIR, 1, 0));
    entries.add(new DirEntry("/", "TAGS", AUTHOR_JESPER, new Date(), DirEntry.Kind.DIR, 1, 0));
    entries.add(new DirEntry("/", "file1.java", AUTHOR_JESPER, new Date(), DirEntry.Kind.FILE, 1, 64000));
    entries.add(new DirEntry("/", "file2.html", AUTHOR_JESPER, new Date(), DirEntry.Kind.FILE, 2, 32000));

    entries.add(new DirEntry("/TAGS/", "tagfile.txt", AUTHOR_JESPER, new Date(), DirEntry.Kind.FILE, 2, 3200));
    entries.add(new DirEntry("/TAGS/", "test.txt", AUTHOR_JESPER, new Date(), DirEntry.Kind.FILE, 3, 1600));

    entries.add(new DirEntry("/trunk/", "code", AUTHOR_JESPER, new Date(), DirEntry.Kind.DIR, 1, 0));
    entries.add(new DirEntry("/trunk/", "src", AUTHOR_JESPER, new Date(), DirEntry.Kind.DIR, 1, 0));
    entries.add(new DirEntry("/trunk/", "DirFile3.java", AUTHOR_JESPER, new Date(), DirEntry.Kind.FILE, 3, 1600));
    entries.add(new DirEntry("/trunk/", "file1_in_trunk.java", AUTHOR_PATRIK, new Date(), DirEntry.Kind.FILE, 1, 6400));
    entries.add(new DirEntry("/trunk/", "TestDirFile3.java", AUTHOR_PATRIK, new Date(), DirEntry.Kind.FILE, 3, 1600));

    entries.add(new DirEntry("/trunk/code/", "File3.java", AUTHOR_JESPER, new Date(), DirEntry.Kind.FILE, 3, 16000));
    entries.add(new DirEntry("/trunk/src/", "DirFile2.html", AUTHOR_JESPER, new Date(), DirEntry.Kind.FILE, 2, 3200));

    return entries;
  }

}