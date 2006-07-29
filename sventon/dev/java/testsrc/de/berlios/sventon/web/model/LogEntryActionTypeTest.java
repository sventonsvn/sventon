package de.berlios.sventon.web.model;

import junit.framework.TestCase;
import de.berlios.sventon.web.model.LogEntryActionType;

public class LogEntryActionTypeTest extends TestCase {

  public void testLogEntryAction() throws Exception {
    // Yes - really stupid tests, I know.. :-)
    assertEquals("Added", LogEntryActionType.ADDED.toString());
    assertEquals("Modified", LogEntryActionType.MODIFIED.toString());
    assertEquals("Replaced", LogEntryActionType.REPLACED.toString());
    assertEquals("Deleted", LogEntryActionType.DELETED.toString());
  }

  public void testLogEntryActionValueOf() throws Exception {
    assertEquals(LogEntryActionType.DELETED, LogEntryActionType.parse("D"));
    assertEquals("Deleted", LogEntryActionType.parse("D").toString());
  }

  public void testLogEntryActionSwitch() throws Exception {
    switch (LogEntryActionType.MODIFIED) {
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