package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.repository.SVNRepositoryStub;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserContext;
import de.berlios.sventon.web.model.UserRepositoryContext;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class AbstractSVNTemplateControllerTest extends TestCase {

  public void testGetUserContext() throws Exception {
    final AbstractSVNTemplateController ctrl = new TestController();

    final HttpServletRequest request = new MockHttpServletRequest();
    assertNull(request.getSession().getAttribute("userContext"));
    final UserRepositoryContext userRepositoryContext = UserRepositoryContext.getContext(request, "instance1");
    assertNotNull(request.getSession());
    final Object o = request.getSession().getAttribute("userContext");
    assertNotNull(o);
    assertTrue(o instanceof UserContext);
    final UserRepositoryContext contextFromSession = ((UserContext) o).getUserRepositoryContext("instance1");
    assertSame(contextFromSession, userRepositoryContext);
  }

  public void testParseAndUpdateSortParameters() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final UserRepositoryContext userRepositoryContext = new UserRepositoryContext();
    final AbstractSVNTemplateController ctrl = new TestController();
    assertNull(userRepositoryContext.getSortMode());
    assertNull(userRepositoryContext.getSortType());
    ctrl.parseAndUpdateSortParameters(request, userRepositoryContext);
    assertEquals("ASC", userRepositoryContext.getSortMode().toString());
    assertEquals("FULL_NAME", userRepositoryContext.getSortType().toString());

    request.addParameter("sortType", "SIZE");
    request.addParameter("sortMode", "DESC");
    ctrl.parseAndUpdateSortParameters(request, userRepositoryContext);
    assertEquals("DESC", userRepositoryContext.getSortMode().toString());
    assertEquals("SIZE", userRepositoryContext.getSortType().toString());
  }

  public void testConvertAndUpdateRevision() throws Exception {
    final AbstractSVNTemplateController ctrl = new TestController();

    final SVNBaseCommand command = new SVNBaseCommand();
    command.setRevision("head");
    assertEquals(SVNRevision.HEAD, ctrl.convertAndUpdateRevision(command, null));
    command.setRevision("");
    assertEquals(SVNRevision.HEAD, ctrl.convertAndUpdateRevision(command, null));
    command.setRevision("123");
    assertEquals(SVNRevision.create(123), ctrl.convertAndUpdateRevision(command, null));
    command.setRevision("{2007-01-01}");
    assertEquals(SVNRevision.create(321), ctrl.convertAndUpdateRevision(command, new SVNRepositoryStub(null, null) {
      public long getDatedRevision(Date date) throws SVNException {
        return 321;
      }
    }));
  }

  private static class TestController extends AbstractSVNTemplateController {
    protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                     final SVNRevision revision, final UserRepositoryContext userRepositoryContext,
                                     final HttpServletRequest request, final HttpServletResponse response,
                                     final BindException exception) throws Exception {
      return new ModelAndView();
    }
  }
}