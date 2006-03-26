package de.berlios.sventon.cache;

/**
 * Interface to be implemented by sventon object cache class.
 */
public interface SventonCache {

  /**
   * Puts an object into the cache.
   *
   * @param cacheKey The cache cacheKey.
   * @param value    The object to cache.
   * @throws IllegalArgumentException if cacheKey is null.
   */
  public void put(final Object cacheKey, final Object value);

  /**
   * Gets an object from the cache.
   *
   * @param cacheKey The key to the object to get.
   * @return The cached object. <code>null</code> if cache miss.
   * @throws IllegalArgumentException if cacheKey is null.
   */
  public Object get(final Object cacheKey);

  /**
   * Gets the cache hit count.
   *
   * @return The cache hit count.
   */
  public long getHitCount();

  /**
   * Gets the cache miss count.
   *
   * @return The cache miss count.
   */
  public long getMissCount();

}
