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

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.sventon.model.Credentials;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;

public class AbstractHttpAuthenticationHandlerTest {

  private final AbstractHttpAuthenticationHandler handler = new AbstractHttpAuthenticationHandler() {
    public String getRealm() {
      return "realm";
    }

    public String getAuthScheme() {
      return "basic";
    }

    public Credentials parseCredentials(HttpServletRequest request) {
      return null;
    }
  };

  @Test
  public void testIsLoginAttempt() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    assertFalse(handler.isLoginAttempt(request));

    request.addHeader(AbstractHttpAuthenticationHandler.AUTHORIZATION_HEADER, "Basic xyz:abc");
    assertTrue(handler.isLoginAttempt(request));
  }

  @Test
  public void testGetAuthzHeader() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    assertEquals("", handler.getAuthzHeader(request));

    request.addHeader(AbstractHttpAuthenticationHandler.AUTHORIZATION_HEADER, "Basic xyz:abc");
    assertEquals("Basic xyz:abc", handler.getAuthzHeader(request));
  }

  @Test
  public void testSendChallenge() throws Exception {
    final MockHttpServletResponse response = new MockHttpServletResponse();
    handler.sendChallenge(response);
    assertEquals("basic realm=\"realm\"", response.getHeader(AbstractHttpAuthenticationHandler.AUTHENTICATE_HEADER));
    assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
  }

}
