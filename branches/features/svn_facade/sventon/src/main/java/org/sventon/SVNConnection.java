package org.sventon;

import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * SVN connection.
 */
public abstract class SVNConnection {

  public abstract SVNRepository getDelegate();

  public abstract void closeSession();
}
