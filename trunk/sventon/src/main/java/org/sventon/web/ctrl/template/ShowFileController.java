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

import org.apache.commons.io.FilenameUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.sventon.colorer.Colorer;
import org.sventon.model.ArchiveFile;
import org.sventon.model.TextFile;
import org.sventon.model.UserRepositoryContext;
import org.sventon.model.ZipFileWrapper;
import org.sventon.util.EncodingUtils;
import org.sventon.util.KeywordHandler;
import org.sventon.util.WebUtils;
import org.sventon.web.command.SVNBaseCommand;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * ShowFileController.
 *
 * @author patrik@sventon.org
 * @author jesper@sventon.org
 */
public final class ShowFileController extends AbstractSVNTemplateController implements Controller {

  /**
   * The colorer instance.
   */
  private Colorer colorer;

  /**
   * The mime/file type map.
   */
  private FileTypeMap mimeFileTypeMap;

  /**
   * Regex pattern that identifies text file extensions.
   */
  private String textFileExtensionPattern;

  /**
   * Regex pattern that identifies binary file extensions.
   */
  private String binaryFileExtensionPattern;

  /**
   * Regex pattern that identifies archive file extensions.
   */
  private String archiveFileExtensionPattern;

  /**
   * FORMAT_REQUEST_PARAMETER = format.
   */
  private static final String FORMAT_REQUEST_PARAMETER = "format";

  /**
   * Request parameter identifying the arcived entry to display.
   */
  private static final String ARCHIVED_ENTRY = "archivedEntry";

  /**
   * Request parameter controlling if archived entry should be displayed
   * independently of it's mime-type.
   */
  private static final String FORCE_ARCHIVED_ENTRY_DISPLAY = "forceDisplay";

  /**
   * Request parameter indicating display should be done in a raw, unprocessed format.
   */
  private static final String RAW_DISPLAY_FORMAT = "raw";

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    logger.debug("Assembling file contents for: " + command);

    final String formatParameter = ServletRequestUtils.getStringParameter(request, FORMAT_REQUEST_PARAMETER, null);
    final String archivedEntry = ServletRequestUtils.getStringParameter(request, ARCHIVED_ENTRY, null);
    final boolean forceDisplay = ServletRequestUtils.getBooleanParameter(request, FORCE_ARCHIVED_ENTRY_DISPLAY, false);
    final Map<String, Object> model = new HashMap<String, Object>();
    final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    final SVNProperties fileProperties = getRepositoryService().getFileProperties(
        repository, command.getPath(), command.getRevisionNumber());

    logger.debug(fileProperties);

    final String charset = userRepositoryContext.getCharset();
    logger.debug("Using charset encoding: " + charset);

    model.put("properties", fileProperties);
    final ModelAndView modelAndView;

    if (isImageFileExtension(command)) {
      logger.debug("File identified as an image file");
      modelAndView = new ModelAndView("showImageFile", model);
    } else if (isArchiveFileExtension(command)) {
      if (archivedEntry == null) {
        logger.debug("File identified as an archive file");
        getRepositoryService().getFile(repository, command.getPath(), command.getRevisionNumber(), outStream);
        final ArchiveFile archiveFile = new ArchiveFile(outStream.toByteArray());
        model.put("entries", archiveFile.getEntries());
        modelAndView = new ModelAndView("showArchiveFile", model);
      } else {
        logger.debug("Archived entry: " + archivedEntry);
        model.put("archivedEntry", archivedEntry);
        final String contentType = mimeFileTypeMap.getContentType(archivedEntry);
        logger.debug("Detected content-type: " + contentType);

        if (contentType != null && contentType.startsWith("text") || forceDisplay) {
          getRepositoryService().getFile(repository, command.getPath(), command.getRevisionNumber(), outStream);
          logger.debug("Extracting [" + archivedEntry + "] from archive [" + command.getPath() + "]");
          final ZipFileWrapper zipFileWrapper = new ZipFileWrapper(outStream.toByteArray());
          final TextFile textFile = new TextFile(new String(zipFileWrapper.extractFile(archivedEntry), charset),
              archivedEntry, charset, colorer, fileProperties, repository.getLocation().toDecodedString());
          model.put("file", textFile);
          modelAndView = new ModelAndView("showTextFile", model);
        } else {
          modelAndView = new ModelAndView("showBinaryFile", model);
        }
      }
    } else if (isBinaryFileExtension(command)) {
      logger.debug("File identified as a binary file");
      modelAndView = new ModelAndView("showBinaryFile", model);
    } else if (isTextFileExtension(command) || isTextMimeType(fileProperties)) {
      getRepositoryService().getFile(repository, command.getPath(), command.getRevisionNumber(), outStream);

      if (RAW_DISPLAY_FORMAT.equals(formatParameter)) {
        final KeywordHandler keywordHandler = new KeywordHandler(fileProperties,
            repository.getLocation().toDecodedString() + command.getPath());
        final String content = keywordHandler.substitute(outStream.toString(charset), charset);
        response.setHeader(WebUtils.CONTENT_DISPOSITION_HEADER,
            "inline; filename=\"" + EncodingUtils.encodeFilename(command.getTarget(), request) + "\"");
        response.setContentType(WebUtils.CONTENT_TYPE_TEXT_PLAIN);
        response.getOutputStream().write(content.getBytes(charset));
        return null;
      } else {
        final TextFile textFile = new TextFile(outStream.toString(charset), command.getPath(), charset,
            colorer, fileProperties, repository.getLocation().toDecodedString());
        model.put("file", textFile);
      }
      modelAndView = new ModelAndView("showTextFile", model);
    } else {
      logger.debug("File unidentified - showing as binary");
      modelAndView = new ModelAndView("showBinaryFile", model);
    }
    return modelAndView;
  }

  /**
   * Checks if given map of svn properties contains a text file mime type.
   *
   * @param properties The svn properties for given file.
   * @return True if text file, false if not.
   */
  protected boolean isTextMimeType(final SVNProperties properties) {
    return SVNProperty.isTextMimeType(properties.getStringValue(SVNProperty.MIME_TYPE));
  }

  /**
   * Checks if given file name indicates a text file.
   *
   * @param command Command
   * @return True if text file, false if not.
   */
  boolean isTextFileExtension(final SVNBaseCommand command) {
    return FilenameUtils.getExtension(command.getPath()).toLowerCase().matches(textFileExtensionPattern);
  }

  /**
   * Checks if given file name indicates a binary file.
   *
   * @param command Command
   * @return True if binary file, false if not.
   */
  boolean isBinaryFileExtension(final SVNBaseCommand command) {
    return FilenameUtils.getExtension(command.getPath()).toLowerCase().matches(binaryFileExtensionPattern);
  }

  /**
   * Checks if given file name indicates an archive file.
   *
   * @param command Command
   * @return True if archive file, false if not.
   */
  protected boolean isArchiveFileExtension(final SVNBaseCommand command) {
    return FilenameUtils.getExtension(command.getPath()).toLowerCase().matches(archiveFileExtensionPattern);
  }

  /**
   * Checks if given file name indicates an image file.
   *
   * @param command Command
   * @return True if image file, false if not.
   */
  protected boolean isImageFileExtension(final SVNBaseCommand command) {
    return mimeFileTypeMap.getContentType(command.getPath()).startsWith("image");
  }

  /**
   * Sets the <tt>Colorer</tt> instance.
   *
   * @param colorer The instance.
   */
  public void setColorer(final Colorer colorer) {
    this.colorer = colorer;
  }

  /**
   * Sets the text file extension pattern.
   *
   * @param fileExtensionPattern The pattern
   */
  public void setTextFileExtensionPattern(final String fileExtensionPattern) {
    textFileExtensionPattern = fileExtensionPattern;
  }

  /**
   * Sets the binary file extension pattern.
   *
   * @param fileExtensionPattern The pattern
   */
  public void setBinaryFileExtensionPattern(final String fileExtensionPattern) {
    binaryFileExtensionPattern = fileExtensionPattern;
  }

  /**
   * Sets the archive file extension pattern.
   *
   * @param fileExtensionPattern The pattern
   */
  public void setArchiveFileExtensionPattern(final String fileExtensionPattern) {
    archiveFileExtensionPattern = fileExtensionPattern;
  }

  /**
   * Sets the mime/file type map.
   *
   * @param mimeFileTypeMap Map.
   */
  public void setMimeFileTypeMap(final FileTypeMap mimeFileTypeMap) {
    this.mimeFileTypeMap = mimeFileTypeMap;
  }

}
