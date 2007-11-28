/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository;

import java.util.List;
import java.util.Comparator;
import java.util.Collections;

/**
 * Sorts collections of <code>RepositoryEntry</code> instances.
 *
 * @author jesper@users.berlios.de
 */
public class RepositoryEntrySorter {

  public enum SortMode {
    ASC, DESC
  }

  /**
   * Field to sort on.
   */
  private final RepositoryEntryComparator.SortType sortType;

  /**
   * The sort mode, ascending or descending.
   */
  private final SortMode sortMode;

  /**
   * Constructor.
   *
   * @param sortType   Field to sort on.
   * @param sortMode Sort mode, ascending or descending.
   * @throws IllegalArgumentException if sortBy is null
   */
  public RepositoryEntrySorter(final RepositoryEntryComparator.SortType sortType, final SortMode sortMode) {
    if (sortType == null) {
      throw new IllegalArgumentException("sortType cannot be null");
    }
    if (sortMode == null) {
      throw new IllegalArgumentException("sortMode cannot be null");
    }

    this.sortType = sortType;
    this.sortMode = sortMode;
  }

  /**
   * Sorts given list.
   *
   * @param entries Entries to sort.
   */
  public void sort(final List<RepositoryEntry> entries) {
    final Comparator<RepositoryEntry> comparator;
    if (sortMode == SortMode.DESC) {
      comparator = Collections.reverseOrder(new RepositoryEntryComparator(sortType, true));
    } else {
      comparator = new RepositoryEntryComparator(sortType, true);
    }
    Collections.sort(entries, comparator);
  }

}
