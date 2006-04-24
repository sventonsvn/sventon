package de.berlios.sventon.repository.cache;

import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.List;

/**
 * Contains cached commit messages.
 * This class is a data holder, with various finder methods.
 *
 * @author jesper@users.berlios.de
 */
public interface CommitMessageCache {

  /**
   * Finds occurencies of given search string among the cached commit messages.
   *
   * @param searchString String to search for
   * @return List of something. Kind of depends on Lucene implementation. -- REVISIT
   * @throws Exception
   */
  List<Object> find(final String searchString) throws Exception;

  /**
   * Add one log entry to the cache.
   *
   * @param entry The entry to parse and add
   */
  void add(final SVNLogEntry entry);

  /**
   * Add one or more log entries to the cache.
   *
   * @param entries The entries to parse and add
   */
  void add(final SVNLogEntry... entries);

  /**
   * Gets the cached revision number.
   * Used to determine if cache needs to be updated.
   *
   * @return Cached revision number.
   */
  long getCachedRevision();

  /**
   * Sets the cached revision number.
   * Used if cache has been updated.
   *
   * @param revision Revision number.
   */
  void setCachedRevision(final long revision);

  /**
   * Clears the entire cache.
   */
  void clear();

  /**
   * Gets the repository URL.
   * Used to verifiy that the cache state and url matches.
   *
   * @return The URL to the repository.
   */
  String getRepositoryUrl();

}
