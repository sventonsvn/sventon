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
package de.berlios.sventon.ctrl;

import de.berlios.sventon.command.DiffCommand;
import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.diff.DiffException;
import de.berlios.sventon.diff.Diff;
import de.berlios.sventon.content.KeywordHandler;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
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
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand, final SVNRevision revision,
                                   final HttpServletRequest request, final HttpServletResponse response, final BindException exception)
      throws SventonException, SVNException {

    logger.debug("Diffing file contents for: " + svnCommand);
    final Map<String, Object> model = new HashMap<String, Object>();

    try {
      final DiffCommand diffCommand = new DiffCommand(request.getParameterValues("rev"));
      model.put("diffCommand", diffCommand);
      logger.debug("Using: " + diffCommand);
      model.putAll(diffInternal(repository, diffCommand));
    } catch (DiffException dex) {
      model.put("diffException", dex.getMessage());
    }

    return new ModelAndView("diff", model);
  }

  /**
   * Internal method for creating the diff between two entries.
   *
   * @param repository The repository instance.
   * @param diffCommand The <code>DiffCommand</code> including to/from diff instructions.
   * @return A populated map containing the following info:
   * <ul>
   * <li><i>isBinary</i>, indicates whether any of the entries were a binary file.</li>
   * <li><i>leftFileContents</i>, <code>List</code> of <code>SourceLine</code>s for the left (from) file.</li>
   * <li><i>rightFileContents</i>, <code>List</code> of <code>SourceLine</code>s for the right (to) file.</li>
   * </ul>
   * @throws DiffException if unable to produce diff.
   * @throws SVNException if a subversion error occurs.
   */
  protected Map<String, Object> diffInternal(final SVNRepository repository, final DiffCommand diffCommand)
      throws DiffException, SVNException {

    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    final Map<String, Object> model = new HashMap<String, Object>();
    final String leftLines;
    final String rightLines;

    final HashMap fromFileProperties = new HashMap();
    final HashMap toFileProperties = new HashMap();

    // Get the file's properties without requesting the content.
    // Make sure files are not in binary format.
    repository.getFile(diffCommand.getFromPath(), diffCommand.getFromRevision(), fromFileProperties, null);
    final boolean isTextType = SVNProperty.isTextMimeType((String) fromFileProperties.get(SVNProperty.MIME_TYPE));
    repository.getFile(diffCommand.getToPath(), diffCommand.getToRevision(), toFileProperties, null);

    if (isTextType && SVNProperty.isTextMimeType((String) toFileProperties.get(SVNProperty.MIME_TYPE))) {
      model.put("isBinary", false);

      // Get content of oldest file (left).
      logger.debug("Getting file contents for (from) revision "
          + diffCommand.getFromRevision()
          + ", path: "
          + diffCommand.getFromPath());
      repository.getFile(diffCommand.getFromPath(), diffCommand.getFromRevision(), null, outStream);
      leftLines = StringEscapeUtils.escapeHtml(outStream.toString());

      // Re-initialize stream
      outStream = new ByteArrayOutputStream();

      // Get content of newest file (right).
      logger.debug("Getting file contents for (to) revision "
          + diffCommand.getToRevision()
          + ", path: "
          + diffCommand.getToPath());
      repository.getFile(diffCommand.getToPath(), diffCommand.getToRevision(), null, outStream);
      rightLines = StringEscapeUtils.escapeHtml(outStream.toString());

      final KeywordHandler fromFileKeywordHandler = new KeywordHandler(fromFileProperties,
          getRepositoryConfiguration().getUrl() + diffCommand.getFromPath());
      final KeywordHandler toFileKeywordHandler = new KeywordHandler(toFileProperties,
          getRepositoryConfiguration().getUrl() + diffCommand.getToPath());

      final Diff differ = new Diff(leftLines, fromFileKeywordHandler, rightLines, toFileKeywordHandler);
      model.put("leftFileContents", differ.getLeft());
      model.put("rightFileContents", differ.getRight());
    } else {
      model.put("isBinary", true);  // Indicates that the file is in binary format.
      logger.info("One or both files selected for diff is in binary format. "
          + "Diff will not be performed");
    }
    return model;
  }

}
