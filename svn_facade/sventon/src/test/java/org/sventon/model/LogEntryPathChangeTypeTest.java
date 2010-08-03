package org.sventon.model;

import junit.framework.TestCase;

public class LogEntryPathChangeTypeTest extends TestCase {

  public void testLogEntryPAthChange() throws Exception {
    // Yes - really stupid tests, I know.. :-)
    assertEquals("Added", DirEntryChangeType.ADDED.toString());
    assertEquals("Modified", DirEntryChangeType.MODIFIED.toString());
    assertEquals("Replaced", DirEntryChangeType.REPLACED.toString());
    assertEquals("Deleted", DirEntryChangeType.DELETED.toString());
  }

  public void testLogEntryPAthChangeValueOf() throws Exception {
    assertEquals(DirEntryChangeType.DELETED, DirEntryChangeType.parse("D"));
    assertEquals("Deleted", DirEntryChangeType.parse("D").toString());
  }

  public void testLogEntryPathChangeSwitch() throws Exception {
    switch (DirEntryChangeType.MODIFIED) {
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