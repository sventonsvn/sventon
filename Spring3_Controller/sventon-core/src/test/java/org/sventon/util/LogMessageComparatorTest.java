package org.sventon.util;

import org.junit.Test;
import org.sventon.TestUtils;
import org.sventon.model.LogMessageSearchItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class LogMessageComparatorTest {

  @Test
  public void testCompare() throws Exception {
    List<LogMessageSearchItem> entries = new ArrayList<LogMessageSearchItem>();
    LogMessageSearchItem c1 = new LogMessageSearchItem(TestUtils.createLogEntry(1, "jesper", new Date(), "Message 1"));
    LogMessageSearchItem c2 = new LogMessageSearchItem(TestUtils.createLogEntry(12, "jesper", new Date(), "Message 2"));
    LogMessageSearchItem c3 = new LogMessageSearchItem(TestUtils.createLogEntry(123, "jesper", new Date(), "Message 3"));

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