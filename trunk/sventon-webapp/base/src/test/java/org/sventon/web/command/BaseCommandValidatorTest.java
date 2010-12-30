/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.command;

import org.junit.Test;
import org.springframework.validation.BindException;
import org.sventon.model.DirEntryComparator;
import org.sventon.model.DirEntrySorter;
import org.sventon.model.Revision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BaseCommandValidatorTest {

  @Test
  public void testSupports() {
    BaseCommandValidator validator = new BaseCommandValidator();
    assertTrue(validator.supports(BaseCommand.class));
  }

  @Test
  public void testValidate() {
    BaseCommandValidator validator = new BaseCommandValidator();

    BaseCommand command = new BaseCommand();

    BindException exception = new BindException(command, "test");

    validator.validate(command, exception);

    // An empty base command is valid
    assertEquals(0, exception.getAllErrors().size());

    // Valid (typical) input
    command.setPath("/test/Test.java");
    command.setRevision(Revision.parse("12"));

    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    // Valid (typical) input
    command.setPath("/test/Test.java");
    command.setRevision(Revision.parse("12"));

    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    //Both HEAD and head (and HeAd) are valid revisions. These are not really
    //accepted by the validator in any other form than HEAD, but other case variations
    //are automatically converted when set using the setRevision method on BaseCommand
    command.setRevision(Revision.parse("HEAD"));
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setRevision(Revision.parse("{2007-01-01}"));
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setRevision(Revision.parse("head "));
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setRevision(Revision.parse("HEad"));
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setRevision(Revision.parse(" 123 "));
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    command.setRevision(Revision.parse("{2007-01-01}"));
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    // Illegal date format
//    command.setRevision(Revision.parse("{2007-01}"));
//    validator.validate(command, exception);
//    assertEquals(1, exception.getAllErrors().size());

    //Other non numerical revisions are however not allowed
    command.setRevision(Revision.parse("2007-01-01"));
    validator.validate(command, exception);
    assertEquals(1, exception.getAllErrors().size());
    assertEquals("browse.error.illegal-revision", exception.getFieldError("revision").getCode());

    exception = new BindException(command, "test2");
    command.setRevision(Revision.create(1));
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    exception = new BindException(command, "test2");
    command.setSortMode(null);
    command.setSortType(null);
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

    exception = new BindException(command, "test2");
    command.setSortMode(DirEntrySorter.SortMode.ASC);
    command.setSortType(DirEntryComparator.SortType.SIZE);
    validator.validate(command, exception);
    assertEquals(0, exception.getAllErrors().size());

  }

}
