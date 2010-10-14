/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.cache.objectcache;

import java.io.File;
import java.util.List;

/**
 * Simple viewer utility for the sventon object cache.
 *
 * @author jesper@sventon.org
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
