package org.sventon.service.svnkit;

import org.sventon.SVNConnection;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * SVNKitConnection..
 */
public class SVNKitConnection extends SVNConnection {
  private SVNRepository delegate;

  /**
   * Constructor.
   *
   * @param delegate Delegate
   */
  public SVNKitConnection(SVNRepository delegate) {
    this.delegate = delegate;
  }

  @Override
  public SVNRepository getDelegate() {
    return delegate;
  }

  @Override
  public void closeSession() {
    delegate.closeSession();
  }

  @Override
  public String getURL() {
    return delegate.getLocation().toDecodedString();
  }
}
