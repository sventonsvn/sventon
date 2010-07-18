package org.sventon.model;

import junit.framework.TestCase;

public class LogEntryPathChangeTypeTest extends TestCase {

  public void testLogEntryPAthChange() throws Exception {
    // Yes - really stupid tests, I know.. :-)
    assertEquals("Added", LogEntryPathChangeType.ADDED.toString());
    assertEquals("Modified", LogEntryPathChangeType.MODIFIED.toString());
    assertEquals("Replaced", LogEntryPathChangeType.REPLACED.toString());
    assertEquals("Deleted", LogEntryPathChangeType.DELETED.toString());
  }

  public void testLogEntryPAthChangeValueOf() throws Exception {
    assertEquals(LogEntryPathChangeType.DELETED, LogEntryPathChangeType.parse("D"));
    assertEquals("Deleted", LogEntryPathChangeType.parse("D").toString());
  }

  public void testLogEntryPathChangeSwitch() throws Exception {
    switch (LogEntryPathChangeType.MODIFIED) {
      case ADDED:
        fail();
      case MODIFIED:
        break;
      case REPLACED:
        fail();
      case DELETED:
        fail();
    }
  }
}