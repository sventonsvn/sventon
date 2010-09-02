package org.sventon.model;

import junit.framework.TestCase;
import org.sventon.TestUtils;

import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class LogEntryTest extends TestCase {

  public void testGetWebFormattedMessage() {
    assertEquals("test", wrap("test"));
    assertEquals("&lt;br&gt;", wrap("<br>"));
    assertEquals("<br>A<br>B", wrap("\nA\nB"));
    assertEquals("\\", wrap("\\"));
  }

  private String wrap(String message) {
    return TestUtils.createLogEntry(1, "", null, message).getWebFormattedMessage();
  }

  public void testIsAccessible() throws Exception {
    assertFalse(TestUtils.createLogEntry(12, null, null, null).isAccessible());
    assertFalse(TestUtils.createLogEntry(12, null, null, "message").isAccessible());
    assertFalse(TestUtils.createLogEntry(12, null, new Date(), null).isAccessible());

    final SortedSet<ChangedPath> changedPaths = new TreeSet<ChangedPath>();
    changedPaths.add(new ChangedPath("/file1.java", null, 1, ChangeType.MODIFIED));
    assertTrue(TestUtils.createLogEntry(12, null, new Date(), null, changedPaths).isAccessible());
  }
}
