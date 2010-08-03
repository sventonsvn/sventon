package org.sventon.util;

import junit.framework.TestCase;
import org.sventon.model.RepositoryEntry;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNURL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import static org.sventon.util.RepositoryEntryComparator.SortType.*;
import static org.tmatesoft.svn.core.SVNNodeKind.DIR;
import static org.tmatesoft.svn.core.SVNNodeKind.FILE;

public class RepositoryEntryComparatorTest extends TestCase {

  public void testCompare() throws Exception {
    List<RepositoryEntry> entries = new ArrayList<RepositoryEntry>();
    RepositoryEntry e1 = new RepositoryEntry(new SVNDirEntry(null, null, "FirstClass.java", FILE, 134, false, 2,
        new GregorianCalendar(2005, 4, 12).getTime(), "patrikfr"), "");
    RepositoryEntry e2 = new RepositoryEntry(new SVNDirEntry(SVNURL.parseURIEncoded("http://test"), null,
        "SecondClass.java", FILE, 135, false, 3, new GregorianCalendar(2005, 4, 13).getTime(), "jesper"), "");
    RepositoryEntry e3 = new RepositoryEntry(new SVNDirEntry(null, null, "ThirdClass.java", DIR, 136, false, 4,
        new GregorianCalendar(2005, 4, 14).getTime(), "patrikfr"), "");
    entries.add(e3);
    entries.add(e2);
    entries.add(e1);

    assertSame(e3, entries.get(0));

    Collections.sort(entries, new RepositoryEntryComparator(NAME, false));
    assertSame(e1, entries.get(0));
    assertSame(e2, entries.get(1));
    assertSame(e3, entries.get(2));

    Collections.sort(entries, new RepositoryEntryComparator(NAME, true));
    assertSame(e1, entries.get(1));
    assertSame(e2, entries.get(2));
    assertSame(e3, entries.get(0));

    Collections.sort(entries, new RepositoryEntryComparator(AUTHOR, false));
    assertSame(e1, entries.get(1));
    assertSame(e2, entries.get(0));
    assertSame(e3, entries.get(2));

    Collections.sort(entries, new RepositoryEntryComparator(AUTHOR, true));
    assertSame(e1, entries.get(2));
    assertSame(e2, entries.get(1));
    assertSame(e3, entries.get(0));

    Collections.sort(entries, new RepositoryEntryComparator(REVISION, false));
    assertSame(e1, entries.get(0));
    assertSame(e2, entries.get(1));
    assertSame(e3, entries.get(2));

    Collections.sort(entries, new RepositoryEntryComparator(DATE, false));
    assertSame(e1, entries.get(0));
    assertSame(e2, entries.get(1));
    assertSame(e3, entries.get(2));

    Collections.sort(entries, new RepositoryEntryComparator(FULL_NAME, false));
    assertSame(e1, entries.get(0));
    assertSame(e2, entries.get(1));
    assertSame(e3, entries.get(2));

    Collections.sort(entries, new RepositoryEntryComparator(SIZE, false));
    assertSame(e1, entries.get(0));
    assertSame(e2, entries.get(1));
    assertSame(e3, entries.get(2));

    // Tricking the constructor with an illegal type should fail fast
    try {
      new RepositoryEntryComparator(RepositoryEntryComparator.SortType.valueOf("test"), false);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException iae) {
      // Expected
    }

    // null values are not OK
    RepositoryEntryComparator comparator = new RepositoryEntryComparator(DATE, false);
    try {
      comparator.compare(null, null);
      fail("NullPointerException expected");
    } catch (NullPointerException npe) {
      // Expected
    }

    // Test handling of null properties in SVNDirEntry
    entries = new ArrayList<RepositoryEntry>();
    e1 = new RepositoryEntry(new SVNDirEntry(null, null, "FirstClass.java", FILE, 134, false, 2, new GregorianCalendar(2005, 4, 12).getTime(),
        "patrikfr"), "");
    e2 = new RepositoryEntry(new SVNDirEntry(null, null, "", FILE, 135, false, 3, new GregorianCalendar(2005, 4, 13).getTime(),
        "jesper"), "");
    e3 = new RepositoryEntry(new SVNDirEntry(null, null, "ThirdClass.java", DIR, 136, false, 4, new GregorianCalendar(2005, 4, 14).getTime(),
        "patrikfr"), "");
    entries.add(e3);
    entries.add(e2);
    entries.add(e1);

    Collections.sort(entries, new RepositoryEntryComparator(NAME, false));
    assertSame(e1, entries.get(1));
    assertSame(e2, entries.get(0));
    assertSame(e3, entries.get(2));
  }

}
