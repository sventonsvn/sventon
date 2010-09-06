package org.sventon.mail;

import org.junit.Test;
import org.sventon.model.RepositoryName;

import static org.junit.Assert.assertEquals;

public class MailNotifierTest {

  @Test
  public void testFormatSubject() throws Exception {
    final MailNotifier mailNotifier = new MailNotifier();
    assertEquals("testRepos - revision 123", mailNotifier.formatSubject(
        "@@repositoryName@@ - revision @@revision@@", 123, new RepositoryName("testRepos")));
  }
}