/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.ctrl.template;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.sventon.web.command.BaseCommand;

import static org.junit.Assert.*;

public class ShowFileControllerTest {

  private ShowFileController ctrl;

  @Before
  public void setUp() throws Exception {
    final ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();
    fileTypeMap.afterPropertiesSet();
    ctrl = new ShowFileController(fileTypeMap);
    ctrl.setArchiveFileExtensionPattern("(jar|zip|war|ear)");
  }

  @Test
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

  @Test
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

  @Test
  public void testIsImageFileExtension() throws Exception {
    final BaseCommand command = new BaseCommand();
    command.setPath("file.gif");
    assertTrue(ctrl.isImageFileExtension(command));
  }
}