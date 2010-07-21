package org.sventon.web.command;

import junit.framework.TestCase;
import org.sventon.model.RepositoryName;
import org.sventon.model.Revision;

public class BaseCommandTest extends TestCase {

  public void testDefaultValues() {
    final BaseCommand command = new BaseCommand();
    assertEquals("/", command.getPath());
    assertEquals(Revision.HEAD, command.getRevision());
  }

  public void testSetPath() {
    final BaseCommand command = new BaseCommand();

    //null is OK, will be converted to "/"
    command.setPath(null);
    assertEquals("/", command.getPath());

    //"" (empty string) will also be converted to "/"
    command.setPath("");
    assertEquals("/", command.getPath());

    command.setPath("Asdf.java");
    assertEquals("/Asdf.java", command.getPath());
  }

  public void testSetRevision() {
    final BaseCommand command = new BaseCommand();
    try {
      command.setRevision(null);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // expected
    }
    assertEquals(Revision.HEAD, command.getRevision());

    command.setRevision(Revision.parse("2"));
    assertEquals(Revision.create(2), command.getRevision());

    //Drutten is accepted as a revision here, but not by the BaseCommandValidator
    command.setRevision(Revision.parse("Drutten"));
    assertEquals(Revision.UNDEFINED, command.getRevision());

    //HEAD in different cases are converted to HEAD
    command.setRevision(Revision.parse("HEAD"));
    assertEquals(Revision.HEAD, command.getRevision());

    command.setRevision(Revision.parse("head"));
    assertEquals(Revision.HEAD, command.getRevision());

    command.setRevision(Revision.parse("HEad"));
    assertEquals(Revision.HEAD, command.getRevision());
  }

  public void testGetCompletePath() {
    final BaseCommand command = new BaseCommand();
    command.setPath("trunk/src/File.java");
    assertEquals("/trunk/src/File.java", command.getPath());
  }

  public void testGetParentPath() {
    final BaseCommand cmd = new BaseCommand();

    cmd.setPath("/trunk/src/File.java");
    assertEquals("/trunk/src/", cmd.getParentPath());

    cmd.setPath("/trunk/src/File.java");
    assertEquals("/trunk/src/", cmd.getParentPath());

    cmd.setPath("/trunk/src/");
    assertEquals("/trunk/", cmd.getParentPath());

    cmd.setPath("");
    assertEquals("/", cmd.getParentPath());

    cmd.setPath("/");
    assertEquals("/", cmd.getParentPath());

    cmd.setPath(null);
    assertEquals("/", cmd.getParentPath());
  }

  public void testGetPathPart() {
    final BaseCommand cmd = new BaseCommand();

    cmd.setPath("/trunk/src/File.java");
    assertEquals("/trunk/src/", cmd.getPathPart());

    cmd.setPath("/trunk/src/");
    assertEquals("/trunk/src/", cmd.getPathPart());

    cmd.setPath("/trunk/src");
    assertEquals("/trunk/", cmd.getPathPart());  // 'src' will be treated as a target leaf

    cmd.setPath("");
    assertEquals("/", cmd.getPathPart());

    cmd.setPath("/");
    assertEquals("/", cmd.getPathPart());

    cmd.setPath(null);
    assertEquals("/", cmd.getPathPart());
  }

  public void testCreateListUrl() {
    final BaseCommand cmd = new BaseCommand();
    cmd.setName(new RepositoryName("test"));

    cmd.setPath("/test/dir");
    assertEquals("/repos/test/list/test/dir/", cmd.createListUrl());

    cmd.setPath("/test/dir/");
    assertEquals("/repos/test/list/test/dir/", cmd.createListUrl());
  }

  public void testCreateShowFileUrl() {
    final BaseCommand cmd = new BaseCommand();
    cmd.setName(new RepositoryName("test"));

    cmd.setPath("/test/file");
    assertEquals("/repos/test/show/test/file", cmd.createShowFileUrl());

    cmd.setPath("/test/file/");
    assertEquals("/repos/test/show/test/file", cmd.createShowFileUrl());
  }

}
