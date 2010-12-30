/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
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