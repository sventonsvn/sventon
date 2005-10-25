package de.berlios.sventon.diff;

/**
 * Exception thrown if diff exceptions occurs.
 *
 * @author jesper@users.berlios.de
 */
public class DiffException extends Exception {

  /**
   * Constructs a new exception with the specified detail message.  The
   * cause is not initialized, and may subsequently be initialized by
   * a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for
   *                later retrieval by the {@link #getMessage()} method.
   */
  public DiffException(String message) {
    super(message);
  }
}
