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

  public void testSetUid() throws Exception {
    final UserContext userContext = new UserContext();
    userContext.setUid("userid");
    assertEquals("userid", userContext.getUid());

    //test null case
    userContext.setUid(null);
    assertEquals(null, userContext.getUid());
  }

  public void testSetPwd() throws Exception {
    final UserContext userContext = new UserContext();
    userContext.setPwd("password");
    assertEquals("password", userContext.getPwd());

    //test null case
    userContext.setPwd(null);
    assertEquals(null, userContext.getPwd());
  }

  //Test toString to make sure pwd and uid is not outputted
  public void testToString() throws Exception {

    String testString = "UserContext[sortType=<null>," +
       "sortMode=<null>," +
       "latestRevisionsDisplayCount=1," +
       "charset=<null>," +
       "uid=*****," +
       "pwd=*****]";

    final UserContext userContext = new UserContext();
    userContext.setUid("uid");
    userContext.setPwd("pwd");
    assertEquals(testString, userContext.toString());

    testString = "UserContext[sortType=<null>," +
       "sortMode=<null>," +
       "latestRevisionsDisplayCount=1," +
       "charset=<null>," +
       "uid=<null>," +
       "pwd=*****]";
    userContext.setUid(null);
    assertEquals(testString, userContext.toString());

    testString = "UserContext[sortType=<null>," +
       "sortMode=<null>," +
       "latestRevisionsDisplayCount=1," +
       "charset=<null>," +
       "uid=<null>," +
       "pwd=<null>]";
    userContext.setPwd(null);
    assertEquals(testString, userContext.toString());
  }

}