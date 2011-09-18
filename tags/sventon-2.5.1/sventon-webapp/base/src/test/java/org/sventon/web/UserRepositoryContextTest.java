/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
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
import org.sventon.model.Credentials;
import org.sventon.model.RepositoryName;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;

public class UserRepositoryContextTest {

  @Test
  public void testDefaults() throws Exception {
    final UserRepositoryContext context = new UserRepositoryContext();
    assertNull(context.getCharset());
    assertEquals(1, context.getLatestRevisionsDisplayCount());
    assertNull(context.getSearchMode());
    assertNull(context.getSortMode());
    assertNull(context.getSortType());
  }

  @Test
  public void testHasCredentials() throws Exception {
    final UserRepositoryContext context = new UserRepositoryContext();
    assertFalse(context.hasCredentials());
    context.setCredentials(new Credentials("uid", "pwd"));
    assertTrue(context.hasCredentials());
  }

  @Test
  public void testClearCredentials() throws Exception {
    final UserRepositoryContext context = new UserRepositoryContext();
    context.setCredentials(new Credentials("uid", "pwd"));
    assertTrue(context.hasCredentials());
    context.clearCredentials();
    assertSame(Credentials.EMPTY, context.getCredentials());
  }

  @Test
  public void testGetUserContext() throws Exception {
    final RepositoryName name = new RepositoryName("repository1");
    final HttpServletRequest request = new MockHttpServletRequest();
    assertNull(request.getSession().getAttribute("userContext"));
    final UserRepositoryContext userRepositoryContext = UserRepositoryContext.getContext(request, name);
    assertNotNull(request.getSession());
    final Object o = request.getSession().getAttribute("userContext");
    assertNotNull(o);
    assertTrue(o instanceof UserContext);
    final UserRepositoryContext contextFromSession = ((UserContext) o).getUserRepositoryContext(name);
    assertSame(contextFromSession, userRepositoryContext);
  }
}