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

import de.berlios.sventon.repository.export.ExportDirectory;
import de.berlios.sventon.util.EncodingUtils;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserContext;
import org.apache.commons.io.IOUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Controller for exporting and downloading files or directories as a zip file.
 *
 * @author jesper@users.berlios.de
 */
public class ExportController extends AbstractSVNTemplateController implements Controller {

  /**
   * Root of temporary directory where export will be made.
   */
  private String exportDir;

  /**
   * Response stream content type. Default set to <code>application/octet-stream</code>.
   */
  private String contentType = "application/octet-stream";

  /**
   * Sets the export dir to use when exporting files to be zipped from the repository
   *
   * @param exportDir The directory
   */
  public void setExportDir(final String exportDir) {
    this.exportDir = exportDir;
  }

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserContext userContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    ServletOutputStream output = null;
    InputStream fileInputStream = null;

    final List<String> targets = Arrays.asList(RequestUtils.getStringParameters(request, "entry"));
    final ExportDirectory exportDirectory = new ExportDirectory(svnCommand.getName(), exportDir);

    try {
      logger.debug(exportDirectory);
      getRepositoryService().export(repository, targets, revision.getNumber(), exportDirectory);
      final File compressedFile = exportDirectory.compress();
      output = response.getOutputStream();
      response.setContentType(contentType);
      response.setHeader("Content-disposition", "attachment; filename=\""
          + EncodingUtils.encodeFilename(compressedFile.getName(), request) + "\"");

      fileInputStream = new FileInputStream(compressedFile);
      IOUtils.copy(fileInputStream, output);
    } finally {
      IOUtils.closeQuietly(fileInputStream);
      IOUtils.closeQuietly(output);
      logger.debug("Cleanup of temporary directory ok: " + exportDirectory.delete());
    }

    //TODO: When converted into asynch, redirect to repobrowser and wait for download to complete.
    return null;
  }

  /**
   * Sets the content type used when writing the stream to the response.
   *
   * @param contentType Content type to use.
   */
  public void setContentType(final String contentType) {
    this.contentType = contentType;
  }

}
