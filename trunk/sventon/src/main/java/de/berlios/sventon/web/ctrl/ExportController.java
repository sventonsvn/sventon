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

import de.berlios.sventon.repository.export.ExportDirectory;
import de.berlios.sventon.util.EncodingUtils;
import de.berlios.sventon.util.WebUtils;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserRepositoryContext;
import de.berlios.sventon.web.support.RequestParameterParser;
import org.apache.commons.io.IOUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Controller for exporting and downloading files or directories as a zip file.
 *
 * @author jesper@users.berlios.de
 */
public final class ExportController extends AbstractSVNTemplateController implements Controller {

  /**
   * Root of temporary directory where export will be made.
   */
  private File exportDir;

  /**
   * The charset to use for filenames and comments in the archive file.
   */
  private Charset archiveFileCharset;
  private static final String FALLBACK_CHARSET = "iso-8859-1";

  /**
   * Sets the export dir to use when exporting files to be zipped from the repository
   *
   * @param exportDir The directory
   */
  public void setExportDir(final File exportDir) {
    this.exportDir = exportDir;
  }

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    ServletOutputStream output = null;
    InputStream fileInputStream = null;

    final List<SVNFileRevision> targets = new RequestParameterParser().parseEntries(request);
    final ExportDirectory exportDirectory = new ExportDirectory(svnCommand.getName(), exportDir, archiveFileCharset);

    try {
      logger.debug(exportDirectory);
      getRepositoryService().export(repository, targets, exportDirectory);
      final File compressedFile = exportDirectory.compress();
      output = response.getOutputStream();
      response.setContentType(WebUtils.APPLICATION_OCTET_STREAM);
      response.setHeader(WebUtils.CONTENT_DISPOSITION_HEADER, "attachment; filename=\""
          + EncodingUtils.encodeFilename(compressedFile.getName(), request) + "\"");

      fileInputStream = new FileInputStream(compressedFile);
      IOUtils.copy(fileInputStream, output);
    } finally {
      IOUtils.closeQuietly(fileInputStream);
      IOUtils.closeQuietly(output);
      try {
        exportDirectory.delete();
      } catch (IOException e) {
        logger.warn("Unable to cleanup temporary directory: " + exportDirectory.toString());
      }
    }
    //TODO: When converted into asynch, redirect to repobrowser and wait for download to complete.
    return null;
  }

  /**
   * Sets the archive file charset to use for filenames and comments.
   * <p/>
   * If given charset does not exist, <code>iso-8859-1</code> will be used as a fallback.
   *
   * @param archiveFileCharset Charset.
   * @see #FALLBACK_CHARSET
   */
  public void setArchiveFileCharset(final String archiveFileCharset) {
    try {
      this.archiveFileCharset = Charset.forName(archiveFileCharset);
    } catch (IllegalArgumentException iae) {
      this.archiveFileCharset = Charset.forName(FALLBACK_CHARSET);
    }
  }

}
