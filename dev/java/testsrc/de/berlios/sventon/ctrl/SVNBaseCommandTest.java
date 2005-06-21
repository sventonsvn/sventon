package de.berlios.sventon.ctrl;

import java.util.Map;

import junit.framework.TestCase;

public class SVNBaseCommandTest extends TestCase {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(SVNBaseCommandTest.class);
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

}
