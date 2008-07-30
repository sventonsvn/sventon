/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.cache.entrycache;

import org.sventon.model.RepositoryEntry;
import org.sventon.util.RepositoryEntryComparator;

import java.util.Collections;
import java.util.TreeSet;

/**
 * Memory persistend repository entry cache.
 * Contains a cached set of the repository entries for a specific revision and URL.
 *
 * @author jesper@sventon.org
 */
public final class MemoryCache extends EntryCache {

  /**
   * Constructor.
   * Initializes the memory cache.
   */
  public MemoryCache() {
    logger.info("Initializing Memorycache");
    setEntries(Collections.checkedSet(new TreeSet<RepositoryEntry>(
        new RepositoryEntryComparator(RepositoryEntryComparator.SortType.FULL_NAME, false)),
        RepositoryEntry.class));
  }

  /**
   * {@inheritDoc}
   */
  public void shutdown() {
    logger.info("Shutting down");
    // Nothing to do.
  }

  /**
   * {@inheritDoc}
   */
  public void flush() {
    // Nothing to do.
  }
}
