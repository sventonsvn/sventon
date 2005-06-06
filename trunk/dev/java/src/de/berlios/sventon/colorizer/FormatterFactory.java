package de.berlios.sventon.colorizer;

import java.util.Map;

/**
 * Formatter factory.
 *
 * @author jesper@users.berlios.de
 */
public class FormatterFactory {

  /** The map of available formatters in the factory. */
  private Map<String, String> formatters = null;

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
  public void setAvailableFormatters(final Map<String, String> formatters) {
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
    if (formatters == null || formatters.size() == 0) {
      throw new IllegalStateException("Method 'setAvailableFormatters()' must be called before any formatter can be gotten.");
    }
    Object formatter = null;
    String formatterClassName = formatters.get(fileExtension.toLowerCase());
    if (formatterClassName == null) {
      return new FormatterImpl();
    } else {
      try {
          formatter = Class.forName(formatterClassName).newInstance();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    return (Formatter) formatter;
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return "FormatterFactory{" +
    "formatters=" + formatters +
    "}";
  }
}
