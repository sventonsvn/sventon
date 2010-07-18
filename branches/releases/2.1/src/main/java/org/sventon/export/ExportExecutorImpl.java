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
package org.sventon.export;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.sventon.appl.ConfigDirectory;
import org.sventon.service.RepositoryService;
import org.sventon.util.EncodingUtils;
import org.sventon.util.WebUtils;
import org.sventon.web.command.MultipleEntriesCommand;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ExportExecutor default implementation.
 *
 * @author jesper@sventon.org
 */
public class ExportExecutorImpl implements ExportExecutor {

  /**
   * Logger for this class.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Fallback character set, default set to ISO-8859-1.
   */
  public static final String FALLBACK_CHARSET = "ISO-8859-1";

  /**
   * The charset to use for file names and comments in compressed archive file.
   */
  private Charset archiveFileCharset = Charset.forName(FALLBACK_CHARSET);

  /**
   * Export root directory.
   */
  private final File exportRootDirectory;

  /**
   * Executor service.
   */
  private final ExecutorService executorService = Executors.newFixedThreadPool(1);

  /**
   * Map of completed exports.
   */
  private final Map<UUID, File> completedExports = new ConcurrentHashMap<UUID, File>();

  /**
   * Repository service.
   */
  private RepositoryService repositoryService;

  /**
   * Constructor.
   *
   * @param configDirectory The configuration directory. The export directory will be extracted.
   */
  public ExportExecutorImpl(final ConfigDirectory configDirectory) {
    this.exportRootDirectory = configDirectory.getExportDirectory();
  }

  public UUID submit(final MultipleEntriesCommand command, final SVNRepository repository, final long pegRevision) {
    final ExportDirectoryImpl exportDirectory = new ExportDirectoryImpl(command.getName(), exportRootDirectory, archiveFileCharset);
    final UUID uuid = exportDirectory.getUUID();
    executorService.submit(new ExportTask(exportDirectory, repository, Arrays.asList(command.getEntries()), pegRevision));
    return uuid;
  }

  public boolean isExported(final UUID uuid) {
    return completedExports.containsKey(uuid);
  }

  public void delete(final UUID uuid) {
    completedExports.remove(uuid);
  }

  public void downloadByUUID(final UUID uuid, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    if (!completedExports.containsKey(uuid)) {
      throw new IllegalStateException("No download with UUID: " + uuid);
    }

    final File compressedFile = completedExports.get(uuid);
    logger.debug("File size: " + compressedFile.length());
    OutputStream output = null;
    InputStream fileInputStream = null;

    try {
      output = response.getOutputStream();
      prepareResponse(request, response, compressedFile);
      fileInputStream = new FileInputStream(compressedFile);
      IOUtils.copy(fileInputStream, output);
      output.flush();
    } finally {
      IOUtils.closeQuietly(fileInputStream);
      IOUtils.closeQuietly(output);
    }
  }

  /**
   * Prepares the HTTP response by adding necessary headers and content-type.
   *
   * @param request    Request
   * @param response   Response
   * @param attachment Attachment
   */
  protected void prepareResponse(final HttpServletRequest request, final HttpServletResponse response,
                                 final File attachment) {
    response.setContentType(WebUtils.APPLICATION_OCTET_STREAM);
    response.setHeader(WebUtils.CONTENT_DISPOSITION_HEADER, "attachment; filename=\"" +
        EncodingUtils.encodeFilename(attachment.getName(), request) + "\"");
  }

  /**
   * Sets the repository service.
   *
   * @param repositoryService Service.
   */
  @Autowired
  public void setRepositoryService(final RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

  /**
   * Sets the archive file charset to use for file names and comments.
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

  /**
   * Gets the archive file charset.
   *
   * @return Charset.
   */
  protected Charset getArchiveFileCharset() {
    return archiveFileCharset;
  }

  /**
   * ExportTask.
   */
  protected class ExportTask implements Callable<Void> {

    private final ExportDirectory exportDirectory;
    private final SVNRepository repository;
    private final List<SVNFileRevision> entries;
    private final long pegRevision;

    public ExportTask(final ExportDirectory exportDirectory, final SVNRepository repository,
                      final List<SVNFileRevision> entries, final long pegRevision) {
      this.exportDirectory = exportDirectory;
      this.repository = repository;
      this.entries = entries;
      this.pegRevision = pegRevision;
    }

    public Void call() throws SVNException, IOException {
      final UUID uuid = exportDirectory.getUUID();
      final StopWatch stopWatch = new StopWatch(
          "Export of [" + exportDirectory.getDirectory().getAbsolutePath() + "] uuid: " + uuid);

      stopWatch.start();
      try {
        exportDirectory.mkdirs();
        repositoryService.export(repository, entries, pegRevision, exportDirectory);
        completedExports.put(uuid, exportDirectory.compress());
      } finally {
        stopWatch.stop();
        logger.info(stopWatch.shortSummary());
        try {
          exportDirectory.delete();
        } catch (IOException e) {
          logger.warn("Unable to cleanup temporary directory: " + exportDirectory.toString());
        }
      }
      return null;
    }
  }
}

