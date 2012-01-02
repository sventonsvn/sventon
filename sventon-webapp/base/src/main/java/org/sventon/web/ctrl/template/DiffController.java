/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
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
import org.sventon.SVNConnection;
import org.sventon.SventonException;
import org.sventon.diff.DiffException;
import org.sventon.diff.IdenticalFilesException;
import org.sventon.diff.IllegalFileFormatException;
import org.sventon.model.DiffStatus;
import org.sventon.model.DiffStyle;
import org.sventon.model.DirEntry;
import org.sventon.model.FileRevision;
import org.sventon.web.UserRepositoryContext;
import org.sventon.web.command.BaseCommand;
import org.sventon.web.command.DiffCommand;

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
  protected ModelAndView svnHandle(final SVNConnection connection, final BaseCommand cmd,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final DiffCommand command = (DiffCommand) cmd;

    final Map<String, Object> model = new HashMap<String, Object>();
    final ModelAndView modelAndView = new ModelAndView();
    final String charset = userRepositoryContext.getCharset();

    handleDiffPrevious(connection, command);
    handleDiffStyle(command);

    try {
      final DirEntry.Kind nodeKind = getRepositoryService().getNodeKind(connection, command.getPath(), command.getRevisionNumber());
      model.put("isFile", DirEntry.Kind.FILE == nodeKind);

      final DirEntry.Kind nodeKindForDiff = getRepositoryService().getNodeKindForDiff(
          connection, command.getFrom(), command.getTo(), command.getPegRevision());
      if (DirEntry.Kind.DIR == nodeKindForDiff) {
        model.putAll(handlePathDiff(connection, modelAndView, command));
      } else if (DirEntry.Kind.FILE == nodeKindForDiff) {
        model.putAll(handleFileDiff(connection, modelAndView, command, charset));
      } else {
        throw new DiffException("Unable to diff entry of kind: " + nodeKindForDiff);
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

  private void handleDiffPrevious(SVNConnection connection, DiffCommand command) throws SventonException {
    if (!command.hasEntries()) {
      logger.debug("No entries has been set - diffing with previous");
      final List<FileRevision> revisions = getRepositoryService().getFileRevisions(
          connection, command.getPath(), command.getRevisionNumber());
      command.setEntries(revisions.toArray(new FileRevision[revisions.size()]));
    }
  }

  private void handleDiffStyle(DiffCommand command) {
    if (DiffStyle.unspecified == command.getStyle()) {
      logger.debug("Setting user preferred diff style: " + preferredDiffStyle);
      command.setStyle(preferredDiffStyle);
    }
  }

  private Map<String, Object> handleFileDiff(final SVNConnection connection, final ModelAndView modelAndView,
                                             final DiffCommand command, final String charset)
      throws SventonException, DiffException {

    final Map<String, Object> model = new HashMap<String, Object>();

    switch (command.getStyle()) {
      case inline:
        modelAndView.setViewName("inlineDiff");
        model.put("diffResult", getRepositoryService().diffInline(
            connection, command.getFrom(), command.getTo(), command.getPegRevision(), charset));
        break;
      case sidebyside:
        modelAndView.setViewName("sideBySideDiff");
        model.put("diffResult", getRepositoryService().diffSideBySide(
            connection, command.getFrom(), command.getTo(), command.getPegRevision(), charset));
        break;
      case unified:
        modelAndView.setViewName("unifiedDiff");
        model.put("diffResult", getRepositoryService().diffUnified(
            connection, command.getFrom(), command.getTo(), command.getPegRevision(), charset));
        break;
      default:
        throw new IllegalStateException();
    }
    model.put("isIdentical", false);
    model.put("isBinary", false);
    return model;
  }

  private Map<String, Object> handlePathDiff(final SVNConnection connection, final ModelAndView modelAndView,
                                             final DiffCommand command) throws SventonException {

    final Map<String, Object> model = new HashMap<String, Object>();
    logger.debug("Diffing dirs");
    modelAndView.setViewName("pathDiff");

    final List<DiffStatus> diffResult = getRepositoryService().diffPaths(connection, command.getFrom(), command.getTo());
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
