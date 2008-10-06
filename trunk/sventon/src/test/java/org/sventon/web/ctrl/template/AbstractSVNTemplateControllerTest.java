package org.sventon.web.ctrl.template;

import org.sventon.model.RepositoryName;
import org.sventon.model.UserContext;
import org.sventon.util.RepositoryEntryComparator;
import org.sventon.util.RepositoryEntrySorter;
import org.sventon.web.command.SVNBaseCommand;
import org.sventon.model.UserRepositoryContext;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AbstractSVNTemplateControllerTest extends TestCase {

  public void testGetUserContext() throws Exception {
    final AbstractSVNTemplateController ctrl = new TestController();

    final RepositoryName name = new RepositoryName("repository1");
    final HttpServletRequest request = new MockHttpServletRequest();
    assertNull(request.getSession().getAttribute("userContext"));
    final UserRepositoryContext userRepositoryContext = ctrl.getUserContext(request, name);
    assertNotNull(request.getSession());
    final Object o = request.getSession().getAttribute("userContext");
    assertNotNull(o);
    assertTrue(o instanceof UserContext);
    final UserRepositoryContext contextFromSession = ((UserContext) o).getUserRepositoryContext(name);
    assertSame(contextFromSession, userRepositoryContext);
  }

  public void testParseAndUpdateSortParameters() throws Exception {
    final UserRepositoryContext userRepositoryContext = new UserRepositoryContext();
    final AbstractSVNTemplateController ctrl = new TestController();
    final SVNBaseCommand svnCommand = new SVNBaseCommand();

    assertNull(userRepositoryContext.getSortMode());
    assertNull(userRepositoryContext.getSortType());
    ctrl.parseAndUpdateSortParameters(svnCommand, userRepositoryContext);
    assertEquals("ASC", userRepositoryContext.getSortMode().toString());
    assertEquals("FULL_NAME", userRepositoryContext.getSortType().toString());

    svnCommand.setSortType(RepositoryEntryComparator.SortType.SIZE);
    svnCommand.setSortMode(RepositoryEntrySorter.SortMode.DESC);

    ctrl.parseAndUpdateSortParameters(svnCommand, userRepositoryContext);
    assertEquals("DESC", userRepositoryContext.getSortMode().toString());
    assertEquals("SIZE", userRepositoryContext.getSortType().toString());
  }

  private static class TestController extends AbstractSVNTemplateController {
    protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                     final long headRevision, final UserRepositoryContext userRepositoryContext,
                                     final HttpServletRequest request, final HttpServletResponse response,
                                     final BindException exception) throws Exception {
      return new ModelAndView();
    }
  }
}