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

import org.apache.commons.lang.Validate;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.diff.DiffException;
import org.sventon.diff.IdenticalFilesException;
import org.sventon.diff.IllegalFileFormatException;
import org.sventon.model.DiffStyle;
import org.sventon.model.UserRepositoryContext;
import org.sventon.web.command.BaseCommand;
import org.sventon.web.command.DiffCommand;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNDiffStatus;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The DiffController generates a Side-by-side diff between two repository entries.
 *
 * @author jesper@sventon.org
 */
public final class DiffController extends AbstractTemplateController {

  /**
   * User preferred diff style.
   */
  private DiffStyle preferredDiffStyle = DiffStyle.sidebyside;

  @Override
  protected ModelAndView svnHandle(final SVNRepository repository, final BaseCommand cmd,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final DiffCommand command = (DiffCommand) cmd;

    final Map<String, Object> model = new HashMap<String, Object>();
    final ModelAndView modelAndView = new ModelAndView();
    final RepositoryConfiguration config = getRepositoryConfiguration(command.getName());
    final String charset = userRepositoryContext.getCharset();

    handleDiffPrevious(repository, command);
    handleDiffStyle(command);

    try {
      final SVNNodeKind nodeKind = getRepositoryService().getNodeKindForDiff(repository, command);
      if (SVNNodeKind.DIR == nodeKind) {
        model.putAll(handlePathDiff(repository, modelAndView, command, config));
      } else if (SVNNodeKind.FILE == nodeKind) {
        model.putAll(handleFileDiff(repository, modelAndView, command, config, charset));
      }
    } catch (final IdenticalFilesException ife) {
      logger.debug("Files are identical");
      model.put("isIdentical", true);
    } catch (final IllegalFileFormatException iffe) {
      logger.info(iffe.getMessage());
      model.put("isBinary", true);  // Indicates that one or both files are in binary format.
    }
    modelAndView.addAllObjects(model);
    return modelAndView;
  }

  private void handleDiffPrevious(SVNRepository repository, DiffCommand command) throws SVNException {
    if (!command.hasEntries()) {
      logger.debug("No entries has been set - diffing with previous");
      final List<SVNFileRevision> revisions = getRepositoryService().getFileRevisions(
          repository, command.getPath(), command.getRevisionNumber());
      command.setEntries(revisions.toArray(new SVNFileRevision[revisions.size()]));
    }
  }

  private void handleDiffStyle(DiffCommand command) {
    if (DiffStyle.unspecified == command.getStyle()) {
      logger.debug("Setting user preferred diff style: " + preferredDiffStyle);
      command.setStyle(preferredDiffStyle);
    }
  }

  private Map<String, Object> handleFileDiff(final SVNRepository repository, final ModelAndView modelAndView,
                                             final DiffCommand command, final RepositoryConfiguration config,
                                             final String charset)
      throws SVNException, DiffException {

    final Map<String, Object> model = new HashMap<String, Object>();
    final SVNRevision pegRevision = SVNRevision.create(command.getPegRevision());

    switch (command.getStyle()) {
      case inline:
        modelAndView.setViewName("inlineDiff");
        model.put("diffResult", getRepositoryService().diffInline(repository, command, pegRevision, charset, config));
        break;
      case sidebyside:
        modelAndView.setViewName("sideBySideDiff");
        model.put("diffResult", getRepositoryService().diffSideBySide(repository, command, pegRevision, charset, config));
        break;
      case unified:
        modelAndView.setViewName("unifiedDiff");
        model.put("diffResult", getRepositoryService().diffUnified(repository, command, pegRevision, charset));
        break;
      default:
        throw new IllegalStateException();
    }
    model.put("isIdentical", false);
    model.put("isBinary", false);
    return model;
  }

  private Map<String, Object> handlePathDiff(final SVNRepository repository, final ModelAndView modelAndView,
                                             final DiffCommand command, final RepositoryConfiguration config)
      throws SVNException {

    final Map<String, Object> model = new HashMap<String, Object>();
    logger.debug("Diffing dirs");
    modelAndView.setViewName("pathDiff");
    final List<SVNDiffStatus> diffResult = getRepositoryService().diffPaths(repository, command, config);
    logger.debug("Number of path diffs: " + diffResult.size());
    model.put("isIdentical", diffResult.isEmpty());
    model.put("diffResult", diffResult);
    return model;
  }

  /**
   * Sets the preferred diff style for diff previous view.
   *
   * @param diffStyle Preferred diff style.
   */
  public void setPreferredDiffStyle(final DiffStyle diffStyle) {
    Validate.notNull(diffStyle);
    this.preferredDiffStyle = diffStyle;

  }
}
