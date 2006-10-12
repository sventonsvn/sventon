/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.diff.DiffAction;
import de.berlios.sventon.diff.DiffException;
import de.berlios.sventon.diff.SourceLine;
import de.berlios.sventon.web.command.DiffCommand;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.DiffTableDataRow;
import de.berlios.sventon.web.model.UserContext;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The DiffController generates a diff between two repository entries.
 *
 * @author jesper@users.berlios.de
 */
public class DiffController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserContext userContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final String[] entries = ServletRequestUtils.getStringParameters(request, "entry");
    logger.debug("Diffing file content for: " + svnCommand);
    final Map<String, Object> model = new HashMap<String, Object>();

    try {
      final DiffCommand diffCommand = new DiffCommand(entries);
      model.put("diffCommand", diffCommand);
      logger.debug("Using: " + diffCommand);

      final boolean isLeftFileTextType = getRepositoryService().isTextFile(
          repository, diffCommand.getFromPath(), diffCommand.getFromRevision().getNumber());

      final boolean isRightFileTextType = getRepositoryService().isTextFile(
          repository, diffCommand.getToPath(), diffCommand.getToRevision().getNumber());

      if (isLeftFileTextType && isRightFileTextType) {
        model.put("isBinary", false);
        final List<SourceLine[]> sourceLines = getRepositoryService().diff(
            repository, diffCommand, getConfiguration().getInstanceConfiguration(svnCommand.getName()));

        final List<DiffTableDataRow> tableRows = new ArrayList<DiffTableDataRow>();
        int diffCounter = 0;
        int rowNumber = 0;
        for (SourceLine[] sourceLine : sourceLines) {
          String diffAnchor = "";
          String nextDiffAnchor = "";
          if (sourceLine[0].getAction() != DiffAction.UNCHANGED) {
            diffAnchor = "diff" + diffCounter;
            nextDiffAnchor = "diff" + ++diffCounter;
          }
          tableRows.add(new DiffTableDataRow(sourceLine, ++rowNumber, diffAnchor, nextDiffAnchor));
        }
        model.put("tableRows", tableRows);
      } else {
        model.put("isBinary", true);  // Indicates that the file is in binary format.
        logger.info("One or both files selected for diff is in binary format. "
            + "Diff will not be performed");
      }

    } catch (DiffException dex) {
      model.put("diffException", dex.getMessage());
    }

    return new ModelAndView("diff", model);
  }

}
