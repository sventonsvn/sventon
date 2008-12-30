package org.sventon.model;

import junit.framework.TestCase;

public class CredentialsTest extends TestCase {

  public void testCredentials() throws Exception {
    Credentials credentials = new Credentials("userid", null);
    assertEquals("userid", credentials.getUsername());
    assertEquals(null, credentials.getPassword());

    credentials = new Credentials(null, "password");
    assertEquals(null, credentials.getUsername());
    assertEquals("password", credentials.getPassword());

    credentials = new Credentials(null, null);
    assertEquals(null, credentials.getUsername());
    assertEquals(null, credentials.getPassword());
  }

  //Test toString to make sure pwd and uid is not outputted
  public void testToString() throws Exception {

    String testString = "Credentials[" +
        "username=*****," +
        "password=*****]";

    Credentials credentials = new Credentials("uid", "pwd");
    assertEquals(testString, credentials.toString());

    testString = "Credentials[" +
        "username=<null>," +
        "password=*****]";
    credentials = new Credentials(null, "pwd");
    assertEquals(testString, credentials.toString());

    testString = "Credentials[" +
        "username=<null>," +
        "password=<null>]";
    credentials = new Credentials(null, null);
    assertEquals(testString, credentials.toString());
  }
}
