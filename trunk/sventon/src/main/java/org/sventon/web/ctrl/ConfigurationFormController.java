/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.ctrl;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.sventon.appl.Application;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.web.command.ConfigCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Form controller handling add/edit of repository configurations.
 *
 * @author jesper@sventon.org
 */
public final class ConfigurationFormController extends SimpleFormController {

  /**
   * The application.
   */
  private Application application;

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
  @Override
  protected Map referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception {
    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("addedRepositories", application.getRepositoryNames());
    return model;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object command, final BindException errors) throws Exception {

    final Map<String, Object> model = new HashMap<String, Object>();
    final ConfigCommand confCommand = (ConfigCommand) command;
    logger.debug("Adding configuration from command: " + confCommand);
    final RepositoryConfiguration repositoryConfiguration = confCommand.createRepositoryConfiguration();
    application.addRepository(repositoryConfiguration);
    model.put("addedRepositories", application.getRepositoryNames());
    model.put("latestAddedRepository", confCommand.getName());
    return showForm(request, errors, getSuccessView(), model);
  }

}
