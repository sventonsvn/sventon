package de.berlios.sventon.colorizer;

import java.util.regex.*;

/**
 * Formatter for C and C++ source files.
 *
 * @author jesper@users.berlios.de
 */
public class CPlusPlusFormatter extends FormatterImpl {

  private static Pattern KEYWORDS_PATTERN = Pattern.compile(
      "\\b(asm|auto|bool|break|case|catch|char|class|const|continue|default|delete|do|double|else|entry|enum|explicit|" +
      "extern|false|far|float|for|fortran|friend|goto|if|inline|int|long|mutable|near|new|operator|pascal|private|protected|" +
      "public|register|return|short|signed|sizeof|static|struct|switch|template|this|throw|true|try|typedef|union|" +
      "unsigned|virtual|void|volatile|while)\\b");


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
