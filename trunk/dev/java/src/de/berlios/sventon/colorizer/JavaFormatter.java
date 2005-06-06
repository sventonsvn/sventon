package de.berlios.sventon.colorizer;

import java.util.regex.*;

/**
 * Formatter for Java source files.
 *
 * @author jesper@users.berlios.de
 */
public class JavaFormatter extends FormatterImpl {

  private static Pattern KEYWORDS_PATTERN = Pattern.compile(
      "\\b(abstract|boolean|break|byte|byvalue|case|catch|char|class|const|continue|default|do|double|else|" +
      "extends|false|final|finally|float|for|goto|if|implements|import|int|instanceof|long|native|new|null|" +
      "package|private|public|return|short|static|super|switch|synchronized|this|threadsafe|throw|transient|" +
      "true|try|void|while)\\b");

  private static Pattern COMMENT_PATTERN = Pattern.compile("(/\\*).*(\\*/)");
  private static Pattern COMMENT_MULTILINE_START_PATTERN = Pattern.compile("(/\\*.*)");
  private static Pattern COMMENT_MULTILINE_END_PATTERN = Pattern.compile("(.*\\*/)");

  private boolean previousLineWasComment = false;

  /**
   * {@inheritDoc}
   */
  public String format(String line) {
    line = replaceEntities(line);
    logger.debug("Before: " + line);
    Matcher matcher = null;
    StringBuffer sb = new StringBuffer();

    if (previousLineWasComment) {
      sb.append(COMMENT_SPAN_TAG);
      matcher = COMMENT_MULTILINE_END_PATTERN.matcher(line);
      if (matcher.find()) {
        sb.append(matcher.group());
        sb.append(SPAN_END);
        previousLineWasComment = false;
      } else {
        // Previous line was a comment and we did not find any comment-closing on this line
        sb.append(line);
        sb.append(SPAN_END);
        return sb.toString();
      }
    }

    matcher = COMMENT_PATTERN.matcher(line);
    if (matcher.find()) {
      matcher.appendReplacement(sb, COMMENT_SPAN_TAG);
      sb.append(matcher.group());
      sb.append(SPAN_END);
    } else {
      matcher = COMMENT_MULTILINE_START_PATTERN.matcher(line);
      if (matcher.find()) {
        matcher.appendReplacement(sb, COMMENT_SPAN_TAG);
        sb.append(matcher.group());
        sb.append(SPAN_END);
        previousLineWasComment = true;
      } else {
        // Start processing the keywords.
        matcher = KEYWORDS_PATTERN.matcher(line);

        if (matcher.find()) {
          // Loop through and create a new String with the replacements
          do {
            matcher.appendReplacement(sb, KEYWORD_SPAN_TAG);
            sb.append(matcher.group());
            sb.append(SPAN_END);
          } while (matcher.find());
          // Add the last segment of input to
          // the new String
          matcher.appendTail(sb);
        }
      }
    }
    logger.debug("After:  " + sb.toString());
    return sb.toString();
  }

}
