package org.sventon.web.ctrl;

import org.junit.Ignore;
import org.sventon.appl.Application;

@Ignore
public class ConfigurationFormControllerTest {

  private Application application;

  /*
  protected void setUp() throws Exception {
    ConfigDirectory configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);
    application = new Application(configDirectory, TestUtils.CONFIG_FILE_NAME);
  }

  public void testShowForm() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest("GET", "");
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationFormController ctrl = new ConfigurationFormController(application);
    ctrl.setCommandClass(ConfigCommand.class);
    final ModelAndView modelAndView = ctrl.handleRequest(request, response);
    assertNotNull(modelAndView);
    assertEquals(3, modelAndView.getModel().size());
  }

  //Test what happens if a repository is partially configured and the config view is invoked
  //this could happen if one started to configure sventon and then called 'browse'.
  public void testShowFormConfiguredII() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest("GET", "");
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationFormController ctrl = new ConfigurationFormController(application);

    final RepositoryConfiguration repositoryConfig = new RepositoryConfiguration("repository1");
    application.setConfigured(false);
    application.addConfiguration(repositoryConfig);
    ctrl.setCommandClass(ConfigCommand.class);
    final ModelAndView modelAndView = ctrl.handleRequest(request, response);
    assertNotNull(modelAndView);
    assertEquals(3, modelAndView.getModel().size());
  }

  public void testProcessFormSubmissionNonConfigured() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationFormController ctrl = new ConfigurationFormController(application);
    assertEquals(0, application.getRepositoryConfigurationCount());
    assertFalse(application.isConfigured());
    final ConfigCommand command = new ConfigCommand();
    final String repositoryName = "testrepos";
    command.setName(repositoryName);
    command.setRepositoryUrl("http://localhost");
    final BindException exception = new BindException(command, "test");
    final ModelAndView modelAndView = ctrl.onSubmit(request, response, command, exception);
    assertNotNull(modelAndView);
    assertEquals(4, modelAndView.getModel().size());
    assertEquals(1, application.getRepositoryConfigurationCount());
    assertFalse(application.isConfigured());
    final Map model = modelAndView.getModel();
    assertEquals(new RepositoryName(repositoryName), ((Set) model.get("addedRepositories")).iterator().next());
  }

  public void testProcessFormSubmissionNonConfiguredUserBasedAccessControl() throws Exception {
    final String repositoryName = "testrepos";

    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationFormController ctrl = new ConfigurationFormController(application);
    assertEquals(0, application.getRepositoryConfigurationCount());
    assertFalse(application.isConfigured());
    final ConfigCommand command = new ConfigCommand();
    command.setName(repositoryName);
    command.setRepositoryUrl("http://localhost");
    command.setAccessMethod(USER);
    command.setZippedDownloadsAllowed(true);
    command.setUserName("test uid");
    command.setUserPassword("test pwd");

    final BindException exception = new BindException(command, "test");
    final ModelAndView modelAndView = ctrl.onSubmit(request, response, command, exception);
    assertNotNull(modelAndView);

    assertEquals(1, application.getRepositoryConfigurationCount());
    assertFalse(application.isConfigured());
    final Map model = modelAndView.getModel();
    assertEquals(new RepositoryName(repositoryName), ((Set) model.get("addedRepositories")).iterator().next());

    //assert that the config was created correctly:
    final RepositoryConfiguration configuration = application.getConfiguration(new RepositoryName(repositoryName));
    assertEquals("http://localhost", configuration.getRepositoryUrl());
    assertTrue(configuration.isAccessControlEnabled());
    assertTrue(configuration.isZippedDownloadsAllowed());
    assertEquals(Credentials.EMPTY, configuration.getUserCredentials()); //UID/PWD only for connection testing, not stored
    assertEquals(Credentials.EMPTY, configuration.getCacheCredentials()); //UID/PWD for cache, not configured.
  }

  public void testProcessFormSubmissionNonConfiguredUserBasedAccessControlWithCache() throws Exception {
    final String repositoryName = "testrepos";

    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationFormController ctrl = new ConfigurationFormController(application);
    assertEquals(0, application.getRepositoryConfigurationCount());
    assertFalse(application.isConfigured());
    final ConfigCommand command = new ConfigCommand();
    command.setName(repositoryName);
    command.setRepositoryUrl("http://localhost");
    command.setAccessMethod(USER);
    command.setZippedDownloadsAllowed(true);
    command.setUserName("test uid");
    command.setUserPassword("test pwd");

    command.setCacheUsed(true);
    command.setCacheUserName("cache uid");
    command.setCacheUserPassword("cache pwd");

    final BindException exception = new BindException(command, "test");
    final ModelAndView modelAndView = ctrl.onSubmit(request, response, command, exception);
    assertNotNull(modelAndView);

    assertEquals(1, application.getRepositoryConfigurationCount());
    assertFalse(application.isConfigured());
    final Map model = modelAndView.getModel();
    assertEquals(new RepositoryName(repositoryName), ((Set) model.get("addedRepositories")).iterator().next());

    //assert that the config was created correctly:
    final RepositoryConfiguration configuration = application.getConfiguration(new RepositoryName(repositoryName));
    assertEquals("http://localhost", configuration.getRepositoryUrl());
    assertTrue(configuration.isAccessControlEnabled());
    assertTrue(configuration.isZippedDownloadsAllowed());
    assertEquals(Credentials.EMPTY, configuration.getUserCredentials()); //UID/PWD only for connection testing, not stored

    assertEquals("cache uid", configuration.getCacheCredentials().getUserName()); //UID for cache
    assertEquals("cache pwd", configuration.getCacheCredentials().getPassword()); //PWD for cache
  }
  */

}