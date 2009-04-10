/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.command;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.sventon.RepositoryConnectionFactory;
import org.sventon.appl.Application;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.Credentials;
import org.sventon.model.RepositoryName;
import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * ConfigCommandValidator.
 *
 * @author jesper@sventon.org
 * @author patrik@sventon.org
 */
public final class ConfigCommandValidator implements Validator {

  /**
   * Controls whether repository connection should be tested or not.
   */
  private boolean testConnections = true;

  /**
   * The repository factory.
   */
  private RepositoryConnectionFactory repositoryConnectionFactory;

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
  public void setApplication(final Application application) {
    this.application = application;
  }

  /**
   * {@inheritDoc}
   */
  public boolean supports(final Class clazz) {
    return clazz.equals(ConfigCommand.class);
  }

  /**
   * Sets the repository connection factory instance.
   *
   * @param repositoryConnectionFactory Factory instance.
   */
  public void setRepositoryConnectionFactory(final RepositoryConnectionFactory repositoryConnectionFactory) {
    this.repositoryConnectionFactory = repositoryConnectionFactory;
  }

  /**
   * {@inheritDoc}
   */
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
          url = SVNURL.parseURIDecoded(repositoryUrl);
        } catch (SVNException ex) {
          errors.rejectValue("repositoryUrl", "config.error.illegal-url");
        }
        if (url != null && testConnections) {
          Credentials credentials;
          final RepositoryName repositoryName = new RepositoryName(command.getName());

          // Check if it's possible to connect to the repos given the user credentials.
          try {
            credentials = new Credentials(command.getUserName(), command.getUserPassword());
            testConnection(repositoryName, repositoryUrl, credentials);
          } catch (SVNAuthenticationException e) {
            errors.rejectValue("accessMethod", "config.error.authentication-error");
          } catch (SVNException e) {
            errors.rejectValue("repositoryUrl", "config.error.connection-error", new String[]{repositoryUrl},
                "Unable to connect to repository [" + repositoryUrl + "]. Check URL.");
          }

          // If cache is used - check cache account access.
          if (command.isCacheUsed()) {
            try {
              credentials = new Credentials(command.getCacheUserName(), command.getCacheUserPassword());
              testConnection(repositoryName, repositoryUrl, credentials);
            } catch (SVNException e) {
              errors.rejectValue("cacheUsed", "config.error.authentication-error");
            }
          }
        }
      }
    }
  }

  private void testConnection(final RepositoryName repositoryName, final String repositoryUrl,
                              final Credentials credentials) throws SVNException {

    final RepositoryConfiguration configuration = new RepositoryConfiguration(repositoryName.toString());
    configuration.setRepositoryUrl(repositoryUrl);

    SVNRepository repository = null;
    try {
      repository = repositoryConnectionFactory.createConnection(repositoryName, configuration.getSVNURL(),
          credentials);
      repository.testConnection();
    } finally {
      if (repository != null) {
        repository.closeSession();
      }
    }
  }
}
