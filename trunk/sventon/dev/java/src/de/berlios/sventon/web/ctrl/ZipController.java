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

import de.berlios.sventon.util.EncodingUtils;
import de.berlios.sventon.util.FileUtils;
import de.berlios.sventon.util.ZipUtils;
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
   * Directory where to export the files to be zipped.
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
    final File tempExportDirectory = FileUtils.createTempDir(exportDir);

    try {
      // Export selected files/directories into java.io.tmpdir + generated UID-named dir
      logger.debug("Using export directory: " + tempExportDirectory);
      getRepositoryService().export(repository, targets, revision.getNumber(), tempExportDirectory);

      final File zipFile = createZipFile(tempExportDirectory);
      logger.debug("Creating temporary zip file: " + zipFile.getAbsolutePath());
      new ZipUtils().zipDir(zipFile, tempExportDirectory);

      output = response.getOutputStream();
      response.setContentType(DEFAULT_CONTENT_TYPE);
      response.setHeader("Content-disposition", "attachment; filename=\""
          + EncodingUtils.encodeFilename(zipFile.getName(), request) + "\"");

      fileInputStream = new FileInputStream(zipFile);
      FileUtils.writeStream(fileInputStream, output);
    } finally {
      FileUtils.close(fileInputStream);
      FileUtils.close(output);

      //boolean result = FileUtils.deleteDir(tempExportDirectory);
      //logger.debug("Cleanup ok: " + result);
    }

    //TODO: When converted into asynch, redirect to repobrowser and wait for download to complete.
    return null;
  }

  private File createZipFile(final File tempDirectory) {
    return new File(tempDirectory.getParentFile(), tempDirectory.getName() + ".zip");
  }
}
