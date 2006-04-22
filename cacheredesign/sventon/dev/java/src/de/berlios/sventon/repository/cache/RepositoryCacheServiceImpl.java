package de.berlios.sventon.repository.cache;

import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.RepositoryConfiguration;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Service class used to access the caches.
 * <p/>
 * Responsibility: Start/stop the transaction, trigger cache update and perform search.
 *
 * @author jesper@users.berlios.de
 */
public class RepositoryCacheServiceImpl implements RepositoryCacheService {

  private SVNRepository repository;
  private RepositoryConfiguration configuration;
  private RepositoryEntryCache repositoryEntryCache;
  private CommitMessageCache commitMessageCache;

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());


  public void setRepositoryConfiguration(final RepositoryConfiguration configuration) {
    this.configuration = configuration;
  }

  public void setIndex(final RepositoryEntryCache repositoryEntryCache) {
    this.repositoryEntryCache = repositoryEntryCache;
  }

  public void setCommitMessageCache(CommitMessageCache commitMessageCache) {
    this.commitMessageCache = commitMessageCache;
  }


  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String searchString) throws Exception {
    //repositoryEntryCache.update(repository);
    return repositoryEntryCache.findByPattern(searchString, RepositoryEntry.Kind.any, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<Object> find(final String searchString) throws Exception {
    commitMessageCache.update(repository);
    return commitMessageCache.find(searchString);
  }


}
