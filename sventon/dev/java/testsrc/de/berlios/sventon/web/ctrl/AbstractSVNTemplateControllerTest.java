package de.berlios.sventon.web.ctrl;

import junit.framework.TestCase;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AbstractSVNTemplateControllerTest extends TestCase {

  public void testGetUserContext() throws Exception {
    final AbstractSVNTemplateController ctrl = new TestController();

    final HttpServletRequest request = new MockHttpServletRequest();
    assertNull(request.getSession().getAttribute("userContext"));
    final UserContext userContext = ctrl.getUserContext(request);
    assertNotNull(request.getSession());
    final Object o = request.getSession().getAttribute("userContext");
    assertNotNull(o);
    assertTrue(o instanceof UserContext);
    assertSame(o, userContext);
  }

  public void testParseAndUpdateSortParameters() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final UserContext userContext = new UserContext();
    final AbstractSVNTemplateController ctrl = new TestController();
    assertNull(userContext.getSortMode());
    assertNull(userContext.getSortType());
    ctrl.parseAndUpdateSortParameters(request, userContext);
    assertEquals("ASC", userContext.getSortMode().toString());
    assertEquals("NAME", userContext.getSortType().toString());

    request.addParameter("sortType", "SIZE");
    request.addParameter("sortMode", "DESC");
    ctrl.parseAndUpdateSortParameters(request, userContext);
    assertEquals("DESC", userContext.getSortMode().toString());
    assertEquals("SIZE", userContext.getSortType().toString());

  }

  private static class TestController extends AbstractSVNTemplateController {
    protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                     final SVNRevision revision, final UserContext userContext,
                                     final HttpServletRequest request, final HttpServletResponse response,
                                     final BindException exception) throws Exception {
      return new ModelAndView();
    }
  }
}