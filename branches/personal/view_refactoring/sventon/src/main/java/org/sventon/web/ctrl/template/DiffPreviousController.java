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
import org.springframework.web.servlet.ModelAndView;
import org.sventon.diff.IdenticalFilesException;
import org.sventon.diff.IllegalFileFormatException;
import org.sventon.model.SideBySideDiffRow;
import org.sventon.model.UserRepositoryContext;
import org.sventon.web.command.DiffCommand;
import org.sventon.web.command.SVNBaseCommand;
import static org.sventon.web.ctrl.template.DiffController.SIDE_BY_SIDE;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The DiffPreviousController generates a Side-by-side diff between a given entry
 * and its previous entry in history.
 *
 * @author jesper@users.berlios.de
 */
public final class DiffPreviousController extends AbstractSVNTemplateController {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    logger.debug("Diffing file (previous): " + svnCommand);

    final Map<String, Object> model = new HashMap<String, Object>();
    final List<SVNFileRevision> revisions = getRepositoryService().getFileRevisions(
        repository, svnCommand.getPath(), svnCommand.getRevisionNumber());

    final DiffCommand diffCommand = new DiffCommand(revisions);
    model.put("diffCommand", diffCommand);
    logger.debug("Using: " + diffCommand);

    try {
      final List<SideBySideDiffRow> diffResult = getRepositoryService().diffSideBySide(repository, diffCommand,
          SVNRevision.UNDEFINED, userRepositoryContext.getCharset(), getRepositoryConfiguration(svnCommand.getName()));

      model.put("style", SIDE_BY_SIDE);
      model.put("diffResult", diffResult);
      model.put("isIdentical", false);
      model.put("isBinary", false);
    } catch (final IdenticalFilesException ife) {
      logger.debug("Files are identical");
      model.put("isIdentical", true);
    } catch (final IllegalFileFormatException iffe) {
      logger.info("Binary file(s) detected", iffe);
      model.put("isBinary", true);  // Indicates that one or both files are in binary format.
    }
    return new ModelAndView("sideBySideDiff", model);
  }

}
