package de.berlios.sventon.ctrl;

import com.Ostermiller.Syntax.ToHTML;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

/**
 * Colorizes given input.
 * Colorizes given input using the syntax highlighting library
 * from <a href="http://ostermiller.org/syntax/">http://ostermiller.org/syntax/</a>.
 *
 * @link http://ostermiller.org/syntax/
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

    StringReader reader = new StringReader(content.trim());
    StringWriter writer = new StringWriter();
    StringBuilder sb = null;

    try {
      ToHTML toHTML = new ToHTML();
      toHTML.setInput(reader);
      toHTML.setOutput(writer);
      toHTML.setFileExt(fileExtension);
      toHTML.writeHTMLFragment();
    } catch (InvocationTargetException itx){
      // can only happen if setLexerType(java.lang.String) method is used.
      logger.error("Error invoking colorizer.", itx);
    } catch (IOException ioex){
      logger.error(ioex);
    }

    if (appendLineNumbers) {
      String colorizedContent = writer.toString();
      // Must get rid of the <pre> tags to get the line numbers correct.
      colorizedContent = colorizedContent.replaceAll("<pre>", "");
      colorizedContent = colorizedContent.replaceAll("</pre>", "");

      LineNumberReader lineReader = new LineNumberReader(new StringReader(colorizedContent.trim()));
      try {
        String line = "";
        sb = new StringBuilder();
        sb.append("<pre>\n");
        while ((line = lineReader.readLine()) != null) {
          sb.append("<span class=text>");
          sb.append("<a style=\"text-decoration: none;\" name=\"");
          sb.append(lineReader.getLineNumber());
          sb.append("\" href=\"#");
          sb.append(lineReader.getLineNumber());
          sb.append("\">");
          sb.append(StringUtils.leftPad(String.valueOf(lineReader.getLineNumber()), 5, " "));
          sb.append(": ");
          sb.append("</a>");
          sb.append("</span>");
          sb.append(line);
          sb.append("\n");
        }
      } catch (IOException ioex) {
        logger.error(ioex);
      }
      sb.append("</pre>\n");
      return sb.toString();
    } else {
      return writer.toString();
    }
  }

}
