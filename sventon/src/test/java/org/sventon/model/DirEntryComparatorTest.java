package org.sventon.model;

import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNURL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import static org.sventon.model.DirEntryComparator.SortType.*;
import static org.tmatesoft.svn.core.SVNNodeKind.DIR;
import static org.tmatesoft.svn.core.SVNNodeKind.FILE;

public class DirEntryComparatorTest extends TestCase {

  public void testCompare() throws Exception {
    List<DirEntry> entries = new ArrayList<DirEntry>();
    DirEntry e1 = new DirEntry(new SVNDirEntry(null, null, "FirstClass.java", FILE, 134, false, 2,
        new GregorianCalendar(2005, 4, 12).getTime(), "patrik"), "");
    DirEntry e2 = new DirEntry(new SVNDirEntry(SVNURL.parseURIEncoded("http://test"), null,
        "SecondClass.java", FILE, 135, false, 3, new GregorianCalendar(2005, 4, 13).getTime(), "jesper"), "");
    DirEntry e3 = new DirEntry(new SVNDirEntry(null, null, "ThirdClass.java", DIR, 136, false, 4,
        new GregorianCalendar(2005, 4, 14).getTime(), "patrik"), "");
    entries.add(e3);
    entries.add(e2);
    entries.add(e1);

    assertSame(e3, entries.get(0));

    Collections.sort(entries, new DirEntryComparator(NAME, false));
    assertSame(e1, entries.get(0));
    assertSame(e2, entries.get(1));
    assertSame(e3, entries.get(2));

    Collections.sort(entries, new DirEntryComparator(NAME, true));
    assertSame(e1, entries.get(1));
    assertSame(e2, entries.get(2));
    assertSame(e3, entries.get(0));

    Collections.sort(entries, new DirEntryComparator(AUTHOR, false));
    assertSame(e1, entries.get(1));
    assertSame(e2, entries.get(0));
    assertSame(e3, entries.get(2));

    Collections.sort(entries, new DirEntryComparator(AUTHOR, true));
    assertSame(e1, entries.get(2));
    assertSame(e2, entries.get(1));
    assertSame(e3, entries.get(0));

    Collections.sort(entries, new DirEntryComparator(REVISION, false));
    assertSame(e1, entries.get(0));
    assertSame(e2, entries.get(1));
    assertSame(e3, entries.get(2));

    Collections.sort(entries, new DirEntryComparator(DATE, false));
    assertSame(e1, entries.get(0));
    assertSame(e2, entries.get(1));
    assertSame(e3, entries.get(2));

    Collections.sort(entries, new DirEntryComparator(FULL_NAME, false));
    assertSame(e1, entries.get(0));
    assertSame(e2, entries.get(1));
    assertSame(e3, entries.get(2));

    Collections.sort(entries, new DirEntryComparator(SIZE, false));
    assertSame(e1, entries.get(0));
    assertSame(e2, entries.get(1));
    assertSame(e3, entries.get(2));

    // Tricking the constructor with an illegal type should fail fast
    try {
      new DirEntryComparator(DirEntryComparator.SortType.valueOf("test"), false);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException iae) {
      // Expected
    }

    // null values are not OK
    DirEntryComparator comparator = new DirEntryComparator(DATE, false);
    try {
      comparator.compare(null, null);
      fail("NullPointerException expected");
    } catch (NullPointerException npe) {
      // Expected
    }

    // Test handling of null properties in SVNDirEntry
    entries = new ArrayList<DirEntry>();
    e1 = new DirEntry(new SVNDirEntry(null, null, "FirstClass.java", FILE, 134, false, 2, new GregorianCalendar(2005, 4, 12).getTime(),
        "patrik"), "");
    e2 = new DirEntry(new SVNDirEntry(null, null, "", FILE, 135, false, 3, new GregorianCalendar(2005, 4, 13).getTime(),
        "jesper"), "");
    e3 = new DirEntry(new SVNDirEntry(null, null, "ThirdClass.java", DIR, 136, false, 4, new GregorianCalendar(2005, 4, 14).getTime(),
        "patrik"), "");
    entries.add(e3);
    entries.add(e2);
    entries.add(e1);

    Collections.sort(entries, new DirEntryComparator(NAME, false));
    assertSame(e1, entries.get(1));
    assertSame(e2, entries.get(0));
    assertSame(e3, entries.get(2));
  }

}
