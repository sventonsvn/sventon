package de.berlios.sventon.colorizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generic formatter implementation.
 *
 * @author jesper@users.berlios.de
 */
public class FormatterImpl implements Formatter{

  /** Logger for this class and subclasses */
  protected final Log logger = LogFactory.getLog(getClass());

  public static String KEYWORD_SPAN_TAG = "<span class=\"srcKeyword\">";
  public static String COMMENT_SPAN_TAG = "<span class=\"srcComment\">";
  public static String STRING_SPAN_TAG = "<span class=\"srcString\">";
  public static String INTEGER_SPAN_TAG = "<span class=\"srcInteger\">";
  public static String FLOAT_SPAN_TAG = "<span class=\"srcFloat\">";
  public static String CHARACTER_SPAN_TAG = "<span class=\"srcCharacter\">";
  public static String PREPROCESSOR_SPAN_TAG = "<span class=\"srcPreproc\">";
  public static String SPAN_END = "</span>";

  /**
   * {@inheritDoc}
   */
  public String format(String line) {
    return line;
  }

  /**
   * Replaces illegal characters with corresponding <code>HTML</code> entities.
   * <p/>
   * @param line
   * @return The replaced line
   */
  public String replaceEntities(String line) {
    // TODO: Will probably have to modify this later as we for instance want
    // to colorize quoted strings, e.g. String s = "hello";

    line = line.replaceAll("[&]","&amp;");
    line = line.replaceAll("[\"]", "&quot;");
    line = line.replaceAll("[<]", "&lt;");
    return line.replaceAll("[>]", "&gt;");
  }

}
