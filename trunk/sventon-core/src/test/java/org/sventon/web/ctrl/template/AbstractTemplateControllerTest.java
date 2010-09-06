package org.sventon.web.ctrl.template;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.SVNConnection;
import org.sventon.SVNConnectionFactory;
import org.sventon.SventonException;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.*;
import org.sventon.web.command.BaseCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;

public class AbstractTemplateControllerTest {

  @Test
  public void testParseAndUpdateSortParameters() throws Exception {
    final UserRepositoryContext userRepositoryContext = new UserRepositoryContext();
    final AbstractTemplateController ctrl = new TestController();
    final BaseCommand command = new BaseCommand();

    assertNull(userRepositoryContext.getSortMode());
    assertNull(userRepositoryContext.getSortType());
    ctrl.parseAndUpdateSortParameters(command, userRepositoryContext);
    assertEquals("ASC", userRepositoryContext.getSortMode().toString());
    assertEquals("FULL_NAME", userRepositoryContext.getSortType().toString());

    command.setSortType(DirEntryComparator.SortType.SIZE);
    command.setSortMode(DirEntrySorter.SortMode.DESC);

    ctrl.parseAndUpdateSortParameters(command, userRepositoryContext);
    assertEquals("DESC", userRepositoryContext.getSortMode().toString());
    assertEquals("SIZE", userRepositoryContext.getSortType().toString());
  }

  @Test
  public void testCreateConnection() throws Exception {
    final AbstractTemplateController ctrl = new TestController();
    final MutableBoolean usingSharedAuthSettings = new MutableBoolean(false);

    ctrl.setConnectionFactory(new SVNConnectionFactory() {
      public SVNConnection createConnection(RepositoryName repositoryName, SVNURL svnUrl, Credentials credentials) throws SventonException {
        if ("shared".equals(credentials.getUserName())) {
          usingSharedAuthSettings.setValue(true);
        } else if ("user".equals(credentials.getUserName())) {
          usingSharedAuthSettings.setValue(false);
        }
        return null;
      }
    });

    final RepositoryConfiguration configuration = new RepositoryConfiguration("test");
    final UserRepositoryContext context = new UserRepositoryContext();

    assertFalse(usingSharedAuthSettings.booleanValue());

    configuration.setUserCredentials(new Credentials("shared", "pass"));
    configuration.setEnableAccessControl(false);
    ctrl.createConnection(configuration, context);
    assertTrue(usingSharedAuthSettings.booleanValue());

    context.setCredentials(new Credentials("user", "pass"));
    configuration.setEnableAccessControl(true);
    ctrl.createConnection(configuration, context);
    assertFalse(usingSharedAuthSettings.booleanValue());

    ctrl.createConnection(configuration, context);
    assertFalse(usingSharedAuthSettings.booleanValue());
  }

  private static class TestController extends AbstractTemplateController {
    protected ModelAndView svnHandle(final SVNConnection connection, final BaseCommand command,
                                     final long headRevision, final UserRepositoryContext userRepositoryContext,
                                     final HttpServletRequest request, final HttpServletResponse response,
                                     final BindException exception) throws Exception {
      return new ModelAndView();
    }
  }
}