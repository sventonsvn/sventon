package de.berlios.sventon.web.model;

import de.berlios.sventon.colorer.Colorer;
import de.berlios.sventon.content.LineNumberAppender;
import de.berlios.sventon.util.PathUtil;

import java.io.IOException;
import java.util.Map;

/**
 * Represents a HTML decorated text file.
 * Keywords will be expanded and the file will be colorized depending on it's format.
 *
 * @author jesper@users.berlios.de
 */
public class HTMLDecoratedTextFile extends TextFile {

  /**
   * Constructor.
   *
   * @param content       The file content.
   * @param properties    The file properties
   * @param repositoryURL The repository root URL
   * @param path          The target path
   * @param colorer       The colorer instance
   */
  public HTMLDecoratedTextFile(final String content, final Map properties, final String repositoryURL,
                               final String path, final Colorer colorer) throws IOException {

    super(content, properties, repositoryURL, path);

    final LineNumberAppender appender = new LineNumberAppender();
    appender.setEmbedStart("<span class=\"sventonLineNo\">");
    appender.setEmbedEnd(":&nbsp;</span>");
    appender.setPadding(5);
    this.content = appender.appendTo(colorer.getColorizedContent(this.content,
        PathUtil.getFileExtension(PathUtil.getTarget(path))));

    model.put("fileContent", this.content);
    model.put("isRawFormat", false);
  }

}
