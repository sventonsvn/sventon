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
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.diff.DiffException;
import org.sventon.diff.IdenticalFilesException;
import org.sventon.diff.IllegalFileFormatException;
import org.sventon.model.InlineDiffRow;
import org.sventon.model.SideBySideDiffRow;
import org.sventon.model.UserRepositoryContext;
import org.sventon.util.RequestParameterParser;
import org.sventon.web.command.DiffCommand;
import org.sventon.web.command.SVNBaseCommand;
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
public final class DiffController extends AbstractSVNTemplateController {

  public static final String SIDE_BY_SIDE = "sidebyside";
  public static final String UNIFIED = "unified";
  public static final String INLINE = "inline";

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final List<SVNFileRevision> entries = new RequestParameterParser().parseEntries(request);
    final SVNRevision pegRevision = SVNRevision.create(ServletRequestUtils.getLongParameter(request, "pegrev", -1));

    final Map<String, Object> model = new HashMap<String, Object>();

    final DiffCommand diffCommand = new DiffCommand(entries);
    model.put("diffCommand", diffCommand);
    logger.debug("Using: " + diffCommand);

    final ModelAndView modelAndView = new ModelAndView();
    final RepositoryConfiguration config = getRepositoryConfiguration(svnCommand.getName());
    final String charset = userRepositoryContext.getCharset();

    try {
      final SVNNodeKind nodeKind = getNodeKind(repository, diffCommand, pegRevision);

      if (SVNNodeKind.DIR == nodeKind) {
        logger.debug("Diffing dirs");
        modelAndView.setViewName("pathDiff");
        final List<SVNDiffStatus> diffResult = getRepositoryService().diffPaths(
            repository, diffCommand, pegRevision, config);
        logger.debug("Number of path diffs: " + diffResult.size());
        model.put("isIdentical", diffResult.isEmpty());
        model.put("diffResult", diffResult);
      } else if (SVNNodeKind.FILE == nodeKind) {
        final String style = ServletRequestUtils.getStringParameter(request, "style", SIDE_BY_SIDE);
        model.put("style", style);
        if (logger.isDebugEnabled()) {
          final StringBuilder sb = new StringBuilder();
          for (SVNFileRevision entry : entries) {
            sb.append(entry.getPath());
            sb.append("@");
            sb.append(entry.getRevision());
            sb.append(", ");
          }
          logger.debug("Diffing files [" + style + "]: " + entries);
        }

        if (SIDE_BY_SIDE.equals(style)) {
          modelAndView.setViewName("sideBySideDiff");
          final List<SideBySideDiffRow> diffResult = getRepositoryService().diffSideBySide(
              repository, diffCommand, pegRevision, charset, config);
          model.put("diffResult", diffResult);
        } else if (UNIFIED.equals(style)) {
          modelAndView.setViewName("unifiedDiff");
          final String diffResult = getRepositoryService().diffUnified(
              repository, diffCommand, pegRevision, charset, config);
          model.put("diffResult", diffResult);
        } else if (INLINE.equals(style)) {
          modelAndView.setViewName("inlineDiff");
          final List<InlineDiffRow> diffResult = getRepositoryService().diffInline(
              repository, diffCommand, pegRevision, charset, config);
          model.put("diffResult", diffResult);
        } else {
          throw new IllegalStateException();
        }
        model.put("isIdentical", false);
        model.put("isBinary", false);
      }
    } catch (final IdenticalFilesException ife) {
      logger.debug("Files are identical");
      model.put("isIdentical", true);
    } catch (final IllegalFileFormatException iffe) {
      logger.info(iffe.getMessage());
      model.put("isBinary", true);  // Indicates that one or both files are in binary format.
    }
    if (SVNRevision.UNDEFINED != pegRevision) {
      model.put("pegrev", pegRevision.getNumber());
    }
    modelAndView.addAllObjects(model);
    return modelAndView;
  }

  SVNNodeKind getNodeKind(final SVNRepository repository, final DiffCommand diffCommand, final SVNRevision pegRevision)
      throws SVNException, DiffException {

    final SVNNodeKind nodeKind1;
    final SVNNodeKind nodeKind2;
    if (SVNRevision.UNDEFINED.equals(pegRevision)) {
      nodeKind1 = getRepositoryService().getNodeKind(repository, diffCommand.getFromPath(), diffCommand.getFromRevision().getNumber());
      nodeKind2 = getRepositoryService().getNodeKind(repository, diffCommand.getToPath(), diffCommand.getToRevision().getNumber());
    } else {
      nodeKind1 = getRepositoryService().getNodeKind(repository, diffCommand.getFromPath(), pegRevision.getNumber());
      nodeKind2 = getRepositoryService().getNodeKind(repository, diffCommand.getToPath(), pegRevision.getNumber());
    }

    if (nodeKind1 != nodeKind2) {
      throw new DiffException("Entries are different kinds!");
    }
    return nodeKind1;
  }

}
