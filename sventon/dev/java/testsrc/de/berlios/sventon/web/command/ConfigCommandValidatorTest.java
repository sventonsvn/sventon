package de.berlios.sventon.web.command;

import junit.framework.TestCase;
import org.springframework.validation.BindException;

public class ConfigCommandValidatorTest extends TestCase {

  public void testSupports() {
    ConfigCommandValidator validator = new ConfigCommandValidator(false);
    assertTrue(validator.supports(ConfigCommand.class));
  }

  public void testValidate() throws Exception {
    ConfigCommandValidator validator = new ConfigCommandValidator(false);

    ConfigCommand command = new ConfigCommand();

    BindException exception = new BindException(command, "test");
    validator.validate(command, exception);

    // An empty command is valid
    assertEquals(0, exception.getAllErrors().size());

    // Invalid repository instance name
    command.setName("Illegal!");
    command.setRepositoryURL("svn://domain.com/svn/");
    command.setPassword("");
    command.setUsername("");
    validator.validate(command, exception);
    assertEquals(1, exception.getAllErrors().size());
    assertEquals("config.error.illegal-name", exception.getFieldError("name").getCode());
    exception = new BindException(command, "test");

    // Empty name is not ok
    command.setName("");
    command.setRepositoryURL("svn://domain.com/svn/");
    command.setPassword("");
    command.setUsername("");
    validator.validate(command, exception);
    assertEquals(1, exception.getAllErrors().size());
    assertEquals("config.error.illegal-name", exception.getFieldError("name").getCode());
    exception = new BindException(command, "test");

    // Valid (typical) input
    command.setRepositoryURL("svn://domain.com/svn/");
    command.setName("default");
    command.setPassword("");
    command.setUsername("");
    command.setConnectionTestUsername("");
    command.setConnectionTestPassword("");
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    // Valid input, spaces will be trimmed
    command.setRepositoryURL(" svn://domain.com/svn/ ");
    command.setPassword("");
    command.setUsername("");
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setPassword(null);
    command.setUsername(null);
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setRepositoryURL("");
    validator.validate(command, exception);
    assertEquals(1, exception.getAllErrors().size());
    assertEquals("config.error.illegal-url", exception.getFieldError("repositoryURL").getCode());

    exception = new BindException(command, "test");
    command.setRepositoryURL("notavalidurl");
    validator.validate(command, exception);
    assertEquals(1, exception.getAllErrors().size());
    assertEquals("config.error.illegal-url", exception.getFieldError("repositoryURL").getCode());

    exception = new BindException(command, "test");
    command.setRepositoryURL("svn://domain.com/svn/");
    command.setPassword("");
    command.setUsername("");
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    //if user based access is used, test connection uid and pwd can be supplied
    //(this is the tyical case)
    command.setRepositoryURL("svn://domain.com/svn/");
    command.setName("default");
    command.setPassword("");
    command.setUsername("");
    command.setEnableAccessControl(true);
    command.setConnectionTestUsername("admin");
    command.setConnectionTestPassword("super-secret-pwd123");
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

  }

}
