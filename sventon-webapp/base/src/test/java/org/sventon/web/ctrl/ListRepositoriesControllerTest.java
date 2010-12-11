package org.sventon.web.ctrl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.ui.ExtendedModelMap;
import org.sventon.TestUtils;
import org.sventon.appl.Application;
import org.sventon.appl.ConfigDirectory;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.Credentials;
import org.sventon.model.RepositoryName;
import org.sventon.model.UserContext;
import org.sventon.model.UserRepositoryContext;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.*;

public class ListRepositoriesControllerTest {

  private Application application;
  private UserContext userContext;

  @Before
  public void setUp() throws Exception {
    ConfigDirectory configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);
    application = new Application(configDirectory);

    final UserRepositoryContext context1 = new UserRepositoryContext();
    context1.setCredentials(new Credentials("UID1", "PWD1"));
    final UserRepositoryContext context2 = new UserRepositoryContext();
    context2.setCredentials(new Credentials("UID2", "PWD2"));
    userContext = new UserContext();

    final RepositoryName repo1 = new RepositoryName("repo1");
    final RepositoryName repo2 = new RepositoryName("repo2");

    userContext.add(repo1, context1);
    userContext.add(repo2, context2);
  }

  @Test
  public void listTwoConfiguredRepositories() throws Exception {
    final ListRepositoriesController ctrl = new ListRepositoriesController(application);

    // Not configured
    assertEquals("redirect:/repos/listconfigs", ctrl.listRepositoriesOrShowIfOnlyOne(new ExtendedModelMap()));

    application.addConfiguration(createTestRepository("test1"));
    application.addConfiguration(createTestRepository("test2"));
    application.setConfigured(true);

    final ExtendedModelMap modelMap = new ExtendedModelMap();
    assertEquals("listRepositories", ctrl.listRepositoriesOrShowIfOnlyOne(modelMap));
    assertArrayEquals(Arrays.asList(new RepositoryName("test1"), new RepositoryName("test2")).toArray(), ((Set) modelMap.get("repositoryNames")).toArray());
  }

  @Test
  public void logout() throws Exception {
    final ListRepositoriesController controller = new ListRepositoriesController(application);
    application.addConfiguration(createTestRepository("test1"));
    application.addConfiguration(createTestRepository("test2"));
    application.setConfigured(true);

    final ExtendedModelMap map = new ExtendedModelMap();
    String view = controller.logoutBeforeListRepositories(true, "repo1", userContext, map);
    assertEquals("listRepositories", view);
    assertFalse(userContext.getUserRepositoryContext(new RepositoryName("repo1")).hasCredentials());
    assertTrue(userContext.getUserRepositoryContext(new RepositoryName("repo2")).hasCredentials());
  }

  @Test
  public void logoutIncorrectRepositoryName() throws Exception {
    final ListRepositoriesController controller = new ListRepositoriesController(application);
    application.addConfiguration(createTestRepository("test1"));
    application.addConfiguration(createTestRepository("test2"));
    application.setConfigured(true);

    final ExtendedModelMap map = new ExtendedModelMap();
    String view = controller.logoutBeforeListRepositories(true, "Pingu", userContext, map);
    assertEquals("listRepositories", view);
    assertTrue(userContext.getUserRepositoryContext(new RepositoryName("repo1")).hasCredentials());
    assertTrue(userContext.getUserRepositoryContext(new RepositoryName("repo2")).hasCredentials());
  }

  @Test
  public void logoutNoRepositoryName() throws Exception {
    final ListRepositoriesController controller = new ListRepositoriesController(application);
    application.addConfiguration(createTestRepository("test1"));
    application.addConfiguration(createTestRepository("test2"));
    application.setConfigured(true);

    final ExtendedModelMap map = new ExtendedModelMap();
    String view = controller.logoutBeforeListRepositories(true, "", userContext, map);
    assertEquals("listRepositories", view);
    assertTrue(userContext.getUserRepositoryContext(new RepositoryName("repo1")).hasCredentials());
    assertTrue(userContext.getUserRepositoryContext(new RepositoryName("repo2")).hasCredentials());
  }

  @Test
  public void listRepositoriesNotConfigured() throws Exception {
    final ListRepositoriesController ctrl = new ListRepositoriesController(application);

    // Not configured
    application.setConfigured(false);
    final ExtendedModelMap model = new ExtendedModelMap();
    String view = ctrl.listRepositoriesOrShowIfOnlyOne(model);
    assertEquals("redirect:/repos/listconfigs", view);
  }

  @Test
  public void listRepositoriesConfiguredButNoInstances() throws Exception {
    final ListRepositoriesController ctrl = new ListRepositoriesController(application);

    // configured but no instancs
    application.setConfigured(true);
    final ExtendedModelMap model = new ExtendedModelMap();
    assertNull(ctrl.listRepositoriesOrShowIfOnlyOne(model));
  }

  @Test
  public void listOneConfiguredRepositories() throws Exception {
    final ListRepositoriesController ctrl = new ListRepositoriesController(application);

    // Not configured
    assertEquals("redirect:/repos/listconfigs", ctrl.listRepositoriesOrShowIfOnlyOne(new ExtendedModelMap()));

    application.addConfiguration(createTestRepository("test1"));
    application.setConfigured(true);

    assertEquals("redirect:/repos/test1/list/", ctrl.listRepositoriesOrShowIfOnlyOne(null));
  }


  @Test
  public void testCreateListUrl() {
    final ListRepositoriesController ctrl = new ListRepositoriesController(application);
    assertEquals("/repos/test/list/", ctrl.createListUrl(new RepositoryName("test"), false));
    assertEquals("redirect:/repos/%C3%BC/list/", ctrl.createListUrl(new RepositoryName("\u00fc"), true));
  }

  private RepositoryConfiguration createTestRepository(final String repositoryName) {
    final RepositoryConfiguration configuration = new RepositoryConfiguration(repositoryName);
    configuration.setRepositoryUrl("http://localhost/svn");
    configuration.setCacheUsed(false);
    configuration.setZippedDownloadsAllowed(false);
    configuration.setEnableAccessControl(false);
    return configuration;
  }
}