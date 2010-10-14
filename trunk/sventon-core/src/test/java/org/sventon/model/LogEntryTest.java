package org.sventon.model;

import org.junit.Test;
import org.sventon.util.DateUtil;

import java.util.*;

import static org.junit.Assert.*;

public class LogEntryTest {

  @Test
  public void testIsAccessible() throws Exception {
    assertFalse(createLogEntry(12, null, null, null).isAccessible());
    assertFalse(createLogEntry(12, null, null, "message").isAccessible());
    assertFalse(createLogEntry(12, null, new Date(), null).isAccessible());

    final SortedSet<ChangedPath> changedPaths = new TreeSet<ChangedPath>();
    changedPaths.add(new ChangedPath("/file1.java", null, 1, ChangeType.MODIFIED));
    assertTrue(createLogEntry(12, null, new Date(), null, changedPaths).isAccessible());
  }

  private static LogEntry createLogEntry(long revision, String author, Date date, String message) {
    return createLogEntry(revision, author, date, message, new TreeSet<ChangedPath>());
  }

  private static LogEntry createLogEntry(long revision, String author, Date date, String message, SortedSet<ChangedPath> changedPaths) {
    final Map<RevisionProperty, String> props = new HashMap<RevisionProperty, String>();
    props.put(RevisionProperty.AUTHOR, author);
    props.put(RevisionProperty.LOG, message);
    props.put(RevisionProperty.DATE, DateUtil.formatISO8601(date));
    return new LogEntry(revision, props, changedPaths);
  }

}
