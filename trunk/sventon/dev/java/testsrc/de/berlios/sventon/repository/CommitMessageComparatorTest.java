package de.berlios.sventon.repository;

import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class CommitMessageComparatorTest extends TestCase {

  public void testCompare() throws Exception {
    List<CommitMessage> entries = new ArrayList<CommitMessage>();
    CommitMessage c1 = new CommitMessage(1, "Message 1");
    CommitMessage c2 = new CommitMessage(12, "Message 2");
    CommitMessage c3 = new CommitMessage(123, "Message 3");

    entries.add(c3);
    entries.add(c2);
    entries.add(c1);

    assertSame(c3, entries.get(0));

    Collections.sort(entries, new CommitMessageComparator(CommitMessageComparator.ASCENDING));
    assertSame(c1, entries.get(0));
    assertSame(c2, entries.get(1));
    assertSame(c3, entries.get(2));

    Collections.sort(entries, new CommitMessageComparator(CommitMessageComparator.DESCENDING));
    assertSame(c1, entries.get(2));
    assertSame(c2, entries.get(1));
    assertSame(c3, entries.get(0));

    try {
      new CommitMessageComparator(6);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException iae) {
      // Expected
    }

    // null values are not OK
    CommitMessageComparator comparator = new CommitMessageComparator(CommitMessageComparator.ASCENDING);
    try {
      comparator.compare(null, null);
      fail("NullPointerException expected");
    } catch (NullPointerException npe) {
      // Expected
    }
  }

}