package org.sventon.model;

import junit.framework.TestCase;

public class LogEntryPathChangeTypeTest extends TestCase {

  public void testLogEntryPAthChange() throws Exception {
    // Yes - really stupid tests, I know.. :-)
    assertEquals("Added", ChangeType.ADDED.toString());
    assertEquals("Modified", ChangeType.MODIFIED.toString());
    assertEquals("Replaced", ChangeType.REPLACED.toString());
    assertEquals("Deleted", ChangeType.DELETED.toString());
  }

  public void testLogEntryPAthChangeValueOf() throws Exception {
    assertEquals(ChangeType.DELETED, ChangeType.parse("D"));
    assertEquals("Deleted", ChangeType.parse("D").toString());
  }

  public void testLogEntryPathChangeSwitch() throws Exception {
    switch (ChangeType.MODIFIED) {
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