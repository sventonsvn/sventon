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
    command.setCacheUserName("cache uid");
    command.setCacheUserPassword("cache pwd");
    command.setUserName("uid");
    command.setUserPassword("pwd");

    final RepositoryConfiguration configuration = command.createRepositoryConfiguration();
    assertEquals("http://localhost", configuration.getRepositoryUrl());
    assertFalse(configuration.isAccessControlEnabled());
    assertTrue(configuration.isCacheUsed());
    assertTrue(configuration.isZippedDownloadsAllowed());
    assertEquals("uid", configuration.getUserCredentials().getUserName());
    assertEquals("pwd", configuration.getUserCredentials().getPassword());

    assertEquals("cache uid", configuration.getCacheCredentials().getUserName());
    assertEquals("cache pwd", configuration.getCacheCredentials().getPassword());
  }

}