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
package org.sventon.web.ctrl;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.sventon.appl.Application;
import org.sventon.web.command.ConfigLoginCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Login controller for the configuration pages.
 *
 * @author jesper@sventon.org
 */
public class ConfigurationLoginController extends SimpleFormController {

  /**
   * The application.
   */
  private Application application;

  /**
   * Sets the application.
   *
   * @param application Application
   */
  public final void setApplication(final Application application) {
    this.application = application;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ModelAndView showForm(final HttpServletRequest request, final HttpServletResponse response,
                                  final BindException errors) throws Exception {
    if (application.isEditableConfig()) {
      return super.showForm(request, response, errors);
    } else {
      throw new UnsupportedOperationException("Configuration is not editable");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object command, final BindException errors) throws Exception {

    final ConfigLoginCommand configLoginCommand = (ConfigLoginCommand) command;
    final String passwordString = configLoginCommand.getPwd();

    if (application.isValidConfigPassword(passwordString)) {
      logger.debug("Correct config password entered");
      final HttpSession session = request.getSession();
      session.setAttribute("isAdminLoggedIn", true);
      return super.onSubmit(configLoginCommand, errors);
    } else {
      logger.debug("Incorrect config password entered");
      errors.rejectValue("pwd", "illegal.config.password", "Illegal config password");
      return super.showForm(request, response, errors);
    }
  }
}
