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
package org.sventon.web.ctrl.template;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.sventon.SVNConnection;
import org.sventon.SVNConnectionFactory;
import org.sventon.appl.Application;
import org.sventon.model.LogEntry;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.BaseCommand;
import org.sventon.web.command.BaseCommandValidator;
import org.sventon.web.ctrl.BaseController;

/**
 * Controller to fetch details for a specific revision.
 *
 * @author jesper@sventon.org
 */
@Controller
@RequestMapping(value = "/repoz/{name}/info")
@SessionAttributes(value = "userContext")
public final class ShowRevisionInfoController extends BaseController{
  protected static final Log logger = LogFactory.getLog(BaseController.class);

  private static final String VIEW_NAME = "showRevisionInfo";

  @Autowired
  public ShowRevisionInfoController(final Application application, final RepositoryService repositoryService, final SVNConnectionFactory svnConnectionFactory, BaseCommandValidator validator) {
    super(validator, new DefaultControllerHandler(application, repositoryService, svnConnectionFactory, new ControllerClosure<String>() {
      @Override
      public String execute(RepositoryService service, SVNConnection connection, BaseCommand command, Model model) throws Exception {
        logger.debug("Getting revision info details for revision: " + command.getRevision());

        final LogEntry logEntry = service.getLogEntry(command.getName(), connection, command.getRevisionNumber());
        model.addAttribute("revisionInfo", logEntry);

        return VIEW_NAME;
      }
    }));
  }
}