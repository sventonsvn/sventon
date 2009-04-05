package org.sventon.web.command;

import junit.framework.TestCase;
import org.sventon.appl.RepositoryConfiguration;

public class ConfigCommandTest extends TestCase {

  public void testCreateRepositoryConfiguration() {

    final ConfigCommand command = new ConfigCommand();
    command.setName("testrepos");
    command.setRepositoryUrl("http://localhost");
    command.setAccessMethod(ConfigCommand.AccessMethod.SHARED);
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
    assertEquals("uid", configuration.getCredentials().getUsername());
    assertEquals("pwd", configuration.getCredentials().getPassword());
  }


}