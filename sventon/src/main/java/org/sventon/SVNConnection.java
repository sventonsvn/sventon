package org.sventon;

import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * SVN connection.
 */
public abstract class SVNConnection {

  /**
   * @return Delegate
   */
  public abstract SVNRepository getDelegate();

  /**
   * Closes the session.
   */
  public abstract void closeSession();

  /**
   * @return The repository URL.
   */
  public abstract String getURL();
}
