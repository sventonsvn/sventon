package de.berlios.sventon.command;

import junit.framework.TestCase;
import org.springframework.validation.BindException;
import de.berlios.sventon.command.ConfigCommand;
import de.berlios.sventon.command.ConfigCommandValidator;

public class ConfigCommandValidatorTest extends TestCase {

  public void testSupports() {
    ConfigCommandValidator validator = new ConfigCommandValidator();
    assertTrue(validator.supports(ConfigCommand.class));
  }

  public void testValidate() {
    ConfigCommandValidator validator = new ConfigCommandValidator();

    ConfigCommand command = new ConfigCommand();

    BindException exception = new BindException(command, "test");
    validator.validate(command, exception);

    // An empty base command is valid
    assertEquals(0, exception.getAllErrors().size());

    // Valid (typical) input
    command.setRepositoryURL("svn://domain.com/svn/");
    command.setConfigPath("");
    command.setCurrentDir("c:\\windows\\system");
    command.setPassword("");
    command.setUsername("");
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setPassword(null);
    command.setUsername(null);
    command.setConfigPath(null);
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setRepositoryURL("");
    validator.validate(command, exception);
    assertEquals(1, exception.getAllErrors().size());
    assertEquals("config.error.illegal-url" ,exception.getFieldError("repositoryURL").getCode());

    exception = new BindException(command, "test");
    command.setRepositoryURL("notavalidurl");
    validator.validate(command, exception);
    assertEquals(1, exception.getAllErrors().size());
    assertEquals("config.error.illegal-url" ,exception.getFieldError("repositoryURL").getCode());
  }

}
