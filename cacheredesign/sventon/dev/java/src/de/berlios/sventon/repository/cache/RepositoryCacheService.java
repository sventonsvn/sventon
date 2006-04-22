package de.berlios.sventon.repository.cache;

import de.berlios.sventon.repository.RepositoryEntry;

import java.util.List;

/**
 * Service class used to access the caches.
 * <p/>
 * Responsibility: Start/stop the transaction, trigger cache update and perform search.
 *
 * @author jesper@users.berlios.de
 */
public interface RepositoryCacheService {

  /**
   * Searched the cached entries for given string (name fragment).
   *
   * @param searchString String to search for
   * @return List of entries
   * @throws Exception if error
   */
  List<RepositoryEntry> findEntry(final String searchString) throws Exception;

  //List<RepositoryEntry> findByPattern(final String searchString, Kind kind) throws Exception;
  //List<RepositoryEntry> findByPattern(final String searchString, final String startDir) throws Exception;
  //List<RepositoryEntry> findByPattern(final String searchString, final String startDir, Kind kind) throws Exception;

  /**
   * Searches the cached commit messages for given string.
   *
   * @param searchString String to search for
   * @return List of something. Depends on Lucene.
   * @throws Exception if error
   */
  List<Object> find(final String searchString) throws Exception;

}
