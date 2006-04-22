package de.berlios.sventon.repository.cache;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.List;

/**
 * Contains cached commit messages.
 * This implementation should use Lucene internally.
 * This class is a data holder only, with various finder methods.
 *
 * @author jesper@users.berlios.de
 */
public class CommitMessageCacheImpl implements CommitMessageCache {

  /**
   * {@inheritDoc}
   */
  public List<Object> find(final String searchString) throws Exception {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public void add(final SVNLogEntry... entry) {

  }

  /**
   * {@inheritDoc}
   */
  public long getCachedRevision() {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * {@inheritDoc}
   */
  public void setCachedRevision(final long revision) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * {@inheritDoc}
   */
  public void clear() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * {@inheritDoc}
   */
  public String getRepositoryUrl() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * {@inheritDoc}
   */
  public synchronized void update(final SVNRepository repository) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

}
