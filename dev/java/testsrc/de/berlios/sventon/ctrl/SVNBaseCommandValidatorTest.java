package de.berlios.sventon.ctrl;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import junit.framework.TestCase;

public class SVNBaseCommandValidatorTest extends TestCase {

  public void testSupports() {
    SVNBaseCommandValidator validator = new SVNBaseCommandValidator();
    assertTrue(validator.supports(SVNBaseCommand.class));
  }

  public void testValidate() {
    SVNBaseCommandValidator validator = new SVNBaseCommandValidator();

    SVNBaseCommand command = new SVNBaseCommand();

    BindException exception = new BindException(command, "test");

    validator.validate(command, exception);

    // An empty base command is valid
    assertEquals(0, exception.getAllErrors().size());

    // Valid (typical) input
    command.setPath("/test/Test.java");
    command.setRevision("12");

    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    // Valid (typical) input
    command.setPath("/test/Test.java");
    command.setRevision("12");

    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());
    
    //Both HEAD and head (and HeAd) are valid revisions. These are not really
    //accepted by the validator in any other form than HEAD, but other case variations
    //are automatically converted when set using the setRevision method on SVNBaseCommand
    command.setRevision("HEAD");

    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());
    
    command.setRevision("head");
 
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());
    
    command.setRevision("HEad");

    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());
    
    //Other non numerical revisions are however not allowed
    command.setRevision("TEST");

    validator.validate(command, exception);
    assertEquals(1, exception.getAllErrors().size());
    assertEquals("browse.error.illegal-revision" ,exception.getFieldError("revision").getCode());

  }

}
