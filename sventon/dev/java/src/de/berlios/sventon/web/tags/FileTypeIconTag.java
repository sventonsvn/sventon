package de.berlios.sventon.web.tags;

import de.berlios.sventon.util.PathUtil;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
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

  static {
/*
    MAPPINGS.put("c", "images/icon_file_c.png");
    MAPPINGS.put("h", "images/icon_file_h.png");
    MAPPINGS.put("cpp", "images/icon_file_cplusplus.png");
    MAPPINGS.put("cs", "images/icon_file_csharp.png");
    MAPPINGS.put("php", "images/icon_file_php.png");
    MAPPINGS.put("pdf", "images/icon_file_acrobat.png");
    MAPPINGS.put("as", "images/icon_file_actionscript.png");
    MAPPINGS.put("cfm", "images/icon_file_coldfusion.png");
    MAPPINGS.put("doc", "images/icon_file_word.png");
    MAPPINGS.put("xls", "images/icon_file_excel.png");
    MAPPINGS.put("ppt", "images/icon_file_powerpoint.png");
    MAPPINGS.put("rb", "images/icon_file_ruby.png");
    MAPPINGS.put("txt", "images/icon_file_text.png");
    MAPPINGS.put("jar", "images/icon_file_zip.png");
    MAPPINGS.put("zip", "images/icon_file_zip.png");
    MAPPINGS.put("war", "images/icon_file_zip.png");
    MAPPINGS.put("ear", "images/icon_file_zip.png");
    MAPPINGS.put("vbproj", "images/icon_file_visualstudio.png");
    MAPPINGS.put("vcproj", "images/icon_file_visualstudio.png");
    MAPPINGS.put("vaf", "images/icon_file_visualstudio.png");
    MAPPINGS.put("vam", "images/icon_file_visualstudio.png");
    MAPPINGS.put("vdp", "images/icon_file_visualstudio.png");
    MAPPINGS.put("vdproj", "images/icon_file_visualstudio.png");
    MAPPINGS.put("vsmproj", "images/icon_file_visualstudio.png");
    MAPPINGS.put("swf", "images/icon_file_flash.png");
    MAPPINGS.put("fla", "images/icon_file_flash.png");
    MAPPINGS.put("arj", "images/icon_file_compressed.png");
    MAPPINGS.put("lha", "images/icon_file_compressed.png");
    MAPPINGS.put("rar", "images/icon_file_compressed.png");
    MAPPINGS.put("uc2", "images/icon_file_compressed.png");
    MAPPINGS.put("ace", "images/icon_file_compressed.png");
    MAPPINGS.put("sqz", "images/icon_file_compressed.png");
    MAPPINGS.put("tar", "images/icon_file_compressed.png");
    MAPPINGS.put("tgz", "images/icon_file_compressed.png");
    MAPPINGS.put("mda", "images/icon_file_database.png");
    MAPPINGS.put("mdb", "images/icon_file_database.png");
    MAPPINGS.put("db", "images/icon_file_database.png");
*/
  }

  /**
   * {@inheritDoc}
   */
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

    final String extension = PathUtil.getFileExtension(filename.toLowerCase());
    String icon = (String) mappings.get(extension);
    if (icon == null) {
      icon = "images/icon_file.png";
    }
    final StringBuilder sb = new StringBuilder("<img src=\"");
    sb.append(icon);
    sb.append("\" alt=\"");
    sb.append(extension);
    sb.append("\"/>");

    return sb.toString();
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
