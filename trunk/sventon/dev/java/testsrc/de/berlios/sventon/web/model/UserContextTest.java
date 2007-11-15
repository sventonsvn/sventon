package de.berlios.sventon.web.model;

import junit.framework.TestCase;

public class UserContextTest extends TestCase {

  public void testDefaults() throws Exception {
    final UserContext userContext = new UserContext();
    assertNull(userContext.getCharset());
    assertEquals(1, userContext.getLatestRevisionsDisplayCount());
    assertNull(userContext.getSearchMode());
    assertNull(userContext.getSortMode());
    assertNull(userContext.getSortType());
  }

  public void testHasCredentials() throws Exception {
    final UserContext userContext = new UserContext();
    assertFalse(userContext.hasCredentials());
    userContext.setUid("uid");
    assertFalse(userContext.hasCredentials());
    userContext.setPwd("pwd");
    assertTrue(userContext.hasCredentials());
  }

}