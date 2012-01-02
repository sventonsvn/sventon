/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.tags;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import static org.junit.Assert.assertEquals;

public class UrlTagTest {

  private MockPageContext context;
  private UrlTag tag;

  @Before
  public void setUp() throws Exception {
    context = createPageContext();
    tag = new UrlTag();
    tag.setPageContext(context);
  }

  @Test
  public void testSimpleUrl() throws Exception {
    tag.doStartTag();
    tag.setValue("/trunk/");
    tag.setVar("var");
    tag.addParameter("a", "1");
    tag.addParameter("b", "2");
    tag.doEndTag();
    assertEquals("/trunk/?a=1&b=2", context.getAttribute("var"));
  }

  @Test
  public void testUrlWithPlusSign() throws Exception {
    tag.doStartTag();
    tag.setValue("/my+trunk/");
    tag.setVar("var");
    tag.addParameter("a", "1");
    tag.addParameter("b", "2");
    tag.doEndTag();
    assertEquals("/my%2Btrunk/?a=1&b=2", context.getAttribute("var"));
  }

  @Test
  public void testUrlWithHashAndSpaceSign() throws Exception {
    tag.doStartTag();
    tag.setValue("/my# trunk/");
    tag.setVar("var");
    tag.addParameter("a", "1");
    tag.addParameter("b", "2");
    tag.doEndTag();
    assertEquals("/my%23%20trunk/?a=1&b=2", context.getAttribute("var"));
  }

  @Test
  public void testCrazyUrl() throws Exception {
    tag.doStartTag();
    tag.setValue("/trunk/'/-/!/#/%/(/@/_/~/+/a b/?/=/;/:/,/$/");
    tag.setVar("var");
    tag.addParameter("a", "1");
    tag.doEndTag();
    assertEquals("/trunk/%27/-/%21/%23/%25/%28/%40/_/%7E/%2B/a%20b/%3F/%3D/%3B/%3A/%2C/%24/?a=1", context.getAttribute("var"));
  }

  protected MockPageContext createPageContext() {
    final MockServletContext sc = new MockServletContext();
    final MockHttpServletRequest request = new MockHttpServletRequest(sc);
    final MockHttpServletResponse response = new MockHttpServletResponse();
    request.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, Mockito.mock(WebApplicationContext.class));
    return new MockPageContext(sc, request, response);
  }

}
