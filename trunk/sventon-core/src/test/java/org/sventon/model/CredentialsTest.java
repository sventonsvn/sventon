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
package org.sventon.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CredentialsTest {

  @Test
  public void testCredentials() throws Exception {
    Credentials credentials = new Credentials("userid", null);
    assertEquals("userid", credentials.getUserName());
    assertEquals(null, credentials.getPassword());

    credentials = new Credentials(null, "password");
    assertEquals(null, credentials.getUserName());
    assertEquals("password", credentials.getPassword());

    credentials = new Credentials(null, null);
    assertEquals(null, credentials.getUserName());
    assertEquals(null, credentials.getPassword());
  }

  //Test toString to make sure pwd and uid is not outputted
  @Test
  public void testToString() throws Exception {

    String testString = "Credentials[" +
        "userName=*****," +
        "password=*****]";

    Credentials credentials = new Credentials("uid", "pwd");
    assertEquals(testString, credentials.toString());

    testString = "Credentials[" +
        "userName=<null>," +
        "password=*****]";
    credentials = new Credentials(null, "pwd");
    assertEquals(testString, credentials.toString());

    testString = "Credentials[" +
        "userName=<null>," +
        "password=<null>]";
    credentials = new Credentials(null, null);
    assertEquals(testString, credentials.toString());
  }

  @Test
  public void testIsEmpty() throws Exception {
    Credentials credentials = new Credentials(null, null);
    assertTrue(credentials.isEmpty());

    credentials = new Credentials("", "");
    assertTrue(credentials.isEmpty());
  }
}
