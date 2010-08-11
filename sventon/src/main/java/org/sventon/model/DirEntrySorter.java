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
package org.sventon.model;

import org.apache.commons.lang.Validate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts collections of <code>RepositoryEntry</code> instances.
 *
 * @author jesper@sventon.org
 */
public final class DirEntrySorter {

  /**
   * Available sort modes.
   */
  public enum SortMode {

    /**
     * Ascending sort.
     */
    ASC,

    /**
     * Descending sort.
     */
    DESC
  }

  /**
   * Field to sort on.
   */
  private final DirEntryComparator.SortType sortType;

  /**
   * The sort mode, ascending or descending.
   */
  private final SortMode sortMode;

  /**
   * Constructor.
   *
   * @param sortType Field to sort on.
   * @param sortMode Sort mode, ascending or descending.
   */
  public DirEntrySorter(final DirEntryComparator.SortType sortType, final SortMode sortMode) {
    Validate.notNull(sortType, "sortType cannot be null");
    Validate.notNull(sortMode, "sortMode cannot be null");
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
      comparator = Collections.reverseOrder(new DirEntryComparator(sortType, true));
    } else {
      comparator = new DirEntryComparator(sortType, true);
    }
    Collections.sort(entries, comparator);
  }

}
