package de.berlios.sventon.colorizer;

import java.util.Map;

/**
 * Formatter factory.
 *
 * @author jesper@users.berlios.de
 */
public class FormatterFactory {

  /** The map of available formatters in the factory. */
  private Map<String, Formatter> formatters = null;

  /**
   * Constructor.
   * TODO: Unnecessary? Remove or merge with setAvailableFormatters?
   */
  public FormatterFactory() {
  }

  /**
   * Sets the available <code>Formatter</code> instances.
   * @param formatters The <code>Map</code> of file extensions (in
   * <u>lower case</u>) and <code>Formatter</code> instances.
   */
  public void setAvailableFormatters(final Map<String, Formatter> formatters) {
    this.formatters = formatters;
  }

  /**
   * Gets formatter for given file extension.
   * @param fileExtension The file extension in e.g. <code>java</code>,
   * <code>html</code> etc.
   * @return The <code>Formatter</code> instance. If no formatter
   * exist for given file extension, a generic formatter will be returned.
   */
  public Formatter getFormatterForExtension(final String fileExtension) {
    Formatter formatter = formatters.get(fileExtension.toLowerCase());
    if (formatter == null) {
      return new FormatterImpl();
    }
    return formatter;
  }

}
