/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.command;

import de.berlios.sventon.appl.Instance;
import de.berlios.sventon.appl.InstanceConfiguration;
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
public final class ConfigCommandValidator implements Validator {

  /**
   * Logger for this class and subclasses
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Controls whether repository connection should be tested or not.
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
   *                       connection will not be tested.
   */
  protected ConfigCommandValidator(final boolean testConnection) {
    this.testConnection = testConnection;
  }

  /**
   * {@inheritDoc}
   */
  public boolean supports(final Class clazz) {
    return clazz.equals(ConfigCommand.class);
  }

  /**
   * {@inheritDoc}
   */
  public void validate(final Object obj, final Errors errors) {
    final ConfigCommand command = (ConfigCommand) obj;

    // Validate 'repository instance name'
    final String instanceName = command.getName();
    if (instanceName != null) {
      if (!Instance.isValidName(instanceName)) {
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
        final InstanceConfiguration configuration = new InstanceConfiguration(instanceName);
        configuration.setRepositoryRoot(trimmedURL);
        configuration.setUid(command.getUsername());
        configuration.setPwd(command.getPassword());
        try {
          final SVNRepository repos = RepositoryFactory.INSTANCE.getRepository(
              configuration.getSVNURL(), configuration.getUid(), configuration.getPwd());
          repos.testConnection();
        } catch (SVNException e) {
          logger.warn("Unable to connect to repository", e);
          errors.rejectValue("repositoryURL", "config.error.connection-error",
              "Unable to connect to repository. Check URL, user name and password.");
        }
      }
    }
  }

}
