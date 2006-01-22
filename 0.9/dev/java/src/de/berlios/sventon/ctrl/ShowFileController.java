/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.ctrl;

import de.berlios.sventon.colorer.Colorer;
import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.svnsupport.KeywordHandler;
import de.berlios.sventon.svnsupport.LineNumberAppender;
import de.berlios.sventon.svnsupport.SventonException;
import de.berlios.sventon.util.ImageUtil;
import de.berlios.sventon.util.PathUtil;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * ShowFileController.
 *
 * @author patrikfr@users.berlios.de
 */
public class ShowFileController extends AbstractSVNTemplateController implements Controller {

  private Colorer colorer;

  private ImageUtil imageUtil;

  private String archiveFileExtensionPattern;

  /**
   * Sets the <tt>Colorer</tt> instance.
   *
   * @param colorer The instance.
   */
  public void setColorer(Colorer colorer) {
    this.colorer = colorer;
  }

  /**
   * Gets <tt>Colorer</tt> instance.
   *
   * @return The instance.
   */
  public Colorer getColorer() {
    return colorer;
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
   * Gets the archive file extension pattern.
   *
   * @return The Pattern
   */
  public String getArchiveFileExtensionPattern() {
    return archiveFileExtensionPattern;
  }

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
                                   HttpServletRequest request, HttpServletResponse response, BindException exception) throws SventonException, SVNException {

    logger.debug("Assembling file contents for: " + svnCommand);
    Map<String, Object> model = new HashMap<String, Object>();

    HashMap properties = new HashMap();
    // Get the file's properties without requesting the content.
    repository.getFile(svnCommand.getPath(), revision.getNumber(), properties, null);
    logger.debug(properties);
    model.put("properties", properties);

    if (SVNProperty.isTextMimeType((String) properties.get(SVNProperty.MIME_TYPE))) {
      model.putAll(handleTextFile(repository, svnCommand, revision, properties));
    } else {
      // It's a binary file
      logger.debug("Binary file detected");
      model.put("isBinary", true);  // Indicates that the file is in binary format.
      model.put("isImage", getImageUtil().isImageFileExtension(PathUtil.getFileExtension(svnCommand.getPath())));

      if (PathUtil.getFileExtension(svnCommand.getPath()).toLowerCase().
          matches(getArchiveFileExtensionPattern())) {
        logger.debug("Binary file as an archive file");
        model.putAll(handleArchiveFile(repository, svnCommand, revision));
      }
    }
    return new ModelAndView("showfile", model);
  }


  /**
   * Internal method for handling text files.
   * Keywords will be expanded and the file will be colorized
   * depending of it's format.
   *
   * @param repository The repository
   * @param svnCommand The command
   * @param revision The revision
   * @param properties The file's properties
   * @return Populated model.
   * @throws SVNException if Subversion error.
   * @throws SventonException if sventon error.
   */
  private Map<String, Object> handleTextFile(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                             final SVNRevision revision, final Map properties) throws SventonException, SVNException {

    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    Map<String, Object> model = new HashMap<String, Object>();
    // Get the file's content. We can skip the properties in this case.
    repository.getFile(svnCommand.getPath(), revision.getNumber(), null, outStream);

    String fileContents;
    try {
      // Expand keywords, if any.
      KeywordHandler keywordHandler = new KeywordHandler(properties,
          getRepositoryConfiguration().getUrl() + svnCommand.getPath());
      fileContents = keywordHandler.substitute(outStream.toString());

      LineNumberAppender appender = new LineNumberAppender();
      appender.setEmbedStart("<span class=\"sventonLineNo\">");
      appender.setEmbedEnd(":&nbsp;</span>");
      appender.setPadding(5);
      fileContents = appender.appendTo(getColorer().getColorizedContent(fileContents, svnCommand.getTarget()));
    } catch (IOException ioex) {
      throw new SventonException(ioex);
    }

    logger.debug("Create model");
    model.put("fileContents", fileContents);
    return model;
  }


  /**
   * Internal method for handling archive files.
   *
   * @param repository The repository
   * @param svnCommand The command
   * @param revision The revision
   * @return Populated model.
   * @throws SVNException if Subversion error.
   * @throws SventonException if sventon error.
   */
  private Map<String, Object> handleArchiveFile(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                             final SVNRevision revision) throws SventonException, SVNException {
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    Map<String, Object> model = new HashMap<String, Object>();

    model.put("isArchive", true); // Indicates that the file is an archive (zip or jar)

    // Get the file's content. We can skip the properties in this case.
    repository.getFile(svnCommand.getPath(), revision.getNumber(), null, outStream);

    ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(outStream.toByteArray()));
    List<ZipEntry> archiveEntries = new ArrayList<ZipEntry>();
    try {
      ZipEntry zipEntry;
      while ((zipEntry = zip.getNextEntry()) != null)
        archiveEntries.add(zipEntry);
    } catch (IOException ioex) {
      throw new SventonException("Unable to show contents of archive file", ioex);
    }
    model.put("entries", archiveEntries);
    return model;
  }

  /**
   * Gets the <code>ImageUtil</code> helper instance.
   *
   * @return The <code>ImageUtil</code>
   * @see de.berlios.sventon.util.ImageUtil
   */
  public ImageUtil getImageUtil() {
    return imageUtil;
  }

  /**
   * Sets the <code>ImageUtil</code> helper instance.
   *
   * @param imageUtil The instance
   * @see de.berlios.sventon.util.ImageUtil
   */
  public void setImageUtil(ImageUtil imageUtil) {
    this.imageUtil = imageUtil;
  }

}
