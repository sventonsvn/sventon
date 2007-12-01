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
package de.berlios.sventon.web.model;

import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.RepositoryEntryComparator;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class representing the user's entry tray.
 * The entry tray will be stored on the user's HTTPSession.
 *
 * @author jesper@users.berlios.de
 */
public final class RepositoryEntryTray implements Serializable {

  /**
   * List of entries in tray.
   */
  private final Set<RepositoryEntry> entries = new TreeSet<RepositoryEntry>(
      new RepositoryEntryComparator(RepositoryEntryComparator.SortType.NAME, true));

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
  public boolean add(final RepositoryEntry entry) {
    return entries.add(entry);
  }

  /**
   * Removs an entry from the user's tray.
   *
   * @param entry Entry to remove
   * @return True if removed.
   */
  public boolean remove(final RepositoryEntry entry) {
    return entries.remove(entry);
  }

  /**
   * Gets an unmodifiable list of the entries in the tray.
   *
   * @return Entries
   */
  public Set<RepositoryEntry> getUnmodifiableEntries() {
    return Collections.unmodifiableSet(entries);
  }

}
