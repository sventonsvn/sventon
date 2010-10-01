package org.sventon.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LogEntryPathChangeTypeTest {

  @Test
  public void testLogEntryPAthChange() throws Exception {
    // Yes - really stupid tests, I know.. :-)
    assertEquals("Added", ChangeType.ADDED.toString());
    assertEquals("Modified", ChangeType.MODIFIED.toString());
    assertEquals("Replaced", ChangeType.REPLACED.toString());
    assertEquals("Deleted", ChangeType.DELETED.toString());
  }

  @Test
  public void testLogEntryPAthChangeValueOf() throws Exception {
    assertEquals(ChangeType.DELETED, ChangeType.parse("D"));
    assertEquals("Deleted", ChangeType.parse("D").toString());
  }

  @Test
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