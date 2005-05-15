package de.berlios.sventon.colorizer;

/**
 * Formatter interface.
 *
 * @author jesper@users.berlios.de 
 */
public interface Formatter {

  /**
   * Formats a String.
   * @param line The String to format
   * @return The formatted String.
   */
  String format(final String line);
}
