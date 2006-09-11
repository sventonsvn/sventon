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
import de.berlios.sventon.util.FileUtils;
import de.berlios.sventon.web.command.SVNBaseCommand;
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
 * Controller used when downloading files or directories as a zip file.
 *
 * @author jesper@users.berlios.de
 */
public class ZipController extends AbstractSVNTemplateController implements Controller {

  public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

  /**
   * Root of temporary directory where export will be made.
   */
  private String exportDir;

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
                                   final SVNRevision revision, final HttpServletRequest request,
                                   final HttpServletResponse response, final BindException exception) throws Exception {

    ServletOutputStream output = null;
    InputStream fileInputStream = null;

    final List<String> targets = Arrays.asList(RequestUtils.getStringParameters(request, "entry"));
    final ExportDirectory exportDirectory = new ExportDirectory(svnCommand.getName(), exportDir);

    try {
      logger.debug(exportDirectory);
      getRepositoryService().export(repository, targets, revision.getNumber(), exportDirectory);
      final File compressedFile = exportDirectory.compress();
      output = response.getOutputStream();
      response.setContentType(DEFAULT_CONTENT_TYPE);
      response.setHeader("Content-disposition", "attachment; filename=\""
          + EncodingUtils.encodeFilename(compressedFile.getName(), request) + "\"");

      fileInputStream = new FileInputStream(compressedFile);
      FileUtils.writeStream(fileInputStream, output);
    } finally {
      FileUtils.close(fileInputStream);
      FileUtils.close(output);
      logger.debug("Cleanup of temporary directory ok: " + exportDirectory.delete());
    }

    //TODO: When converted into asynch, redirect to repobrowser and wait for download to complete.
    return null;
  }

}
