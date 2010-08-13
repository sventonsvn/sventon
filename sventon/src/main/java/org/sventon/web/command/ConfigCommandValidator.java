/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.sventon.*;
import org.sventon.appl.Application;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.Credentials;
import org.sventon.model.RepositoryName;
import org.sventon.model.SVNURL;

/**
 * ConfigCommandValidator.
 *
 * @author jesper@sventon.org
 * @author patrik@sventon.org
 */
public final class ConfigCommandValidator implements Validator {

  /**
   * Logger for this class.
   */
  private final Log logger = LogFactory.getLog(getClass().getName());

  /**
   * Controls whether repository connection should be tested or not.
   */
  private boolean testConnections = true;

  /**
   * The repository factory.
   */
  private SVNConnectionFactory connectionFactory;

  /**
   * The application.
   */
  private Application application;

  /**
   * Constructor.
   */
  public ConfigCommandValidator() {
  }

  /**
   * Constructor for testing purposes.
   *
   * @param testConnections If <tt>false</tt> repository
   *                        connection will not be tested.
   */
  protected ConfigCommandValidator(final boolean testConnections) {
    this.testConnections = testConnections;
  }

  /**
   * Sets the application.
   *
   * @param application Application
   */
  @Autowired
  public void setApplication(final Application application) {
    this.application = application;
  }

  public boolean supports(final Class clazz) {
    return clazz.equals(ConfigCommand.class);
  }

  /**
   * Sets the connection factory instance.
   *
   * @param connectionFactory Factory instance.
   */
  @Autowired
  public void setConnectionFactory(final SVNConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  public void validate(final Object obj, final Errors errors) {
    final ConfigCommand command = (ConfigCommand) obj;

    final String repositoryUrl = command.getRepositoryUrl();

    if (command.getName() != null && repositoryUrl != null) {
      if (!RepositoryName.isValid(command.getName())) {
        errors.rejectValue("name", "config.error.illegal-name");
      } else if (application.getRepositoryNames().contains(new RepositoryName(command.getName()))) {
        errors.rejectValue("name", "config.error.duplicate-name");
      } else {
        SVNURL url = null;
        try {
          url = SVNURL.parse(repositoryUrl);
        } catch (SventonException e) {
          logger.info(e);
          errors.rejectValue("repositoryUrl", "config.error.illegal-url");
        }
        if (url != null && testConnections) {
          Credentials credentials;
          final RepositoryName repositoryName = new RepositoryName(command.getName());

          // Check if it's possible to connect to the repos given the user credentials.
          try {
            credentials = new Credentials(command.getUserName(), command.getUserPassword());
            testConnection(repositoryName, repositoryUrl, credentials);
          } catch (AuthenticationException e) {
            errors.rejectValue("accessMethod", "config.error.authentication-error");
          } catch (SventonException e) {
            errors.rejectValue("repositoryUrl", "config.error.connection-error", new String[]{repositoryUrl},
                "Unable to connect to repository [" + repositoryUrl + "]. Check URL.");
          }

          // If cache is used - check cache account access.
          if (command.isCacheUsed()) {
            try {
              credentials = new Credentials(command.getCacheUserName(), command.getCacheUserPassword());
              testConnection(repositoryName, repositoryUrl, credentials);
            } catch (SventonException e) {
              errors.rejectValue("cacheUsed", "config.error.authentication-error");
            }
          }
        }
      }
    }
  }

  private void testConnection(final RepositoryName repositoryName, final String repositoryUrl,
                              final Credentials credentials) throws SventonException {

    final RepositoryConfiguration configuration = new RepositoryConfiguration(repositoryName.toString());
    configuration.setRepositoryUrl(repositoryUrl);

    SVNConnection connection = null;
    try {
      connection = connectionFactory.createConnection(repositoryName, configuration.getSVNURL(),
          credentials);
      connection.getDelegate().testConnection();
    } catch (org.tmatesoft.svn.core.SVNException ex) {
      logger.info(ex);
      throw new SventonException(ex.getMessage());
    } finally {
      if (connection != null) {
        connection.closeSession();
      }
    }
  }
}
