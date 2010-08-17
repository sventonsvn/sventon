package org.sventon.model;

import junit.framework.TestCase;
import org.sventon.TestUtils;

public class LogEntryWrapperTest extends TestCase {

  public void testGetWebFormattedMessage() {
    assertEquals("test", wrap("test"));
    assertEquals("&lt;br&gt;", wrap("<br>"));
    assertEquals("<br>A<br>B", wrap("\nA\nB"));
    assertEquals("\\", wrap("\\"));
  }

  private String wrap(String message) {
    return new LogEntry(TestUtils.getLogEntryStub(1, message)).getWebFormattedMessage();
  }

}
