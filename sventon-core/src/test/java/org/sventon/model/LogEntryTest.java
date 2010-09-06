package org.sventon.model;

import org.junit.Test;
import org.sventon.TestUtils;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class LogEntryTest {

  @Test
  public void testGetWebFormattedMessage() {
    assertEquals("test", wrap("test"));
    assertEquals("&lt;br&gt;", wrap("<br>"));
    assertEquals("<br>A<br>B", wrap("\nA\nB"));
    assertEquals("\\", wrap("\\"));
  }

  @Test
  public void testIsAccessible() throws Exception {
    assertFalse(TestUtils.createLogEntry(12, null, null, null).isAccessible());
    assertFalse(TestUtils.createLogEntry(12, null, null, "message").isAccessible());
    assertFalse(TestUtils.createLogEntry(12, null, new Date(), null).isAccessible());

    final SortedSet<ChangedPath> changedPaths = new TreeSet<ChangedPath>();
    changedPaths.add(new ChangedPath("/file1.java", null, 1, ChangeType.MODIFIED));
    assertTrue(TestUtils.createLogEntry(12, null, new Date(), null, changedPaths).isAccessible());
  }

  private String wrap(String message) {
    return TestUtils.createLogEntry(1, "", null, message).getWebFormattedMessage();
  }
}
