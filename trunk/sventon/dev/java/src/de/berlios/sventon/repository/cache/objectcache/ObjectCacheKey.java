package de.berlios.sventon.repository.cache.objectcache;

/**
 * Cachekey class.
 *
 * @author jesper@users.berlios.de
 */
public class ObjectCacheKey {

  private final String path;
  private final String checksum;

  /**
   * Constructor.
   *
   * @param checksum Subversion entry checksum
   * @param path     Path
   */
  public ObjectCacheKey(final String path, final String checksum) {
    this.path = path;
    this.checksum = checksum;
  }

  public String toString() {
    return "ObjectCacheKey{" +
        "path='" + path + '\'' +
        ", checksum='" + checksum + '\'' +
        '}';
  }
}
