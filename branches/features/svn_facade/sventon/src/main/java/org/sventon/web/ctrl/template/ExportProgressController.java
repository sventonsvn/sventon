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
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.export.ExportExecutor;
import org.sventon.model.UserRepositoryContext;
import org.sventon.web.command.BaseCommand;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The export progress controller.
 *
 * @author jesper@sventon.org
 */
public final class ExportProgressController extends AbstractTemplateController {

  /**
   * The export executor instance.
   */
  private final ExportExecutor exportExecutor;

  /**
   * Constructor.
   *
   * @param exportExecutor Export executor instance.
   */
  public ExportProgressController(final ExportExecutor exportExecutor) {
    this.exportExecutor = exportExecutor;
  }

  @Override
  protected ModelAndView svnHandle(final SVNRepository repository, final BaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final Map<String, Object> model = new HashMap<String, Object>();
    final UUID exportUuid = UUID.fromString(ServletRequestUtils.getRequiredStringParameter(request, "uuid"));
    final boolean download = ServletRequestUtils.getBooleanParameter(request, "download", false);
    final boolean delete = ServletRequestUtils.getBooleanParameter(request, "delete", false);

    if (delete) {
      exportExecutor.delete(exportUuid);
      userRepositoryContext.setIsWaitingForExport(false);
    } else if (download) {
      logger.info("Downloading export file, uuid: " + exportUuid);
      exportExecutor.downloadByUUID(exportUuid, request, response);
      return null;
    } else {
      final boolean finished = exportExecutor.isExported(exportUuid);
      logger.debug("Export finished: " + finished);
      model.put("exportFinished", finished);
    }
    return new ModelAndView(getViewName(), model);
  }

}