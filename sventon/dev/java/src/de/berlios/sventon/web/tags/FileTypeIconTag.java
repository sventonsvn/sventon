package de.berlios.sventon.web.tags;

import org.apache.commons.io.FilenameUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * JSP tag that returns appropriate file type icon for given file extension.
 *
 * @author jesper@users.berlios.de
 */
public final class FileTypeIconTag extends TagSupport {

  private static final Properties MAPPINGS = new Properties();

  private String filename;

  private static final String FILE_TYPE_ICON_MAPPINGS_FILENAME = "/fileTypeIconMappings.properties";

  /**
   * {@inheritDoc}
   */
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

  private synchronized void assertMappingsLoaded() throws IOException {
    if (MAPPINGS.isEmpty()) {
      final InputStream is = this.getClass().getResourceAsStream(FILE_TYPE_ICON_MAPPINGS_FILENAME);
      if (is == null) {
        throw new FileNotFoundException("Unable to find '" + FILE_TYPE_ICON_MAPPINGS_FILENAME + "' in the CLASSPATH root");
      }
      MAPPINGS.load(is);
    }
  }

  /**
   * Creates an <code>IMG</code> tag pointing to the appropriate file type icon.
   *
   * @param filename Filename.
   * @param mappings Extension and image filename mappings.
   * @return <code>IMG</code> tag.
   */
  protected static String createImageTag(final String filename, final Properties mappings) {
    if (filename == null) {
      throw new IllegalArgumentException("Filename was null");
    }

    final String extension = FilenameUtils.getExtension(filename.toLowerCase());
    String icon = (String) mappings.get(extension);
    if (icon == null) {
      icon = "images/icon_file.png";
    }
    return "<img src=\"" + icon + "\" alt=\"" + extension + "\"/>";
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
