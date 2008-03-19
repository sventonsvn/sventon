package de.berlios.sventon.web.command;

import de.berlios.sventon.appl.RepositoryConfiguration;
import junit.framework.TestCase;

public class ConfigCommandTest extends TestCase {

  public void testCreateRepositoryConfiguration() {

    final ConfigCommand command = new ConfigCommand();
    command.setName("testrepos");
    command.setRepositoryUrl("http://localhost");
    command.setAccessMethod(ConfigCommand.AccessMethod.GLOBAL);
    command.setCacheUsed(true);
    command.setZippedDownloadsAllowed(true);
    command.setConnectionTestUid("test uid");
    command.setConnectionTestPwd("test pwd");
    command.setUid("uid");
    command.setPwd("pwd");

    final RepositoryConfiguration configuration = command.createRepositoryConfiguration();
    assertEquals("http://localhost", configuration.getRepositoryUrl());
    assertFalse(configuration.isAccessControlEnabled());
    assertTrue(configuration.isCacheUsed());
    assertTrue(configuration.isZippedDownloadsAllowed());
    assertEquals("uid", configuration.getUid());
    assertEquals("pwd", configuration.getPwd());
  }


}