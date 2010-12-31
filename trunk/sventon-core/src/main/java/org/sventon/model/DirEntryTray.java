/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class representing the user's entry tray.
 * The entry tray will be stored on the user's HTTPSession.
 *
 * @author jesper@sventon.org
 */
public final class DirEntryTray implements Serializable {

  private static final long serialVersionUID = -7140458501231816209L;

  /**
   * List of entries in tray.
   */
  private final Set<PeggedDirEntry> entries = new TreeSet<PeggedDirEntry>(new PeggedDirEntryComparator());


  /**
   * Gets the number of entries in the tray.
   *
   * @return Number of entries.
   */
  public int getSize() {
    return entries.size();
  }

  /**
   * Adds an entry to the user's tray.
   *
   * @param entry Entry to add.
   * @return True if added.
   */
  public boolean add(final PeggedDirEntry entry) {
    return entries.add(entry);
  }

  /**
   * Removes an entry from the user's tray.
   *
   * @param entry Entry to remove
   * @return True if removed.
   */
  public boolean remove(final PeggedDirEntry entry) {
    return entries.remove(entry);
  }

  /**
   * Gets an unmodifiable list of the entries in the tray.
   *
   * @return Entries
   */
  public Set<PeggedDirEntry> getUnmodifiableEntries() {
    return Collections.unmodifiableSet(entries);
  }

  /**
   * Removes all entries from the tray.
   */
  public void removeAll() {
    entries.clear();
  }

  /**
   * Comparator for pegged entries.
   */
  private static class PeggedDirEntryComparator implements Comparator<PeggedDirEntry>, Serializable {

    private static final long serialVersionUID = 4597643015545681872L;

    private final DirEntryComparator comparator =
        new DirEntryComparator(DirEntryComparator.SortType.NAME, true);

    public int compare(final PeggedDirEntry o1, final PeggedDirEntry o2) {
      return comparator.compare(o1.getEntry(), o2.getEntry());
    }
  }
}
