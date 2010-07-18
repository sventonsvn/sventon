package org.sventon.web;

import junit.framework.TestCase;
import org.apache.commons.codec.binary.Base64;
import org.springframework.mock.web.MockHttpServletRequest;
import org.sventon.model.Credentials;

import javax.servlet.http.HttpServletRequest;

public class HttpBasicAuthenticationHandlerTest extends TestCase {

  public void testGetAuthScheme() throws Exception {
    final HttpBasicAuthenticationHandler handler = new HttpBasicAuthenticationHandler();
    assertEquals(HttpServletRequest.BASIC_AUTH, handler.getAuthScheme());
  }

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
