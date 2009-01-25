package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.sventon.web.command.SVNBaseCommand;

public class ShowFileControllerTest extends TestCase {

  private ShowFileController ctrl;

  protected void setUp() throws Exception {
    ctrl = new ShowFileController();
    final ConfigurableMimeFileTypeMap mftm = new ConfigurableMimeFileTypeMap();
    mftm.afterPropertiesSet();
    ctrl.setMimeFileTypeMap(mftm);
    ctrl.setArchiveFileExtensionPattern("(jar|zip|war|ear)");
  }

  public void testIsArchiveFileExtension() throws Exception {
    final SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("/file.zip");
    assertTrue(ctrl.isArchiveFileExtension(command));
    command.setPath("/file.jar");
    assertTrue(ctrl.isArchiveFileExtension(command));
    command.setPath("/file.war");
    assertTrue(ctrl.isArchiveFileExtension(command));
    command.setPath("/file.ear");
    assertTrue(ctrl.isArchiveFileExtension(command));
    command.setPath("/file.EAR");
    assertTrue(ctrl.isArchiveFileExtension(command));
    command.setPath("/file");
    assertFalse(ctrl.isArchiveFileExtension(command));
    command.setPath("/filezip");
    assertFalse(ctrl.isArchiveFileExtension(command));
    command.setPath("");
    assertFalse(ctrl.isArchiveFileExtension(command));
  }

  public void testConfigurableMimeFileTypeMap() {
    final ConfigurableMimeFileTypeMap ftm = new ConfigurableMimeFileTypeMap();
    ftm.afterPropertiesSet();

    assertEquals("application/octet-stream", ftm.getContentType("file.abc"));
    assertEquals("application/zip", ftm.getContentType("file.zip"));
    assertEquals("image/jpeg", ftm.getContentType("file.jpg"));
    assertEquals("image/jpeg", ftm.getContentType("file.jpe"));
    assertEquals("image/jpeg", ftm.getContentType("file.jpeg"));
    assertEquals("image/gif", ftm.getContentType("file.gif"));
    assertEquals("image/x-png", ftm.getContentType("file.png"));
    assertEquals("application/octet-stream", ftm.getContentType("filenamejpg"));
    assertEquals("image/gif", ftm.getContentType("/dir/file.gif"));
  }

  public void testIsImageFileExtension() throws Exception {
    final SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("file.gif");
    assertTrue(ctrl.isImageFileExtension(command));
  }
}