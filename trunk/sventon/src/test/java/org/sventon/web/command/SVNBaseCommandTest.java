package org.sventon.web.command;

import junit.framework.TestCase;
import org.sventon.SVNRepositoryStub;
import org.sventon.model.RepositoryName;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.Date;

public class SVNBaseCommandTest extends TestCase {

  public void testDefaultValues() {
    final SVNBaseCommand command = new SVNBaseCommand();
    assertEquals("/", command.getPath());
    assertEquals(SVNRevision.HEAD, command.getRevision());
  }

  public void testSetPath() {
    final SVNBaseCommand command = new SVNBaseCommand();

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
    final SVNBaseCommand command = new SVNBaseCommand();
    try {
      command.setRevision(null);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // expected
    }
    assertEquals(SVNRevision.HEAD, command.getRevision());

    command.setRevision(SVNRevision.parse("2"));
    assertEquals(SVNRevision.create(2), command.getRevision());

    //Drutten is accepted as a revision here, but not by the SVNBaseCommandValidator
    command.setRevision(SVNRevision.parse("Drutten"));
    assertEquals(SVNRevision.UNDEFINED, command.getRevision());

    //HEAD in different cases are converted to HEAD
    command.setRevision(SVNRevision.parse("HEAD"));
    assertEquals(SVNRevision.HEAD, command.getRevision());

    command.setRevision(SVNRevision.parse("head"));
    assertEquals(SVNRevision.HEAD, command.getRevision());

    command.setRevision(SVNRevision.parse("HEad"));
    assertEquals(SVNRevision.HEAD, command.getRevision());
  }

  public void testGetCompletePath() {
    final SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("trunk/src/File.java");
    assertEquals("/trunk/src/File.java", command.getPath());
  }

  public void testTranslateRevision() throws Exception {
    final SVNBaseCommand command = new SVNBaseCommand();
    command.setRevision(SVNRevision.parse("head"));
    command.translateRevision(100, null);
    assertEquals(SVNRevision.HEAD, command.getRevision());
    assertEquals(100, command.getRevisionNumber());

    command.setRevision(SVNRevision.parse(""));
    command.translateRevision(100, null);
    assertEquals(SVNRevision.UNDEFINED, command.getRevision());
    assertEquals(100, command.getRevisionNumber());

    command.setRevision(SVNRevision.parse("123"));
    command.translateRevision(200, null);
    assertEquals(SVNRevision.create(123), command.getRevision());

    command.setRevision(SVNRevision.parse("{2007-01-01}"));
    command.translateRevision(200, new SVNRepositoryStub(null, null) {
      public long getDatedRevision(final Date date) throws SVNException {
        return 123;
      }
    });
    assertEquals(123, command.getRevisionNumber());
  }

  public void testGetParentPath() {
    final SVNBaseCommand cmd = new SVNBaseCommand();

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
    final SVNBaseCommand cmd = new SVNBaseCommand();

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

  public void testCreateBrowseUrl() {
    final SVNBaseCommand cmd = new SVNBaseCommand();
    cmd.setName(new RepositoryName("test"));

    cmd.setPath("/test/dir");
    assertEquals("/repos/test/browse/test/dir/", cmd.createBrowseUrl());

    cmd.setPath("/test/dir/");
    assertEquals("/repos/test/browse/test/dir/", cmd.createBrowseUrl());
  }

  public void testCreateViewUrl() {
    final SVNBaseCommand cmd = new SVNBaseCommand();
    cmd.setName(new RepositoryName("test"));

    cmd.setPath("/test/file");
    assertEquals("/repos/test/view/test/file", cmd.createViewUrl());

    cmd.setPath("/test/file/");
    assertEquals("/repos/test/view/test/file", cmd.createViewUrl());
  }

}
