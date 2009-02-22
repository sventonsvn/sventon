package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.RepositoryConnectionFactory;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.Credentials;
import org.sventon.model.RepositoryName;
import org.sventon.model.UserRepositoryContext;
import org.sventon.util.RepositoryEntryComparator;
import org.sventon.util.RepositoryEntrySorter;
import org.sventon.web.command.BaseCommand;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AbstractTemplateControllerTest extends TestCase {

  public void testParseAndUpdateSortParameters() throws Exception {
    final UserRepositoryContext userRepositoryContext = new UserRepositoryContext();
    final AbstractTemplateController ctrl = new TestController();
    final BaseCommand command = new BaseCommand();

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

  public void testCreateConnection() throws Exception {
    final AbstractTemplateController ctrl = new TestController();
    final MutableBoolean usingGlobalAuthSettings = new MutableBoolean(false);

    ctrl.setRepositoryConnectionFactory(new RepositoryConnectionFactory() {
      public SVNRepository createConnection(RepositoryName repositoryName, SVNURL svnUrl, Credentials credentials) throws SVNException {
        if ("global".equals(credentials.getUsername())) {
          usingGlobalAuthSettings.setValue(true);
        } else if ("user".equals(credentials.getUsername())) {
          usingGlobalAuthSettings.setValue(false);
        }
        return null;
      }
    });

    final RepositoryConfiguration configuration = new RepositoryConfiguration("test");
    final UserRepositoryContext context = new UserRepositoryContext();

    assertFalse(usingGlobalAuthSettings.booleanValue());

    configuration.setCredentials(new Credentials("global", "pass"));
    configuration.setEnableAccessControl(false);
    ctrl.createConnection(configuration, context);
    assertTrue(usingGlobalAuthSettings.booleanValue());

    configuration.setEnableAccessControl(true);
    ctrl.createConnection(configuration, context);
    assertTrue(usingGlobalAuthSettings.booleanValue());

    context.setCredentials(new Credentials("user", "pass"));
    ctrl.createConnection(configuration, context);
    assertFalse(usingGlobalAuthSettings.booleanValue());
  }

  private static class TestController extends AbstractTemplateController {
    protected ModelAndView svnHandle(final SVNRepository repository, final BaseCommand command,
                                     final long headRevision, final UserRepositoryContext userRepositoryContext,
                                     final HttpServletRequest request, final HttpServletResponse response,
                                     final BindException exception) throws Exception {
      return new ModelAndView();
    }
  }
}