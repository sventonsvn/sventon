package de.berlios.sventon.index;

import de.berlios.sventon.ctrl.SventonException;

/**
 * Exception thrown by <code>RevisionIndexer</code>.
 *
 * @author jesper@users.berlios.de
 */
public class RevisionIndexException extends SventonException {

  /**
   * Constructs a new exception with the specified detail message and
   * cause.  <p>Note that the detail message associated with
   * <code>cause</code> is <i>not</i> automatically incorporated in
   * this exception's detail message.
   *
   * @param message the detail message (which is saved for later retrieval
   *                by the {@link #getMessage()} method).
   * @param cause   the cause (which is saved for later retrieval by the
   *                {@link #getCause()} method).  (A <tt>null</tt> value is
   *                permitted, and indicates that the cause is nonexistent or
   *                unknown.)
   */
  public RevisionIndexException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
