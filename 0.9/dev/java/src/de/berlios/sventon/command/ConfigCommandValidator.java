/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.command;

import de.berlios.sventon.ctrl.RepositoryConfiguration;
import de.berlios.sventon.svnsupport.RepositoryFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.io.File;

/**
 * ConfigCommandValidator.
 * @author jesper@users.berlios.de
 */
public class ConfigCommandValidator implements Validator {

  /** Logger for this class and subclasses */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * Controls wether repository connection should be tested or not.
   */
  private boolean testConnection = true;

  /**
   * Constructor.
   */
  public ConfigCommandValidator() {
  }

  /**
   * Constructor for testing purposes.
   *
   * @param testConnection If <tt>false</tt> repository
   * connection will not be tested.
   */
  protected ConfigCommandValidator(boolean testConnection) {
    this.testConnection = testConnection;
  }

  public boolean supports(Class clazz) {
    return clazz.equals(ConfigCommand.class);
  }

  public void validate(Object obj, Errors errors) {
    ConfigCommand command = (ConfigCommand) obj;

    // Validate the 'configPath'
    String configPath = command.getConfigPath();
    if (configPath != null) {
      if (configPath.equals("")) {
        configPath += System.getProperty("file.separator");
      }
      File configDir = new File(configPath);
      if (!configDir.isDirectory()) {
        errors.rejectValue("configPath", "config.error.illegal-path", "'" + configPath + "' is not a directory.");
      } else {
        if (!configDir.canWrite()) {
          errors.rejectValue("configPath", "config.error.no-write-permission", "No write permission to path '" + configPath + "'.");
        }
      }
    }

    // Validate 'repositoryURL', 'username' and 'password'
    String repositoryURL = command.getRepositoryURL();

    if (repositoryURL != null) {
      SVNURL url = null;
      try {
        url = SVNURL.parseURIDecoded(repositoryURL);
      } catch (SVNException ex) {
        errors.rejectValue("repositoryURL", "config.error.illegal-url", "Invalid repository URL.");
      }
      if (url != null && testConnection) {
        RepositoryConfiguration config = new RepositoryConfiguration();
        config.setRepositoryRoot(repositoryURL);
        config.setConfiguredUID(command.getUsername());
        config.setConfiguredUID(command.getPassword());
        config.setSVNConfigurationPath(command.getConfigPath());
        try {
          SVNRepository repos = RepositoryFactory.INSTANCE.getRepository(config);
          repos.testConnection();
        } catch (SVNException e) {
          errors.rejectValue("repositoryURL", "config.error.connection-error", "Unable to connect to repository. Check URL, user name and password.");
        }
      }
    }
  }

}
