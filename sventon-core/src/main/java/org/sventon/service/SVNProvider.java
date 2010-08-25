package org.sventon.service;

import org.sventon.SVNConnectionFactory;

/**
 * Represents a Subversion provider, eg. svnjavahl and SVNKit.
 */
public interface SVNProvider {

  SVNConnectionFactory getConnectionFactory();

  RepositoryService getRepositoryService();

}
