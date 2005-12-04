package de.berlios.sventon.command;

import junit.framework.TestCase;

import java.util.Map;

public class SVNBaseCommandTest extends TestCase {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(SVNBaseCommandTest.class);
  }

  public void testDefaultValues() {
    SVNBaseCommand command = new SVNBaseCommand();
    assertEquals("/", command.getCompletePath());
    assertNull(command.getRevision());
  }

  public void testSetPath() {
    SVNBaseCommand command = new SVNBaseCommand();

    //null is OK, will be converted to "/"
    command.setPath(null);
    assertEquals("/", command.getCompletePath());

    //"" (empty string) will also be converted to "/"
    command.setPath("");
    assertEquals("/", command.getCompletePath());

    command.setPath("Asdf.java");
    assertEquals("Asdf.java", command.getCompletePath());

    command.setMountPoint("/trunk");
    command.setPath("/src/Asdf.java");
    assertEquals("/trunk/src/Asdf.java", command.getCompletePath());
    assertEquals("/src/Asdf.java", command.getPath());

    command.setPath(""); //<- "" is converted to /
    assertEquals("/trunk/", command.getCompletePath());
    assertEquals("/", command.getPath()); //<- converted

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
    assertEquals("trunk/src/File.java", command.getCompletePath());
  }

  public void testAsMap() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("/src/File.java");
    command.setRevision("123");
    command.setMountPoint("/trunk");
    Map<String, Object> model = command.asModel();

    assertEquals("/src/File.java", model.get("path"));
    assertEquals("123", model.get("revision"));
    assertEquals("/trunk/src/File.java", model.get("completePath"));
  }

  public void testGetMountPoint() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("/src/File.java");
    command.setMountPoint("/trunk");
    assertEquals("/trunk", command.getMountPoint(false));
    assertEquals("trunk", command.getMountPoint(true));
  }
}
