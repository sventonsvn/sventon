package org.sventon.model;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import static org.sventon.model.DirEntryComparator.SortType.*;

public class DirEntryComparatorTest extends TestCase {

  public void testCompare() throws Exception {
    List<DirEntry> entries = new ArrayList<DirEntry>();
    DirEntry e1 = new DirEntry("", "FirstClass.java", "patrik", new GregorianCalendar(2005, 4, 12).getTime(), DirEntry.Kind.FILE, 2, 134);
    DirEntry e2 = new DirEntry("", "SecondClass.java", "jesper",  new GregorianCalendar(2005, 4, 13).getTime(), DirEntry.Kind.FILE, 3, 135);
    DirEntry e3 = new DirEntry("", "ThirdClass.java", "patrik", new GregorianCalendar(2005, 4, 14).getTime(), DirEntry.Kind.DIR, 4, 136);
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

    // Test handling of null properties in DirEntry
    entries = new ArrayList<DirEntry>();
    e1 = new DirEntry("", "FirstClass.java",  "patrik", new GregorianCalendar(2005, 4, 12).getTime(), DirEntry.Kind.FILE, 2, 134);
    e2 = new DirEntry("", "", "jesper", new GregorianCalendar(2005, 4, 13).getTime(), DirEntry.Kind.FILE, 3, 135);
    e3 = new DirEntry("", "ThirdClass.java", "patrik", new GregorianCalendar(2005, 4, 14).getTime(), DirEntry.Kind.DIR, 4, 136);
    entries.add(e3);
    entries.add(e2);
    entries.add(e1);

    Collections.sort(entries, new DirEntryComparator(NAME, false));
    assertSame(e1, entries.get(1));
    assertSame(e2, entries.get(0));
    assertSame(e3, entries.get(2));
  }

}
