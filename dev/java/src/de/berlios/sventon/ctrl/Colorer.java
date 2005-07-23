package de.berlios.sventon.ctrl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.IOException;

/**
 * Colorizes given input using the JHighlight syntax highlighting library.
 *
 * @link http://jhighlight.dev.java.net
 * @author jesper@users.berlios.de
 */
public class Colorer {

  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Constructor.
   */
  public Colorer() {
  }

  /**
   * Converts the file contents into colorized HTML code.
   * @param content The file contents.
   * @param fileExtension The file extension, used to determine formatter.
   * @param appendLineNumbers If true, all lines will start with a line number.
   * @return The colorized string.
   */
  public String getColorizedContent(final String content, final String fileExtension,
                                    final boolean appendLineNumbers) {
    logger.debug("Colorizing content, file extension: " + fileExtension);

    //TODO: Use JHighlight (after necessary modifications have been done)

    StringBuilder sb = new StringBuilder();

    if (appendLineNumbers) {
      LineNumberReader lineReader = new LineNumberReader(new StringReader(content));
      try {
        String line = "";
        while ((line = lineReader.readLine()) != null) {
          sb.append("<a class=\"sventonLineNo\" name=\"");
          sb.append(lineReader.getLineNumber());
          sb.append("\" href=\"#");
          sb.append(lineReader.getLineNumber());
          sb.append("\">");
          sb.append(StringUtils.leftPad(String.valueOf(lineReader.getLineNumber()), 5, " "));
          sb.append(": ");
          sb.append("</a>");
          sb.append(line);
          sb.append("\n");
        }
      } catch (IOException ioex) {
        logger.error(ioex);
      }
      return sb.toString();
    } else {
      // No colorization and no line numbers - return string as it was for now.
      return content;
    }
  }

}
