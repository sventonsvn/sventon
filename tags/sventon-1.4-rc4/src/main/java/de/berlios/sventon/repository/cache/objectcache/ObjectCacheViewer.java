package de.berlios.sventon.repository.cache.objectcache;

import java.io.File;
import java.util.List;

/**
 * Simple viewer utility for the sventon object cache.
 *
 * @author jesper@users.berlios.de
 */
public final class ObjectCacheViewer {

  /**
   * Private.
   */
  private ObjectCacheViewer() {
  }

  public static void main(String[] args) throws Exception {
    System.out.println("Sventon object cache viewer");
    if (args.length == 0) {
      System.out.println("Syntax: ObjectCacheViewer [cache name] [cache root directory]");
      return;
    }

    final String cacheFile = args[1] + File.separator + args[0];
    System.out.println("Viewing cache file: " + cacheFile);

    final ObjectCacheImpl cache = new ObjectCacheImpl(args[0], cacheFile, 1000, true, true, 0, 0, true, 120);
    final List<Object> keys = cache.getKeys();

    System.out.println("Number of cached entries: " + keys.size());
    System.out.println("--------------------------------------------------------");

    for (Object key : keys) {
      System.out.print("Key [" + key + "]:\n" + cache.get(key));
      System.out.println("\n--------------------------------------------------------");
    }

    System.out.println("==============");

    for (int i = 1; i < 2000; i++) {
      String key = "svnRevision-" + String.valueOf(i);
      Object o = cache.get(key);
      if (o == null) {
        throw new IllegalStateException();
      }
      System.out.print("Key [" + key + "]:\n" + o);
      System.out.println("\n--------------------------------------------------------");
    }

    System.out.println("Done.");



  }
}
