/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.tags;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * JSP tag that returns appropriate file type icon for given file extension.
 *
 * @author jesper@sventon.org
 */
public final class FileTypeIconTag extends TagSupport {

  private static final long serialVersionUID = -1953829689152447996L;

  /**
   * Mappings.
   */
  private static final Properties MAPPINGS = new Properties();

  /**
   * File name.
   */
  private String filename;

  /**
   * Property file containing file type mappings.
   */
  private static final String FILE_TYPE_ICON_MAPPINGS_FILENAME = "/fileTypeIconMappings.properties";

  /**
   * Default file icon.
   */
  private static final String DEFAULT_FILE_ICON = "images/icon_file.png";
  private String fallbackIcon = DEFAULT_FILE_ICON;

  @Override
  public int doStartTag() throws JspException {
    try {
      assertMappingsLoaded();
      pageContext.getOut().write(createImageTag(filename, MAPPINGS));
    } catch (final IOException e) {
      throw new JspException(e);
    }
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Makes sure the mappings are loaded.
   *
   * @throws IOException if unable to load mappings.
   */
  private synchronized void assertMappingsLoaded() throws IOException {
    if (MAPPINGS.isEmpty()) {
      InputStream is = null;
      try {
        is = this.getClass().getResourceAsStream(FILE_TYPE_ICON_MAPPINGS_FILENAME);
        if (is == null) {
          throw new FileNotFoundException("Unable to find '" + FILE_TYPE_ICON_MAPPINGS_FILENAME + "' in the CLASSPATH root");
        }
        MAPPINGS.load(is);
      } finally {
        IOUtils.closeQuietly(is);
      }
    }
  }

  /**
   * Creates an <code>IMG</code> tag pointing to the appropriate file type icon.
   *
   * @param filename Filename.
   * @param mappings Extension and image filename mappings.
   * @return <code>IMG</code> tag.
   */
  protected String createImageTag(final String filename, final Properties mappings) {
    Validate.notNull(filename, "Filename was null or empty");
    final String extension = FilenameUtils.getExtension(filename.toLowerCase());
    final String icon = extractIconFromMapping(mappings.getProperty(extension));

    String description = extractDescriptionFromMapping(mappings.getProperty(extension));
    if (description == null) {
      description = extension;
    } else {
      description = StringEscapeUtils.escapeHtml(description);
    }
    return "<img src=\"" + icon + "\" title=\"" + description + "\" alt=\"" + description + "\">";
  }

  /**
   * Extracts description from mapping string.
   *
   * @param mapping Mapping string
   * @return Description, or null if no description exists.
   */
  protected String extractDescriptionFromMapping(final String mapping) {
    if (StringUtils.isEmpty(mapping)) {
      return null;
    }
    final String[] s = mapping.trim().split(";");
    return s.length > 1 ? s[1] : null;
  }

  /**
   * Extracts icon path and name from mapping string.
   *
   * @param mapping Mapping string.
   * @return Icon path and name or {@link #fallbackIcon} if mapping was null.
   */
  protected String extractIconFromMapping(final String mapping) {
    if (mapping == null) {
      return fallbackIcon;
    }
    final String[] s = mapping.trim().split(";");
    return StringUtils.isEmpty(s[0]) ? fallbackIcon : s[0];
  }

  /**
   * Sets the fallback icon to use when no specific mapping exists.
   *
   * @param fallbackIcon Path to fallback icon.
   */
  public void setFallbackIcon(final String fallbackIcon) {
    this.fallbackIcon = fallbackIcon;
  }

  /**
   * Sets the filename.
   *
   * @param filename Filename
   */
  public void setFilename(final String filename) {
    this.filename = filename;
  }

}
