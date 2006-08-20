package de.berlios.sventon.cache;

import de.berlios.sventon.repository.cache.CacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles EntryCache instances.
 *
 * @author jesper@users.berlios.de
 */
public class ObjectCacheManager {

  /**
   * Logger for this class and subclasses
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * The cache instances.
   */
  final Map<String, ObjectCache> caches = new HashMap<String, ObjectCache>();

  /**
   * Root directory for cache files.
   */
  private final String rootDirectory;
  private final int maxElementsInMemory;
  private final boolean overflowToDisk;
  private final boolean eternal;
  private final int timeToLiveSeconds;
  private final int timeToIdleSeconds;
  private final boolean diskPersistent;
  private final int diskExpiryThreadIntervalSeconds;

  /**
   * Constructor.
   *
   * @param rootDirectory       Root directory for cache files.
   * @param maxElementsInMemory Max elements in memory
   * @param overflowToDisk      Overflow to disk
   * @param eternal             If true, objects never expire
   * @param timeToLiveSeconds   Object time to live in seconds
   * @param timeToIdleSeconds   Object time to idle in seconds
   * @param diskPersistent      If true, cache will be stored on disk
   * @param diskExpiryThreadIntervalSeconds
   *                            Expiry thread interval
   */
  public ObjectCacheManager(final String rootDirectory,
                            final int maxElementsInMemory,
                            final boolean overflowToDisk,
                            final boolean eternal,
                            final int timeToLiveSeconds,
                            final int timeToIdleSeconds,
                            final boolean diskPersistent,
                            final int diskExpiryThreadIntervalSeconds) {
    this.rootDirectory = rootDirectory;
    this.maxElementsInMemory = maxElementsInMemory;
    this.overflowToDisk = overflowToDisk;
    this.eternal = eternal;
    this.timeToLiveSeconds = timeToLiveSeconds;
    this.timeToIdleSeconds = timeToIdleSeconds;
    this.diskPersistent = diskPersistent;
    this.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds;
  }

  /**
   * Gets a cache by name. If cache does not exist yet, it will be created
   * using the default settings.
   *
   * @param cacheName Name of cache to get
   * @return The cache instance.
   */
  public ObjectCache getCache(final String cacheName) throws CacheException {
    logger.debug("Getting cache: " + cacheName);
    ObjectCache cache = caches.get(cacheName);
    if (cache == null) {
      cache = addCache(cacheName, createCache(cacheName));
    }
    return cache;
  }

  /**
   * Creates a new cache instance using given name and default settings.
   *
   * @param cacheName Name of cache instance.
   * @return The created cache instance.
   * @throws de.berlios.sventon.repository.cache.CacheException
   *          if unable to create cache.
   */
  private ObjectCache createCache(final String cacheName) throws CacheException {
    logger.debug("Creating cache: " + cacheName);
    return new ObjectCacheImpl(
        cacheName,
        maxElementsInMemory,
        overflowToDisk,
        eternal,
        timeToLiveSeconds,
        timeToIdleSeconds,
        diskPersistent,
        diskExpiryThreadIntervalSeconds);
  }

  /**
   * For test purposes only.
   * Adds a cache instance to the manager's list.
   *
   * @param cacheName  Name of cache
   * @param objectCache Cache instance
   * @return The added cache instance
   */
  public ObjectCache addCache(final String cacheName, final ObjectCache objectCache) {
    return caches.put(cacheName, objectCache);
  }


  /**
   * Gets all cache instances.
   *
   * @return Cache instances.
   */
  public Map<String, ObjectCache> getCaches() {
    return Collections.unmodifiableMap(caches);
  }

  /**
   * Shuts all the caches down.
   *
   * @throws CacheException if unable to shutdown caches.
   */
  public void shutdown() throws CacheException {
    for (final ObjectCache cache : caches.values()) {
      cache.shutdown();
    }
  }
}
