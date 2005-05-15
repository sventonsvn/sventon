package de.berlios.sventon.colorizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract formatter.
 *
 * @author jesper@users.berlios.de
 */
public abstract class AbstractFormatter implements Formatter{

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
}
