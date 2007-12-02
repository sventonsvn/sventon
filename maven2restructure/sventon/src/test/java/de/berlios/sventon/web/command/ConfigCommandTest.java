package de.berlios.sventon.web.command;

import de.berlios.sventon.appl.InstanceConfiguration;
import junit.framework.TestCase;

public class ConfigCommandTest extends TestCase {

  public void testCreateInstanceConfiguration() {

    final ConfigCommand command = new ConfigCommand();
    command.setName("testrepos");
    command.setRepositoryUrl("http://localhost");
    command.setEnableAccessControl(false);
    command.setCacheUsed(true);
    command.setZippedDownloadsAllowed(true);
    command.setConnectionTestUid("test uid");
    command.setConnectionTestPwd("test pwd");
    command.setUid("uid");
    command.setPwd("pwd");

    final InstanceConfiguration configuration = command.createInstanceConfiguration();
    assertEquals("http://localhost", configuration.getRepositoryUrl());
    assertFalse(configuration.isAccessControlEnabled());
    assertTrue(configuration.isCacheUsed());
    assertTrue(configuration.isZippedDownloadsAllowed());
    assertEquals("uid", configuration.getUid());
    assertEquals("pwd", configuration.getPwd());
  }


}