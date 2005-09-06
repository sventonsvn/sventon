package de.berlios.sventon.ctrl;

import junit.framework.*;

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
    
    command.setPath("Drutten.java");
    assertEquals("Drutten.java", command.getPath());
    
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

  public void testGetTarget() {
    SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("trunk/src/File.java");
    assertEquals("File.java", command.getTarget());
    
    command.setPath("trunk/src/");
    assertEquals("src", command.getTarget());
    
    command.setPath("");
    assertEquals("", command.getTarget());
    
    command.setPath("/");
    assertEquals("", command.getTarget());
    
    command.setPath(null);
    assertEquals("", command.getTarget());
  }

  public void testGetPath() {
    SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("trunk/src/File.java");
    assertEquals("trunk/src/", command.getPathPart());
    
    command.setPath("trunk/src/");
    assertEquals("trunk/", command.getPathPart());
    
    command.setPath("");
    assertEquals("", command.getPathPart());
    
    command.setPath("/");
    assertEquals("", command.getPathPart());
    
    command.setPath(null);
    assertEquals("", command.getPathPart());
  }
  
  public void testAsMap() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("/trunk/src/File.java");
    command.setRevision("123");
    Map<String, Object> model = command.asModel();
    
    assertEquals("/trunk/src/File.java", model.get("path"));
    assertEquals("123", model.get("revision"));
  }

  public void testGetFileExtension() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("trunk/src/File.java");
    assertEquals("java", command.getFileExtension());

    command.setPath("trunk/src/File");
    assertEquals("", command.getFileExtension());

    command.setPath("trunk/src/.htpasswd");
    assertEquals("htpasswd", command.getFileExtension());
  }
}
