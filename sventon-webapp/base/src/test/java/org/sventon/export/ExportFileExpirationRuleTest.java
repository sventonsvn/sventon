/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.export;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class ExportFileExpirationRuleTest {

  @Test
  public void testHasExpiredNumbersInFileName() throws Exception {
    final ExportFileExpirationRule rule = new ExportFileExpirationRule(1000);
    assertTrue(rule.hasExpired(new File("123-20000318025831111.zip")));
    assertTrue(rule.hasExpired(new File("20000318025831111-20000318025831111.zip")));
  }

  @Test
  public void testHasExpired() throws Exception {
    final ExportFileExpirationRule rule = new ExportFileExpirationRule(1000);
    assertTrue(rule.hasExpired(new File("defaultsvn-20000318025831111.zip")));
  }
}
