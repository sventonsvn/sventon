package de.berlios.sventon.command;

import junit.framework.TestCase;
import org.springframework.validation.BindException;

import java.io.File;

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

    exception = new BindException(command, "test");
    command.setRepositoryURL("svn://domain.com/svn/");
    command.setConfigPath("c:");
    command.setCurrentDir("c:\\windows\\system");
    command.setPassword("");
    command.setUsername("");
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    exception = new BindException(command, "test");
    command.setRepositoryURL("svn://domain.com/svn/");
    command.setConfigPath("c:\\");
    command.setCurrentDir("c:\\windows\\system");
    command.setPassword("");
    command.setUsername("");
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    File tempFile = File.createTempFile("sventon-test", null);
    exception = new BindException(command, "test");
    command.setRepositoryURL("svn://domain.com/svn/");
    command.setConfigPath(tempFile.getAbsolutePath());
    command.setCurrentDir("c:\\windows\\system");
    command.setPassword("");
    command.setUsername("");
    validator.validate(command, exception);
    assertEquals(1, exception.getAllErrors().size());
    assertEquals("config.error.illegal-path" ,exception.getFieldError("configPath").getCode());
    tempFile.delete();
  }

}
