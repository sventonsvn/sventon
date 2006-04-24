package de.berlios.sventon.repository.cache;

import de.berlios.sventon.repository.RepositoryConfiguration;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Current indexed revision.
   */
  private long cachedRevision = 0;

  /**
   * Indexed URL.
   */
  private String repositoryURL;

  /**
   * Constructor.
   *
   * @param configuration Repository config
   */
  public CommitMessageCacheImpl(final RepositoryConfiguration configuration) {
    logger.debug("Initializing cache using [" + configuration.getUrl() + "]");
    this.repositoryURL = configuration.getUrl();
  }

  /**
   * {@inheritDoc}
   */
  public List<Object> find(final String searchString) throws Exception {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public synchronized void add(final SVNLogEntry entry) {
  }

  /**
   * {@inheritDoc}
   */
  public synchronized void add(final SVNLogEntry... entries) {
  }

  /**
   * {@inheritDoc}
   */
  public long getCachedRevision() {
    return cachedRevision;
  }

  /**
   * {@inheritDoc}
   */
  public synchronized void setCachedRevision(final long revision) {
    this.cachedRevision = revision;
  }

  /**
   * {@inheritDoc}
   */
  public String getRepositoryUrl() {
    return repositoryURL;
  }


  /**
   * {@inheritDoc}
   */
  public synchronized void clear() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

}
