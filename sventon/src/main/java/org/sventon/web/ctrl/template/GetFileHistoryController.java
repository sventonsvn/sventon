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
import org.sventon.SVNConnection;
import org.sventon.SventonException;
import org.sventon.model.FileRevision;
import org.sventon.model.UserRepositoryContext;
import org.sventon.web.command.BaseCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gets the file revision history for a given entry.
 *
 * @author jesper@sventon.org
 */
public final class GetFileHistoryController extends AbstractTemplateController {

  /**
   * Request parameter identifying the archived entry to display.
   */
  static final String ARCHIVED_ENTRY = "archivedEntry";

  @Override
  protected ModelAndView svnHandle(final SVNConnection connection, final BaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final Map<String, Object> model = new HashMap<String, Object>();
    final String archivedEntry = ServletRequestUtils.getStringParameter(request, ARCHIVED_ENTRY, null);

    try {
      logger.debug("Finding revisions for [" + command.getPath() + "]");
      final List<FileRevision> revisions = getRepositoryService().getFileRevisions(
          connection, command.getPath(), command.getRevisionNumber());
      Collections.reverse(revisions);

      model.put("currentRevision", command.getRevisionNumber());
      model.put("fileRevisions", revisions);
      if (archivedEntry != null) {
        model.put(ARCHIVED_ENTRY, archivedEntry);
      }
    } catch (SventonException ex) {
      logger.error(ex.getMessage());
    }

    return new ModelAndView(getViewName(), model);
  }
}
