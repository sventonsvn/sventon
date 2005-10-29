package de.berlios.sventon.colorer;

import com.uwyn.jhighlight.renderer.Renderer;
import com.uwyn.jhighlight.renderer.RendererFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.IOException;

/**
 * Colorizes given input using the JHighlight syntax highlighting library.
 *
 * @link http://jhighlight.dev.java.net
 * @author jesper@users.berlios.de
 */
public class JHighlightColorer implements Colorer {

  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Constructor.
   */
  public JHighlightColorer() {
  }

  /**
   * {@inheritDoc}
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
      return StringEscapeUtils.escapeXml(content);
    }
  }

}
