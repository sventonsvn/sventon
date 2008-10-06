package org.sventon.appl;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.sventon.TestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfigAuthorizationFilterTest extends TestCase {

  public void testDoFilterInternalApplicationNotConfigured() throws Exception {
    final ConfigAuthorizationFilter filter = new ConfigAuthorizationFilter();

    final HttpServletRequest request = new MockHttpServletRequest();
    final HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
    final MockFilterChain filterChain = new MockFilterChain();

    final Application application = TestUtils.getApplicationStub();
    application.setConfigured(false);
    filter.setApplication(application);

    EasyMock.replay(response);
    filter.doFilterInternal(request, response, filterChain);
    EasyMock.verify(response);

    assertSame(request, filterChain.getRequest());
    assertSame(response, filterChain.getResponse());
  }

  public void testDoFilterInternalApplicationConfiguredEditDisabled() throws Exception {
    final ConfigAuthorizationFilter filter = new ConfigAuthorizationFilter();

    final HttpServletRequest request = new MockHttpServletRequest();
    final HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
    final MockFilterChain filterChain = new MockFilterChain();

    final Application application = TestUtils.getApplicationStub();
    application.setConfigured(true);
    application.setEditableConfig(false);
    filter.setApplication(application);

    response.sendRedirect("/repos/list");

    EasyMock.replay(response);
    filter.doFilterInternal(request, response, filterChain);
    EasyMock.verify(response);

    assertNull(filterChain.getRequest());
    assertNull(filterChain.getResponse());
  }

  public void testDoFilterInternalApplicationConfiguredEditEnabledAlreadyLoggedIn() throws Exception {
    final ConfigAuthorizationFilter filter = new ConfigAuthorizationFilter();

    final MockHttpSession session = new MockHttpSession();
    session.setAttribute("isAdminLoggedIn", true);

    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setSession(session);

    final HttpServletResponse response = new MockHttpServletResponse();
    final MockFilterChain filterChain = new MockFilterChain();

    final Application application = TestUtils.getApplicationStub();
    application.setConfigured(true);
    application.setEditableConfig(true);
    filter.setApplication(application);

    filter.doFilterInternal(request, response, filterChain);

    assertSame(request, filterChain.getRequest());
    assertSame(response, filterChain.getResponse());
    assertTrue((Boolean) request.getAttribute("isEdit"));
  }

}
