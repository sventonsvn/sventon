package de.berlios.sventon.colorizer;

import java.util.regex.*;

/**
 * Formatter for Java source files.
 *
 * @author jesper@users.berlios.de
 */
public class JavaFormatter extends AbstractFormatter {

  private static Pattern KEYWORDS_PATTERN = Pattern.compile(
      "\\b(abstract|boolean|break|byte|byvalue|case|catch|char|class|const|continue|default|do|double|else|" +
      "extends|false|final|finally|float|for|goto|if|implements|import|int|instanceof|long|native|new|null|" +
      "package|private|public|return|short|static|super|switch|synchronized|this|threadsafe|throw|transient|" +
      "true|try|void|while)\\b");

  
  /**
   * {@inheritDoc}
   */
  public String format(String line) {
    line = replaceEntities(line);
    logger.debug("Before: " + line);
    Matcher matcher = KEYWORDS_PATTERN.matcher(line);
    StringBuffer sb = new StringBuffer();
    boolean result = matcher.find();
    // Loop through and create a new String with the replacements
    while(result) {
      matcher.appendReplacement(sb, KEYWORD_SPAN_TAG);
      sb.append(matcher.group());
      sb.append(SPAN_END);
      result = matcher.find();
    }
    // Add the last segment of input to
    // the new String
    matcher.appendTail(sb);
    logger.debug("After:  " + sb.toString());
    return sb.toString();
  }

}
