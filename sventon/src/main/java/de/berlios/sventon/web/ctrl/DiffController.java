/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.diff.IdenticalFilesException;
import de.berlios.sventon.diff.IllegalFileFormatException;
import de.berlios.sventon.model.InlineDiffRow;
import de.berlios.sventon.model.SideBySideDiffRow;
import de.berlios.sventon.web.command.DiffCommand;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserRepositoryContext;
import de.berlios.sventon.web.support.RequestParameterParser;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The DiffController generates a Side-by-side diff between two repository entries.
 *
 * @author jesper@users.berlios.de
 */
public final class DiffController extends AbstractSVNTemplateController implements Controller {

  private static final String SIDE_BY_SIDE = "sidebyside";
  private static final String UNIFIED = "unified";
  private static final String INLINE = "inline";

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final List<SVNFileRevision> entries = new RequestParameterParser().parseEntries(request);
    final String style = ServletRequestUtils.getStringParameter(request, "style", SIDE_BY_SIDE);
    final long pegRevision = ServletRequestUtils.getLongParameter(request, "pegrev", -1);
    logger.debug("Diffing [" + style + "]: " + entries);

    final Map<String, Object> model = new HashMap<String, Object>();

    final DiffCommand diffCommand = new DiffCommand(entries);
    model.put("diffCommand", diffCommand);
    logger.debug("Using: " + diffCommand);

    final ModelAndView modelAndView = new ModelAndView();
    try {
      if (SIDE_BY_SIDE.equals(style)) {
        modelAndView.setViewName("diff");
        final List<SideBySideDiffRow> diffResult = getRepositoryService().diffSideBySide(
            repository, diffCommand, SVNRevision.create(pegRevision), userRepositoryContext.getCharset(), getInstanceConfiguration(
            svnCommand.getName()));
        model.put("diffResult", diffResult);
      } else if (UNIFIED.equals(style)) {
        modelAndView.setViewName("unifiedDiff");
        final String diffResult = getRepositoryService().diffUnified(
            repository, diffCommand, SVNRevision.create(pegRevision), userRepositoryContext.getCharset(),
            getInstanceConfiguration(svnCommand.getName()));
        model.put("diffResult", diffResult);
      } else if (INLINE.equals(style)) {
        modelAndView.setViewName("inlineDiff");
        final List<InlineDiffRow> diffResult = getRepositoryService().diffInline(
            repository, diffCommand, SVNRevision.create(pegRevision), userRepositoryContext.getCharset(),
            getInstanceConfiguration(svnCommand.getName()));
        model.put("diffResult", diffResult);
      } else {
        throw new IllegalStateException();
      }
      model.put("isIdentical", false);
      model.put("isBinary", false);
    } catch (final IdenticalFilesException ife) {
      logger.debug("Files are identical");
      model.put("isIdentical", true);
    } catch (final IllegalFileFormatException iffe) {
      logger.info("Binary file(s) detected", iffe);
      model.put("isBinary", true);  // Indicates that one or both files are in binary format.
    }
    if (SVNRevision.UNDEFINED != SVNRevision.create(pegRevision)) {
      model.put("pegrev", pegRevision);
    }
    modelAndView.addAllObjects(model);
    return modelAndView;
  }

}
