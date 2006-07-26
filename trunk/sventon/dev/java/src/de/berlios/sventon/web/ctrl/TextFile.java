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

import de.berlios.sventon.colorer.Colorer;
import de.berlios.sventon.content.KeywordHandler;
import de.berlios.sventon.content.LineNumberAppender;
import de.berlios.sventon.util.PathUtil;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.repository.RepositoryConfiguration;

import java.io.IOException;
import java.util.Map;

/**
 * Handles text files.
 * Keywords will be expanded and the file will be colorized
 * depending of it's format.
 *
 * @author jesper@users.berlios.de
 */
public class TextFile extends AbstractFile {

  /**
   * Constructor.
   *
   * @param contents The file contents.
   */
  public TextFile(final String contents, final Map properties, final RepositoryConfiguration configuration,
                  final Colorer colorer, final SVNBaseCommand command) throws IOException {

    String fileContents;

    // Expand keywords, if any.
    final KeywordHandler keywordHandler = new KeywordHandler(properties, configuration.getUrl()
        + command.getPath());
    fileContents = keywordHandler.substitute(contents);

    final LineNumberAppender appender = new LineNumberAppender();
    appender.setEmbedStart("<span class=\"sventonLineNo\">");
    appender.setEmbedEnd(":&nbsp;</span>");
    appender.setPadding(5);
    fileContents = appender.appendTo(colorer.getColorizedContent(fileContents,
        PathUtil.getFileExtension(command.getTarget())));

    model.put("fileContents", fileContents);
  }

}
