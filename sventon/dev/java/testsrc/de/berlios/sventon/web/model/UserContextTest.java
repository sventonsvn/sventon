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
}