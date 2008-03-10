package de.berlios.sventon.appl;

import junit.framework.TestCase;

public class InstanceTest extends TestCase {

  public void testIsValidName() throws Exception {
    assertFalse(Instance.isValidName(null));
    assertFalse(Instance.isValidName(""));
    assertFalse(Instance.isValidName(" "));
    assertFalse(Instance.isValidName("A A"));

    assertTrue(Instance.isValidName("a"));
    assertTrue(Instance.isValidName("aa"));
    assertTrue(Instance.isValidName("AA"));
    assertTrue(Instance.isValidName("Aa1"));
    assertTrue(Instance.isValidName("Aa1-"));
    assertTrue(Instance.isValidName("-aa1_"));
    assertTrue(Instance.isValidName("testRepos"));
    assertTrue(Instance.isValidName("test_Repos"));
    assertTrue(Instance.isValidName("test-Repos"));
    assertTrue(Instance.isValidName("test-Repos_1"));

    assertTrue(Instance.isValidName("åÄö"));
  }

}