/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.ctrl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.appl.Application;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.web.command.ConfigCommand;
import org.sventon.web.command.ConfigCommandValidator;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Form controller handling add/edit of repository configurations.
 */
@Controller("configurationFormController")
@RequestMapping("/repos/config")
public final class ConfigurationFormController {

  private final Log logger = LogFactory.getLog(getClass());

  /**
   * The application.
   */
  private final Application application;

  /**
   * Validator for input.
   */
  private ConfigCommandValidator validator;

  @Autowired
  public void setValidator(ConfigCommandValidator validator) {
    this.validator = validator;
  }

  /**
   * Constructor.
   *
   * @param application Application instance
   */
  @Autowired
  public ConfigurationFormController(final Application application) {
    this.application = application;
  }

  @InitBinder("command")
  public void bindValidator(WebDataBinder binder) {
    binder.setValidator(validator);
  }

  @RequestMapping(method = GET)
  public ModelAndView setUpForm(ModelAndView modelAndView) throws Exception {
    modelAndView.getModel().put("addedRepositories", application.getRepositoryNames());
    modelAndView.getModel().put("command", new ConfigCommand());
    modelAndView.setViewName("config/configForm");
    return modelAndView;
  }

  @RequestMapping(method = POST)
  public String onFormSubmit(@ModelAttribute("command") @Valid ConfigCommand command, BindingResult result, Model model) {

    if (result.hasErrors()) {
      return "config/configForm";
    }

    logger.debug("Adding configuration from command: " + command);
    final RepositoryConfiguration repositoryConfiguration = command.createRepositoryConfiguration();
    application.addConfiguration(repositoryConfiguration);
    model.addAttribute("addedRepositories", application.getRepositoryNames());
    model.addAttribute("latestAddedRepository", command.getName());
    return "config/listConfigs";
  }
}
