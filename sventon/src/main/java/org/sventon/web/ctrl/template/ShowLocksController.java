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

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.SVNConnection;
import org.sventon.model.UserRepositoryContext;
import org.sventon.web.command.BaseCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * ShowLockController.
 *
 * @author jesper@sventon.org
 */
public final class ShowLocksController extends AbstractTemplateController {

  @Override
  protected ModelAndView svnHandle(final SVNConnection connection, final BaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("currentLocks", getRepositoryService().getLocks(connection, command.getPath()).values());
    return new ModelAndView(getViewName(), model);
  }

}
