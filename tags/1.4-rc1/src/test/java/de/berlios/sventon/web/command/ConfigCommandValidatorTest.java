package de.berlios.sventon.web.command;

import junit.framework.TestCase;
import org.springframework.validation.BindException;
import static de.berlios.sventon.web.command.ConfigCommand.AccessMethod.USER;

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
    command.setRepositoryUrl("svn://domain.com/svn/");
    command.setPwd("");
    command.setUid("");
    validator.validate(command, exception);
    assertEquals(1, exception.getAllErrors().size());
    assertEquals("config.error.illegal-name", exception.getFieldError("name").getCode());
    exception = new BindException(command, "test");

    // Empty name is not ok
    command.setName("");
    command.setRepositoryUrl("svn://domain.com/svn/");
    command.setPwd("");
    command.setUid("");
    validator.validate(command, exception);
    assertEquals(1, exception.getAllErrors().size());
    assertEquals("config.error.illegal-name", exception.getFieldError("name").getCode());
    exception = new BindException(command, "test");

    // Valid (typical) input
    command.setRepositoryUrl("svn://domain.com/svn/");
    command.setName("default");
    command.setPwd("");
    command.setUid("");
    command.setConnectionTestUid("");
    command.setConnectionTestPwd("");
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    // Valid input, spaces will be trimmed
    command.setRepositoryUrl(" svn://domain.com/svn/ ");
    command.setPwd("");
    command.setUid("");
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setPwd(null);
    command.setUid(null);
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
    command.setPwd("");
    command.setUid("");
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    //if user based access is used, test connection uid and pwd can be supplied
    //(this is the tyical case)
    command.setRepositoryUrl("svn://domain.com/svn/");
    command.setName("default");
    command.setPwd("");
    command.setUid("");
    command.setAccessMethod(USER);
    command.setConnectionTestUid("admin");
    command.setConnectionTestPwd("super-secret-pwd123");
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

  }

}
