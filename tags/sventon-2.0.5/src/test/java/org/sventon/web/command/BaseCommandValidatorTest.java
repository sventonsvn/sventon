package org.sventon.web.command;

import junit.framework.TestCase;
import org.springframework.validation.BindException;
import org.sventon.util.RepositoryEntryComparator;
import org.sventon.util.RepositoryEntrySorter;
import org.tmatesoft.svn.core.wc.SVNRevision;

public class BaseCommandValidatorTest extends TestCase {

  public void testSupports() {
    BaseCommandValidator validator = new BaseCommandValidator();
    assertTrue(validator.supports(BaseCommand.class));
  }

  public void testValidate() {
    BaseCommandValidator validator = new BaseCommandValidator();

    BaseCommand command = new BaseCommand();

    BindException exception = new BindException(command, "test");

    validator.validate(command, exception);

    // An empty base command is valid
    assertEquals(0, exception.getAllErrors().size());

    // Valid (typical) input
    command.setPath("/test/Test.java");
    command.setRevision(SVNRevision.parse("12"));

    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    // Valid (typical) input
    command.setPath("/test/Test.java");
    command.setRevision(SVNRevision.parse("12"));

    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    //Both HEAD and head (and HeAd) are valid revisions. These are not really
    //accepted by the validator in any other form than HEAD, but other case variations
    //are automatically converted when set using the setRevision method on BaseCommand
    command.setRevision(SVNRevision.parse("HEAD"));
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setRevision(SVNRevision.parse("{2007-01-01}"));
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setRevision(SVNRevision.parse("head "));
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setRevision(SVNRevision.parse("HEad"));
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setRevision(SVNRevision.parse(" 123 "));
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setRevision(SVNRevision.parse("{2007-01-01}"));
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    //Other non numerical revisions are however not allowed
    command.setRevision(SVNRevision.parse("2007-01-01"));
    validator.validate(command, exception);
    assertEquals(1, exception.getAllErrors().size());
    assertEquals("browse.error.illegal-revision", exception.getFieldError("revision").getCode());

    exception = new BindException(command, "test2");
    command.setRevision(SVNRevision.create(1));
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    exception = new BindException(command, "test2");
    command.setSortMode(null);
    command.setSortType(null);
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    exception = new BindException(command, "test2");
    command.setSortMode(RepositoryEntrySorter.SortMode.ASC);
    command.setSortType(RepositoryEntryComparator.SortType.SIZE);
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

  }

}
