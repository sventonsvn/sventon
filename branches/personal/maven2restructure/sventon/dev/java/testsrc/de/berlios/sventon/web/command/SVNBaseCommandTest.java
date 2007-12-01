package de.berlios.sventon.web.command;

import junit.framework.TestCase;

import java.util.Map;

public class SVNBaseCommandTest extends TestCase {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(SVNBaseCommandTest.class);
  }

  public void testDefaultValues() {
    SVNBaseCommand command = new SVNBaseCommand();
    assertEquals("/", command.getPath());
    assertNull(command.getRevision());
  }

  public void testSetPath() {
    SVNBaseCommand command = new SVNBaseCommand();

    //null is OK, will be converted to "/"
    command.setPath(null);
    assertEquals("/", command.getPath());

    //"" (empty string) will also be converted to "/"
    command.setPath("");
    assertEquals("/", command.getPath());

    command.setPath("Asdf.java");
    assertEquals("Asdf.java", command.getPath());

  }

  public void testSetRevision() {
    SVNBaseCommand command = new SVNBaseCommand();
    command.setRevision(null);
    assertNull(command.getRevision());

    command.setRevision("2");
    assertEquals("2", command.getRevision());

    //Drutten is accepted as a revision here, but not by the SVNBaseCommandValidator
    command.setRevision("Drutten");
    assertEquals("Drutten", command.getRevision());

    //HEAD in different cases are converted to HEAD
    command.setRevision("HEAD");
    assertEquals("HEAD", command.getRevision());

    command.setRevision("head");
    assertEquals("HEAD", command.getRevision());

    command.setRevision("HEad");
    assertEquals("HEAD", command.getRevision());

  }

  public void testGetCompletePath() {
    SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("trunk/src/File.java");
    assertEquals("trunk/src/File.java", command.getPath());
  }

  public void testAsMap() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("/src/File.java");
    command.setRevision("123");
    Map<String, Object> model = command.asModel();

    assertEquals("/src/File.java", model.get("path"));
    assertEquals("123", model.get("revision"));
  }
}
