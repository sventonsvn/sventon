/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.colorer;

import com.uwyn.jhighlight.renderer.Renderer;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.Colorer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Colorizes given input using the JHighlight syntax highlighting library.
 *
 * @author jesper@sventon.org
 * @link http://jhighlight.dev.java.net
 */
public final class JHighlightColorer implements Colorer {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * File type <-> renderer mappings.
   */
  private Map<String, Renderer> rendererMappings = new HashMap<String, Renderer>();

  @Override
  public String getColorizedContent(final String content, final String fileExtension, final String encoding) throws IOException {
    logger.debug("Colorizing content, file extension: " + fileExtension);

    if (content == null) {
      return "";
    }

    final Renderer renderer = getRenderer(fileExtension);

    if (renderer == null) {
      return StringEscapeUtils.escapeXml(content);
    }

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes(encoding));
    renderer.highlight(null, in, out, encoding, true);

    // Sorry Geert, but we need to remove the trailing BR-tags
    // and the initial comment, as it messes up the line numbering.
    return out.toString(encoding).substring(73).replace("<br />", "").trim();
  }

  /**
   * Gets the <code>Renderer</code> instance for given file extension,
   * based on it's extension.
   *
   * @param fileExtension The file extension.
   * @return The JHighlight <code>Renderer</code> instance.
   */
  protected Renderer getRenderer(final String fileExtension) {
    Validate.notNull(fileExtension, "File extension cannot be null");
    return rendererMappings.get(fileExtension.toLowerCase());
  }

  /**
   * Sets the file extension / renderer mapping.
   *
   * @param rendererMappings The mappings
   */
  public void setRendererMappings(final Map<String, Renderer> rendererMappings) {
    Validate.notNull(rendererMappings, "Mappings cannot be null");
    this.rendererMappings = rendererMappings;
  }

}
