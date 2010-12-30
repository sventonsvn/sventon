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
package org.sventon.web;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.sventon.model.Credentials;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;

public class HttpBasicAuthenticationHandlerTest {

  @Test
  public void testGetAuthScheme() throws Exception {
    final HttpBasicAuthenticationHandler handler = new HttpBasicAuthenticationHandler();
    assertEquals(HttpServletRequest.BASIC_AUTH, handler.getAuthScheme());
  }

  @Test
  public void testParseCredentials() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final HttpBasicAuthenticationHandler handler = new HttpBasicAuthenticationHandler();

    Credentials credentials;

    try {
      handler.parseCredentials(request);
      fail("Should cause IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // Expected
    }

    final String credentialString = "uid:pwd";

    request.addHeader(AbstractHttpAuthenticationHandler.AUTHORIZATION_HEADER, "Basic " +
        new String(Base64.encodeBase64(credentialString.getBytes())));
    credentials = handler.parseCredentials(request);
    assertFalse(credentials.isEmpty());
    assertEquals("uid", credentials.getUserName());
    assertEquals("pwd", credentials.getPassword());
  }

}
