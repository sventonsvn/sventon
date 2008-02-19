package de.berlios.sventon.mail;

import junit.framework.TestCase;

public class MailNotifierTest extends TestCase {

  public void testFormatSubject() throws Exception {
    final MailNotifier mailNotifier = new MailNotifier();
    assertEquals("testInstance - revision 123", mailNotifier.formatSubject(
        "@@instanceName@@ - revision @@revision@@", 123, "testInstance"));
  }
}