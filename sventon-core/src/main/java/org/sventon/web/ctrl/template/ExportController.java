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
import org.springframework.web.servlet.view.RedirectView;
import org.sventon.SVNConnection;
import org.sventon.export.ExportExecutor;
import org.sventon.model.UserRepositoryContext;
import org.sventon.util.EncodingUtils;
import org.sventon.web.command.BaseCommand;
import org.sventon.web.command.MultipleEntriesCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for exporting and downloading files or directories as a zip file.
 *
 * @author jesper@sventon.org
 */
public final class ExportController extends AbstractTemplateController {

  /**
   * The export executor instance.
   */
  private final ExportExecutor exportExecutor;

  /**
   * Constructor.
   *
   * @param exportExecutor Export executor instance.
   */
  public ExportController(final ExportExecutor exportExecutor) {
    this.exportExecutor = exportExecutor;
  }

  @Override
  protected ModelAndView svnHandle(final SVNConnection connection, final BaseCommand cmd,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final MultipleEntriesCommand command = (MultipleEntriesCommand) cmd;
    final long pegRevision = command.hasPegRevision() ? command.getPegRevision() : command.getRevisionNumber();

    if (userRepositoryContext.getIsWaitingForExport()) {
      throw new IllegalStateException("Export already in progress");
    }

    final UUID uuid = exportExecutor.submit(command, connection, pegRevision);
    userRepositoryContext.setExportUuid(uuid);
    userRepositoryContext.setIsWaitingForExport(true);

    // Add the redirect URL parameters
    final Map<String, String> model = new HashMap<String, String>();
    model.put("revision", command.getRevision().toString());

    return new ModelAndView(new RedirectView(EncodingUtils.encodeUrl(command.createListUrl()), true), model);
  }

}
