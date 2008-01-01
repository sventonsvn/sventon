/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository.cache.entrycache;

import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.RepositoryEntryComparator;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * Simple viewer utility for the sventon entry cache file.
 *
 * @author jesper@users.berlios.de
 */
public final class EntryCacheViewer {

  /**
   * Private.
   */
  private EntryCacheViewer() {
  }

  public static void main(String[] args) throws Exception {

    System.out.println("Sventon entry cache viewer");
    if (args.length == 0) {
      System.out.println("Syntax: EntryCacheViewer [cache file]");
      return;
    }

    System.out.println("Viewing cache file: " + args[0]);

    final ObjectInputStream inputStream = new ObjectInputStream(new GZIPInputStream(new FileInputStream(args[0])));
    final long cachedRevision = inputStream.readLong();

    //noinspection unchecked
    final Set<RepositoryEntry> entries = (Set<RepositoryEntry>) inputStream.readObject();

    System.out.println("Number of cached entries: " + entries.size());
    System.out.println("Cached revision: " + cachedRevision);
    System.out.println("--------------------------------------------------------");

    Collections.sort(new ArrayList<RepositoryEntry>(entries), new RepositoryEntryComparator(RepositoryEntryComparator.SortType.FULL_NAME, false));

    for (RepositoryEntry entry : entries) {
      System.out.println(entry);
    }
    System.out.println("--------------------------------------------------------");
    System.out.println("Done.");

  }
}
