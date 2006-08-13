/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.command;

import de.berlios.sventon.config.InstanceConfiguration;
import de.berlios.sventon.repository.RepositoryFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * ConfigCommandValidator.
 *
 * @author jesper@users.berlios.de
 */
public class ConfigCommandValidator implements Validator {

  /**
   * Logger for this class and subclasses
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * Controls whether repository connection should be tested or not.
   */
  private boolean testConnection = true;

  /**
   * Sventon temporary config path.
   */
  private String configPath;

  /**
   * Constructor.
   */
  public ConfigCommandValidator() {
  }

  /**
   * Constructor for testing purposes.
   *
   * @param testConnection If <tt>false</tt> repository
   *                       connection will not be tested.
   */
  protected ConfigCommandValidator(boolean testConnection) {
    this.testConnection = testConnection;
  }

  /**
   * Sets the config path where temporary files will be stored.
   *
   * @param configPath The path
   */
  public void setConfigPath(final String configPath) {
    this.configPath = configPath;
  }

  public boolean supports(Class clazz) {
    return clazz.equals(ConfigCommand.class);
  }

  public void validate(Object obj, Errors errors) {
    final ConfigCommand command = (ConfigCommand) obj;

    // Validate 'repository instance name'
    final String instanceName = command.getName();
    if (instanceName != null) {
      if (!instanceName.matches("[a-z0-9]+")) {
        final String msg = "Name must be in lower case a-z and/or 0-9";
        logger.warn(msg);
        errors.rejectValue("name", "config.error.illegal-name", msg);
      }
    }

    // Validate 'repositoryURL', 'username' and 'password'
    final String repositoryURL = command.getRepositoryURL();

    if (repositoryURL != null) {
      final String trimmedURL = repositoryURL.trim();
      SVNURL url = null;
      try {
        url = SVNURL.parseURIDecoded(trimmedURL);
      } catch (SVNException ex) {
        final String msg = "Invalid repository URL: " + repositoryURL;
        logger.warn(msg);
        errors.rejectValue("repositoryURL", "config.error.illegal-url", msg);
      }
      if (url != null && testConnection) {
        logger.info("Testing repository connection");
        final InstanceConfiguration instanceConfiguration = new InstanceConfiguration();
        instanceConfiguration.setInstanceName(instanceConfiguration.getInstanceName());
        instanceConfiguration.setRepositoryRoot(trimmedURL);
        instanceConfiguration.setConfiguredUID(command.getUsername());
        instanceConfiguration.setConfiguredPWD(command.getPassword());
        try {
          final SVNRepository repos = RepositoryFactory.INSTANCE.getRepository(instanceConfiguration, configPath);
          repos.testConnection();
        } catch (SVNException e) {
          logger.warn("Unable to connect to repository", e);
          errors.rejectValue("repositoryURL", "config.error.connection-error", "Unable to connect to repository. Check URL, user name and password.");
        }
      }
    }
  }

}
