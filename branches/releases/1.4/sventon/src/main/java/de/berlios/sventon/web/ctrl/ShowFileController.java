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

import de.berlios.sventon.colorer.Colorer;
import de.berlios.sventon.model.ArchiveFile;
import de.berlios.sventon.model.TextFile;
import de.berlios.sventon.util.WebUtils;
import de.berlios.sventon.util.ZipUtils;
import de.berlios.sventon.util.EncodingUtils;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserRepositoryContext;
import de.berlios.sventon.content.KeywordHandler;
import org.apache.commons.io.FilenameUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * ShowFileController.
 *
 * @author patrikfr@users.berlios.de
 * @author jesper@users.berlios.de
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
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    logger.debug("Assembling file contents for: " + svnCommand);

    final String formatParameter = ServletRequestUtils.getStringParameter(request, FORMAT_REQUEST_PARAMETER, null);
    final String archivedEntry = ServletRequestUtils.getStringParameter(request, ARCHIVED_ENTRY, null);
    final boolean forceDisplay = ServletRequestUtils.getBooleanParameter(request, FORCE_ARCHIVED_ENTRY_DISPLAY, false);
    final Map<String, Object> model = new HashMap<String, Object>();
    final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    final Map fileProperties = getRepositoryService().getFileProperties(repository, svnCommand.getPath(), revision.getNumber());

    logger.debug(fileProperties);

    final String charset = userRepositoryContext.getCharset();
    logger.debug("Using charset encoding: " + charset);

    model.put("properties", fileProperties);
    model.put("committedRevision", fileProperties.get(SVNProperty.COMMITTED_REVISION));

    final ModelAndView modelAndView;

    if (isImageFileExtension(svnCommand)) {
      logger.debug("File identified as an image file");
      modelAndView = new ModelAndView("showImageFile", model);
    } else if (isArchiveFileExtension(svnCommand)) {
      if (archivedEntry == null) {
        logger.debug("File identified as an archive file");
        getRepositoryService().getFile(repository, svnCommand.getPath(), revision.getNumber(), outStream);
        final ArchiveFile archiveFile = new ArchiveFile(outStream.toByteArray());
        model.put("entries", archiveFile.getEntries());
        modelAndView = new ModelAndView("showArchiveFile", model);
      } else {
        logger.debug("Archived entry: " + archivedEntry);
        model.put("archivedEntry", archivedEntry);
        final String contentType = mimeFileTypeMap.getContentType(archivedEntry);
        logger.debug("Detected content-type: " + contentType);

        if (contentType != null && contentType.startsWith("text") || forceDisplay) {
          getRepositoryService().getFile(repository, svnCommand.getPath(), revision.getNumber(), outStream);
          final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(outStream.toByteArray()));
          logger.debug("Extracting [" + archivedEntry + "] from archive [" + svnCommand.getPath() + "]");
          final TextFile textFile = new TextFile(new String(ZipUtils.extractFile(zis, archivedEntry), charset),
              archivedEntry, charset, colorer, fileProperties, repository.getLocation().toDecodedString());
          model.put("file", textFile);
          modelAndView = new ModelAndView("showTextFile", model);
        } else {
          modelAndView = new ModelAndView("showBinaryFile", model);
        }
      }
    } else if (isBinaryFileExtension(svnCommand)) {
      logger.debug("File identified as a binary file");
      modelAndView = new ModelAndView("showBinaryFile", model);
    } else if (isTextFileExtension(svnCommand) || isTextMimeType(fileProperties)) {
      getRepositoryService().getFile(repository, svnCommand.getPath(), revision.getNumber(), outStream);

      if (RAW_DISPLAY_FORMAT.equals(formatParameter)) {
        final KeywordHandler keywordHandler = new KeywordHandler(fileProperties,
            repository.getLocation().toDecodedString() + svnCommand.getPath());
        final String content = keywordHandler.substitute(outStream.toString(charset), charset);
        response.setHeader(WebUtils.CONTENT_DISPOSITION_HEADER,
            "inline; filename=\"" + EncodingUtils.encodeFilename(svnCommand.getTarget(), request) + "\"");
        response.setContentType(WebUtils.CONTENT_TYPE_TEXT_PLAIN);
        response.getOutputStream().write(content.getBytes(charset));
        return null;
      } else {
        final TextFile textFile = new TextFile(outStream.toString(charset), svnCommand.getPath(), charset,
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
  protected boolean isTextMimeType(final Map properties) {
    return SVNProperty.isTextMimeType((String) properties.get(SVNProperty.MIME_TYPE));
  }

  /**
   * Checks if given file name indicates a text file.
   *
   * @param svnCommand Command
   * @return True if text file, false if not.
   */
  protected boolean isTextFileExtension(final SVNBaseCommand svnCommand) {
    return FilenameUtils.getExtension(svnCommand.getPath()).toLowerCase().matches(textFileExtensionPattern);
  }

  /**
   * Checks if given file name indicates a binary file.
   *
   * @param svnCommand Command
   * @return True if binary file, false if not.
   */
  protected boolean isBinaryFileExtension(final SVNBaseCommand svnCommand) {
    return FilenameUtils.getExtension(svnCommand.getPath()).toLowerCase().matches(binaryFileExtensionPattern);
  }

  /**
   * Checks if given file name indicates an archive file.
   *
   * @param svnCommand Command
   * @return True if archive file, false if not.
   */
  protected boolean isArchiveFileExtension(final SVNBaseCommand svnCommand) {
    return FilenameUtils.getExtension(svnCommand.getPath()).toLowerCase().matches(archiveFileExtensionPattern);
  }

  /**
   * Checks if given file name indicates an image file.
   *
   * @param svnCommand Command
   * @return True if image file, false if not.
   */
  protected boolean isImageFileExtension(final SVNBaseCommand svnCommand) {
    return mimeFileTypeMap.getContentType(svnCommand.getPath()).startsWith("image");
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
