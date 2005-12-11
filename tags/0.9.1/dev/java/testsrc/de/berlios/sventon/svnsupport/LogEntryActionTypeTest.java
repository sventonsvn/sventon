package de.berlios.sventon.svnsupport;

import junit.framework.TestCase;

public class LogEntryActionTypeTest extends TestCase {

  public void testLogEntryAction() throws Exception {
    // Yes - really stupid tests, I know.. :-)
    assertEquals("Added", LogEntryActionType.A.toString());
    assertEquals("Modified", LogEntryActionType.M.toString());
    assertEquals("Replaced", LogEntryActionType.R.toString());
    assertEquals("Deleted", LogEntryActionType.D.toString());
  }

  public void testLogEntryActionValueOf() throws Exception {
    assertEquals(LogEntryActionType.D, LogEntryActionType.valueOf("D"));
    assertEquals("Deleted", LogEntryActionType.valueOf("D").toString());
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