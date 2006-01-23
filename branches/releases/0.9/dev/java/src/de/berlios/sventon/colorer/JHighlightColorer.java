/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.colorer;

import com.uwyn.jhighlight.renderer.Renderer;
import de.berlios.sventon.util.PathUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;

/**
 * Colorizes given input using the JHighlight syntax highlighting library.
 *
 * @link http://jhighlight.dev.java.net
 * @author jesper@users.berlios.de
 */
public class JHighlightColorer implements Colorer {

  private final Log logger = LogFactory.getLog(getClass());

  private Properties rendererMappings;

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

    Renderer renderer = getRenderer(filename);
    StringBuilder sb = new StringBuilder();

    if (renderer == null) {
      return StringEscapeUtils.escapeXml(content);
    }

    try {
      sb.append(renderer.highlight(null, content, "ISO-8859-1", true, true));
    } catch (Exception ioex) {
      logger.error(ioex);
    }
    return sb.toString();
  }

  /**
   * Gets the <code>Renderer</code> instance for given filename,
   * based on it's extension.
   *
   * @param filename The filename
   * @return The JHighlight <code>Renderer</code> instance.
   */
  protected Renderer getRenderer(final String filename) {
    if (filename == null) {
      throw new IllegalArgumentException("Filename cannot be null");
    }
    return (Renderer) rendererMappings.get(PathUtil.getFileExtension(filename.toLowerCase()));
  }

  /**
   * Sets the file type / renderer mapping
   *
   * @param rendererMappings The mappings
   */
  public void setRendererMappings(Properties rendererMappings) {
    this.rendererMappings = rendererMappings;
  }

}
