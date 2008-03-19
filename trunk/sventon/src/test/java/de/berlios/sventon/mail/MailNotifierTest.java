package de.berlios.sventon.mail;

import junit.framework.TestCase;
import de.berlios.sventon.appl.RepositoryName;

public class MailNotifierTest extends TestCase {

  public void testFormatSubject() throws Exception {
    final MailNotifier mailNotifier = new MailNotifier();
    assertEquals("testRepos - revision 123", mailNotifier.formatSubject(
        "@@repositoryName@@ - revision @@revision@@", 123, new RepositoryName("testRepos")));
  }
}