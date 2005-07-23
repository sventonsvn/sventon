package de.berlios.sventon.svnsupport;

import junit.framework.TestCase;
import org.tmatesoft.svn.core.io.SVNDirEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import static org.tmatesoft.svn.core.io.SVNNodeKind.FILE;
import static org.tmatesoft.svn.core.io.SVNNodeKind.DIR;
import static de.berlios.sventon.svnsupport.SVNDirEntryComparator.AUTHOR;
import static de.berlios.sventon.svnsupport.SVNDirEntryComparator.DATE;
import static de.berlios.sventon.svnsupport.SVNDirEntryComparator.NAME;
import static de.berlios.sventon.svnsupport.SVNDirEntryComparator.REVISION;

public class SVNDirEntryComparatorTest extends TestCase {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(SVNDirEntryComparatorTest.class);
  }

  public SVNDirEntryComparatorTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public final void testCompare() {
    List<SVNDirEntry> entries = new ArrayList<SVNDirEntry>();
    SVNDirEntry e1 = new SVNDirEntry("FirstClass.java", FILE, 134, false, 2, new GregorianCalendar(2005, 4, 12)
        .getTime(), "patrikfr");
    SVNDirEntry e2 = new SVNDirEntry("SecondClass.java", FILE, 135, false, 3, new GregorianCalendar(2005, 4, 13)
        .getTime(), "jesper");
    SVNDirEntry e3 = new SVNDirEntry("ThirdClass.java", DIR, 136, false, 4, new GregorianCalendar(2005, 4, 14)
        .getTime(), "patrikfr");
    entries.add(e3);
    entries.add(e2);
    entries.add(e1);

    assertSame(e3, entries.get(0));

    Collections.sort(entries, new SVNDirEntryComparator(NAME, false));
    assertSame(e1, entries.get(0));
    assertSame(e2, entries.get(1));
    assertSame(e3, entries.get(2));

    Collections.sort(entries, new SVNDirEntryComparator(NAME, true));
    assertSame(e1, entries.get(1));
    assertSame(e2, entries.get(2));
    assertSame(e3, entries.get(0));

    Collections.sort(entries, new SVNDirEntryComparator(AUTHOR, false));
    assertSame(e1, entries.get(1));
    assertSame(e2, entries.get(0));
    assertSame(e3, entries.get(2));

    Collections.sort(entries, new SVNDirEntryComparator(AUTHOR, true));
    assertSame(e1, entries.get(2));
    assertSame(e2, entries.get(1));
    assertSame(e3, entries.get(0));

    Collections.sort(entries, new SVNDirEntryComparator(REVISION, false));
    assertSame(e1, entries.get(0));
    assertSame(e2, entries.get(1));
    assertSame(e3, entries.get(2));

    Collections.sort(entries, new SVNDirEntryComparator(DATE, false));
    assertSame(e1, entries.get(0));
    assertSame(e2, entries.get(1));
    assertSame(e3, entries.get(2));

    // Tricking the constructor with an illegal type should fail fast
    try {
      new SVNDirEntryComparator(4, false);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException iae) {
      // Expected
    }

    // null values are not OK
    SVNDirEntryComparator comparator = new SVNDirEntryComparator(SVNDirEntryComparator.DATE, false);
    try {
      comparator.compare(null, null);
      fail("NullPointerException expected");
    } catch (NullPointerException npe) {
      // Expected
    }

    // Test handling of null properties in SVNDirEntry
    entries = new ArrayList<SVNDirEntry>();
    e1 = new SVNDirEntry("FirstClass.java", FILE, 134, false, 2, new GregorianCalendar(2005, 4, 12).getTime(),
        "patrikfr");
    e2 = new SVNDirEntry(null, FILE, 135, false, 3, new GregorianCalendar(2005, 4, 13).getTime(),
        "jesper");
    e3 = new SVNDirEntry("ThirdClass.java", DIR, 136, false, 4, new GregorianCalendar(2005, 4, 14).getTime(),
        "patrikfr");
    entries.add(e3);
    entries.add(e2);
    entries.add(e1);
    
    Collections.sort(entries, new SVNDirEntryComparator(NAME, false));
    assertSame(e1, entries.get(1));
    assertSame(e2, entries.get(0));
    assertSame(e3, entries.get(2));
  }

}
