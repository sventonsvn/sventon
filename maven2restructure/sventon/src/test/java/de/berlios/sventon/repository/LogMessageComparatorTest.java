package de.berlios.sventon.repository;

import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class LogMessageComparatorTest extends TestCase {

  public void testCompare() throws Exception {
    List<LogMessage> entries = new ArrayList<LogMessage>();
    LogMessage c1 = new LogMessage(1, "Message 1");
    LogMessage c2 = new LogMessage(12, "Message 2");
    LogMessage c3 = new LogMessage(123, "Message 3");

    entries.add(c3);
    entries.add(c2);
    entries.add(c1);

    assertSame(c3, entries.get(0));

    Collections.sort(entries, new LogMessageComparator(LogMessageComparator.ASCENDING));
    assertSame(c1, entries.get(0));
    assertSame(c2, entries.get(1));
    assertSame(c3, entries.get(2));

    Collections.sort(entries, new LogMessageComparator(LogMessageComparator.DESCENDING));
    assertSame(c1, entries.get(2));
    assertSame(c2, entries.get(1));
    assertSame(c3, entries.get(0));

    try {
      new LogMessageComparator(6);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException iae) {
      // Expected
    }

    // null values are not OK
    LogMessageComparator comparator = new LogMessageComparator(LogMessageComparator.ASCENDING);
    try {
      comparator.compare(null, null);
      fail("NullPointerException expected");
    } catch (NullPointerException npe) {
      // Expected
    }
  }

}