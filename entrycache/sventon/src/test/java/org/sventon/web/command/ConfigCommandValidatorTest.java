package org.sventon.web.command;

import junit.framework.TestCase;
import org.springframework.mock.web.MockServletContext;
import org.springframework.validation.BindException;
import org.sventon.TestUtils;
import org.sventon.appl.Application;
import org.sventon.appl.ConfigDirectory;
import static org.sventon.web.command.ConfigCommand.AccessMethod.USER;

public class ConfigCommandValidatorTest extends TestCase {

  public void testSupports() {
    final ConfigCommandValidator validator = new ConfigCommandValidator(false);
    assertTrue(validator.supports(ConfigCommand.class));
  }

  public void testValidate() throws Exception {
    final ConfigDirectory configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);
    final Application application = new Application(configDirectory, TestUtils.CONFIG_FILE_NAME);

    final ConfigCommandValidator validator = new ConfigCommandValidator(false);
    validator.setApplication(application);

    final ConfigCommand command = new ConfigCommand();

    BindException exception = new BindException(command, "test");
    validator.validate(command, exception);

    // An empty command is valid
    assertEquals(0, exception.getAllErrors().size());

    // Invalid repository name
    command.setName("Illegal whitespace in name");
    command.setRepositoryUrl("svn://domain.com/svn/");
    command.setUserPassword("");
    command.setUserName("");
    validator.validate(command, exception);
    assertEquals(1, exception.getAllErrors().size());
    assertEquals("config.error.illegal-name", exception.getFieldError("name").getCode());
    exception = new BindException(command, "test");

    // Empty name is not ok
    command.setName("");
    command.setRepositoryUrl("svn://domain.com/svn/");
    command.setUserPassword("");
    command.setUserName("");
    validator.validate(command, exception);
    assertEquals(1, exception.getAllErrors().size());
    assertEquals("config.error.illegal-name", exception.getFieldError("name").getCode());
    exception = new BindException(command, "test");

    // Valid (typical) input
    command.setRepositoryUrl("svn://domain.com/svn/");
    command.setName("default");
    command.setUserPassword("");
    command.setUserName("");
    command.setConnectionTestUid("");
    command.setConnectionTestPwd("");
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    // Valid input, spaces will be trimmed
    command.setRepositoryUrl(" svn://domain.com/svn/ ");
    command.setUserPassword("");
    command.setUserName("");
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setUserPassword(null);
    command.setUserName(null);
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setRepositoryUrl("");
    validator.validate(command, exception);
    assertEquals(1, exception.getAllErrors().size());
    assertEquals("config.error.illegal-url", exception.getFieldError("repositoryUrl").getCode());

    exception = new BindException(command, "test");
    command.setRepositoryUrl("notavalidurl");
    validator.validate(command, exception);
    assertEquals(1, exception.getAllErrors().size());
    assertEquals("config.error.illegal-url", exception.getFieldError("repositoryUrl").getCode());

    exception = new BindException(command, "test");
    command.setRepositoryUrl("svn://domain.com/svn/");
    command.setUserPassword("");
    command.setUserName("");
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    //if user based access is used, test connection uid and pwd can be supplied
    //(this is the tyical case)
    command.setRepositoryUrl("svn://domain.com/svn/");
    command.setName("default");
    command.setUserPassword("");
    command.setUserName("");
    command.setAccessMethod(USER);
    command.setConnectionTestUid("admin");
    command.setConnectionTestPwd("super-secret-pwd123");
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());
  }

}
