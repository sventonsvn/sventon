package de.berlios.sventon.svnsupport;

import junit.framework.TestCase;

public class LogEntryActionTypeTest extends TestCase {

  public void testLogEntryAction() throws Exception {
    assertEquals("Added", LogEntryActionType.A.getDescription());
    assertEquals("Modified", LogEntryActionType.M.getDescription());
    assertEquals("Replaced", LogEntryActionType.R.getDescription());
    assertEquals("Deleted", LogEntryActionType.D.getDescription());
  }

  public void testLogEntryActionValueOf() throws Exception {
    assertEquals(LogEntryActionType.D, LogEntryActionType.valueOf("D"));
    assertEquals("Deleted", LogEntryActionType.valueOf("D").getDescription());
  }

  public void testLogEntryActionSwitch() throws Exception {
    switch (LogEntryActionType.M) {
      case A:
        fail();
      case M:
        break;
      case R:
        fail();
      case D:
        fail();
    }
  }
}