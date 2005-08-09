package de.berlios.sventon.ctrl;

import com.uwyn.jhighlight.renderer.Renderer;
import com.uwyn.jhighlight.renderer.RendererFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
   * @param filename The filename, used to determine formatter.
   * @return The colorized string.
   */
  public String getColorizedContent(final String content, final String filename) {
    logger.debug("Colorizing content, filename: " + filename);

    StringBuilder sb = new StringBuilder();
    Renderer renderer = RendererFactory.INSTANCE.getRenderer(filename);
    if (renderer != null) {
      try {
        sb.append(renderer.highlight(null, content, "ISO-8859-1", true, true));
      } catch (IOException ioex) {
        logger.error(ioex);
      } finally {
        return sb.toString();
      }
    } else {
      return content;
    }
  }

}
