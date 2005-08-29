package de.berlios.sventon.svnsupport;

import de.berlios.sventon.ctrl.RepositoryEntry;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNDirEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import static de.berlios.sventon.svnsupport.RepositoryEntryComparator.AUTHOR;
import static de.berlios.sventon.svnsupport.RepositoryEntryComparator.DATE;
import static de.berlios.sventon.svnsupport.RepositoryEntryComparator.NAME;
import static de.berlios.sventon.svnsupport.RepositoryEntryComparator.REVISION;
import static org.tmatesoft.svn.core.SVNNodeKind.DIR;
import static org.tmatesoft.svn.core.SVNNodeKind.FILE;

public class RepositoryEntryComparatorTest extends TestCase {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(RepositoryEntryComparatorTest.class);
  }

  public RepositoryEntryComparatorTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public final void testCompare() {
    List<RepositoryEntry> entries = new ArrayList<RepositoryEntry>();
    RepositoryEntry e1 = new RepositoryEntry(new SVNDirEntry("FirstClass.java", FILE, 134, false, 2, new GregorianCalendar(2005, 4, 12)
        .getTime(), "patrikfr"), "");
    RepositoryEntry e2 = new RepositoryEntry(new SVNDirEntry("SecondClass.java", FILE, 135, false, 3, new GregorianCalendar(2005, 4, 13)
        .getTime(), "jesper"), "");
    RepositoryEntry e3 = new RepositoryEntry(new SVNDirEntry("ThirdClass.java", DIR, 136, false, 4, new GregorianCalendar(2005, 4, 14)
        .getTime(), "patrikfr"), "");
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

    // Tricking the constructor with an illegal type should fail fast
    try {
      new RepositoryEntryComparator(4, false);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException iae) {
      // Expected
    }

    // null values are not OK
    RepositoryEntryComparator comparator = new RepositoryEntryComparator(RepositoryEntryComparator.DATE, false);
    try {
      comparator.compare(null, null);
      fail("NullPointerException expected");
    } catch (NullPointerException npe) {
      // Expected
    }

    // Test handling of null properties in SVNDirEntry
    entries = new ArrayList<RepositoryEntry>();
    e1 = new RepositoryEntry(new SVNDirEntry("FirstClass.java", FILE, 134, false, 2, new GregorianCalendar(2005, 4, 12).getTime(),
        "patrikfr"), "");
    e2 = new RepositoryEntry(new SVNDirEntry(null, FILE, 135, false, 3, new GregorianCalendar(2005, 4, 13).getTime(),
        "jesper"), "");
    e3 = new RepositoryEntry(new SVNDirEntry("ThirdClass.java", DIR, 136, false, 4, new GregorianCalendar(2005, 4, 14).getTime(),
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
