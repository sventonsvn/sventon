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
package org.sventon.web.ctrl.template;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.model.UserRepositoryContext;
import org.sventon.web.command.SVNBaseCommand;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Gets the file revision history for a given entry.
 *
 * @author jesper@sventon.org
 */
public final class GetFileHistoryController extends AbstractSVNTemplateController {

  /**
   * Request parameter identifying the arcived entry to display.
   */
  protected static final String ARCHIVED_ENTRY = "archivedEntry";

  /**
   * Formatter for time in a quasi ISO 8601 format where the ':' are replaced by periods to save to disk.
   * <p/>
   * <code>yyyy-MM-ddTHH.mm.ss.SSSSSSZ</code>.
   */
  public static final DateFormat ISO8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final Map<String, Object> model = new HashMap<String, Object>();
    final String archivedEntry = ServletRequestUtils.getStringParameter(request, ARCHIVED_ENTRY, null);

    final List<SVNFileRevision> fileRevisions = new ArrayList<SVNFileRevision>();
    try {
      logger.debug("Finding revisions for [" + command.getPath() + "]");
      fileRevisions.addAll(getRepositoryService().getFileRevisions(repository, command.getPath(),
          command.getRevisionNumber()));
      Collections.reverse(fileRevisions);
    } catch (SVNException svnex) {
      logger.error(svnex.getMessage());
    }

    model.put("currentRevision", command.getRevisionNumber());
    model.put("fileRevisions", fileRevisions);
    if (archivedEntry != null) {
      model.put(ARCHIVED_ENTRY, archivedEntry);
    }
    return new ModelAndView(getViewName(), model);
  }
}
