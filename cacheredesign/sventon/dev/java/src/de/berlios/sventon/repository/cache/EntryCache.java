package de.berlios.sventon.repository.cache;

import de.berlios.sventon.repository.RepositoryEntry;

import java.util.List;
import java.util.Set;

/**
 * Contains a cached set of the repository entries for a specific revision.
 * This class is a data holder, with various finder methods.
 *
 * @author jesper@users.berlios.de
 */
public interface EntryCache {

  /**
   * Finds entry names based on given regex pattern.
   *
   * @param pattern Entry name pattern to search for
   * @return List of entries.
   * @throws CacheException if error
   */
  List<RepositoryEntry> findByPattern(final String pattern, final RepositoryEntry.Kind kind, final Integer limit) throws CacheException;

  /**
   * Add one or more entries to the cache.
   *
   * @param entry The entry to parse and add
   */
  boolean add(final RepositoryEntry entry);

  /**
   * Add one or more entries to the cache.
   *
   * @param entries The entries to parse and add
   */
  boolean add(final List<RepositoryEntry> entries);

  /**
   * Removes entries from the cache, based by path and file name.
   *
   * @param pathAndName Entry to remove from cache.
   * @param recursive   True if remove should be performed recursively
   */
  void removeByName(final String pathAndName, final boolean recursive);

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
   * Gets all entries in the cache.
   *
   * @return All entries currently in the cache
   */
  Set<RepositoryEntry> getUnmodifiableEntries();

  /**
   * Gets the repository URL.
   * Used to verifiy that the cache state and url matches.
   *
   * @return The URL to the repository.
   */
  String getRepositoryUrl();

}
