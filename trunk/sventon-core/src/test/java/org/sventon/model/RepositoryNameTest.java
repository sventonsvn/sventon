package org.sventon.model;

import junit.framework.TestCase;

public class RepositoryNameTest extends TestCase {

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