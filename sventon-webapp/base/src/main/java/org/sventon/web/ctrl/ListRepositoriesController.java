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
import org.springframework.web.bind.annotation.*;
import org.sventon.appl.Application;
import org.sventon.model.RepositoryName;
import org.sventon.web.UserContext;
import org.sventon.web.UserRepositoryContext;

import static org.sventon.util.EncodingUtils.encode;

/**
 * Controller that creates a list of all configured repositories.
 *
 * @author jesper@sventon.org
 */
@Controller
@RequestMapping(value = "/repos/list")
@SessionAttributes(value = "userContext")
public final class ListRepositoriesController {
  private final Log logger = LogFactory.getLog(ListRepositoriesController.class);

  /**
   * The application.
   */
  private Application application;

  @Autowired
  public ListRepositoriesController(final Application application) {
    this.application = application;
  }


  @RequestMapping(params = "logout=true")
  public String logoutBeforeListRepositories(@RequestParam final boolean logout, @RequestParam final String repositoryName,
                                             @ModelAttribute final UserContext userContext, final Model model) {
    if (!application.isConfigured()) {
      logger.debug("sventon not configured, redirecting to '/repos/listconfigs'");
      return "redirect:/repos/listconfigs";
    }

    if (logout && RepositoryName.isValid(repositoryName)) {
      final UserRepositoryContext userRepositoryContext = userContext.getUserRepositoryContext(new RepositoryName(repositoryName));
      if (userRepositoryContext != null) {
        logger.debug("Clear credential for repository " + repositoryName);
        userRepositoryContext.clearCredentials();
      }
    }

    return listRepositoriesOrShowIfOnlyOne(model);
  }

  @RequestMapping(method = RequestMethod.GET)
  public String listRepositoriesOrShowIfOnlyOne(final Model model) {
    if (!application.isConfigured()) {
      logger.debug("sventon not configured, redirecting to '/repos/listconfigs'");
      return "redirect:/repos/listconfigs";
    }

    if (application.getRepositoryConfigurationCount() > 1) {
      model.addAttribute("repositoryNames", application.getRepositoryNames());
      model.addAttribute("isEditableConfig", application.isEditableConfig());
      return "listRepositories";
    } else if (application.getRepositoryConfigurationCount() == 1) {
      final RepositoryName repositoryName = application.getRepositoryNames().iterator().next();
      return createListUrl(repositoryName, true);
    }

    return null;
  }

  protected String createListUrl(final RepositoryName repositoryName, final boolean redirect) {
    return (redirect ? "redirect:" : "") + "/repos/" + encode(repositoryName.toString()) + "/list/";
  }
}
