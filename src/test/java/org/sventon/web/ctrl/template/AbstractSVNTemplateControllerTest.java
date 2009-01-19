package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.model.RepositoryName;
import org.sventon.model.UserContext;
import org.sventon.model.UserRepositoryContext;
import org.sventon.util.RepositoryEntryComparator;
import org.sventon.util.RepositoryEntrySorter;
import org.sventon.web.command.SVNBaseCommand;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AbstractSVNTemplateControllerTest extends TestCase {

  public void testParseAndUpdateSortParameters() throws Exception {
    final UserRepositoryContext userRepositoryContext = new UserRepositoryContext();
    final AbstractSVNTemplateController ctrl = new TestController();
    final SVNBaseCommand command = new SVNBaseCommand();

    assertNull(userRepositoryContext.getSortMode());
    assertNull(userRepositoryContext.getSortType());
    ctrl.parseAndUpdateSortParameters(command, userRepositoryContext);
    assertEquals("ASC", userRepositoryContext.getSortMode().toString());
    assertEquals("FULL_NAME", userRepositoryContext.getSortType().toString());

    command.setSortType(RepositoryEntryComparator.SortType.SIZE);
    command.setSortMode(RepositoryEntrySorter.SortMode.DESC);

    ctrl.parseAndUpdateSortParameters(command, userRepositoryContext);
    assertEquals("DESC", userRepositoryContext.getSortMode().toString());
    assertEquals("SIZE", userRepositoryContext.getSortType().toString());
  }

  private static class TestController extends AbstractSVNTemplateController {
    protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand command,
                                     final long headRevision, final UserRepositoryContext userRepositoryContext,
                                     final HttpServletRequest request, final HttpServletResponse response,
                                     final BindException exception) throws Exception {
      return new ModelAndView();
    }
  }
}