package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.sventon.web.command.BaseCommand;

public class ShowFileControllerTest extends TestCase {

  private ShowFileController ctrl;

  protected void setUp() throws Exception {
    final ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();
    fileTypeMap.afterPropertiesSet();
    ctrl = new ShowFileController(fileTypeMap);
    ctrl.setArchiveFileExtensionPattern("(jar|zip|war|ear)");
  }

  public void testIsArchiveFileExtension() throws Exception {
    final BaseCommand command = new BaseCommand();
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
    final ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();
    fileTypeMap.afterPropertiesSet();

    assertEquals("application/octet-stream", fileTypeMap.getContentType("file.abc"));
    assertEquals("application/zip", fileTypeMap.getContentType("file.zip"));
    assertEquals("image/jpeg", fileTypeMap.getContentType("file.jpg"));
    assertEquals("image/jpeg", fileTypeMap.getContentType("file.jpe"));
    assertEquals("image/jpeg", fileTypeMap.getContentType("file.jpeg"));
    assertEquals("image/gif", fileTypeMap.getContentType("file.gif"));
    assertEquals("image/x-png", fileTypeMap.getContentType("file.png"));
    assertEquals("application/octet-stream", fileTypeMap.getContentType("filenamejpg"));
    assertEquals("image/gif", fileTypeMap.getContentType("/dir/file.gif"));
  }

  public void testIsImageFileExtension() throws Exception {
    final BaseCommand command = new BaseCommand();
    command.setPath("file.gif");
    assertTrue(ctrl.isImageFileExtension(command));
  }
}