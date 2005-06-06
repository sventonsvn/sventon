package de.berlios.sventon.colorizer;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Formatter for XML files.
 *
 * @author jesper@users.berlios.de
 */
public class XMLFormatter extends FormatterImpl {

  /** Pattern to identify an XML tag. */
  private static Pattern KEYWORDS_PATTERN = Pattern.compile("<(\\w*)[^>]*>");
/* "<(\w*)[^>]*>.*?</(\w*)>" */
  /**
   * {@inheritDoc}
   */
  public String format(String line) {
    logger.debug("Before: " + line);
    line = line.replaceAll("[&]","&amp;");
    line = line.replaceAll("[\"]", "&quot;");

    Matcher matcher = KEYWORDS_PATTERN.matcher(line);
    StringBuffer sb = new StringBuffer();
    boolean result = matcher.find();
    // Loop through and create a new String with the replacements
    String matchedTag;
    while(result) {
      matcher.appendReplacement(sb, KEYWORD_SPAN_TAG);
      matchedTag = matcher.group();
      matchedTag = matchedTag.replaceAll("<", "&lt;");
      matchedTag = matchedTag.replaceAll(">", "&gt;");
      sb.append(matchedTag);
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
