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
package org.sventon.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RepositoryNameTest {

  @Test
  public void testIsValid() throws Exception {
    assertFalse(RepositoryName.isValid(null));
    assertFalse(RepositoryName.isValid(""));
    assertFalse(RepositoryName.isValid(" "));
    assertFalse(RepositoryName.isValid("A A"));

    assertTrue(RepositoryName.isValid("a"));
    assertTrue(RepositoryName.isValid("aa"));
    assertTrue(RepositoryName.isValid("AA"));
    assertTrue(RepositoryName.isValid("Aa1"));
    assertTrue(RepositoryName.isValid("Aa1-"));
    assertTrue(RepositoryName.isValid("-aa1_"));
    assertTrue(RepositoryName.isValid("testRepos"));
    assertTrue(RepositoryName.isValid("test_Repos"));
    assertTrue(RepositoryName.isValid("test-Repos"));
    assertTrue(RepositoryName.isValid("test-Repos_1"));

    assertTrue(RepositoryName.isValid("åäö"));
  }

}