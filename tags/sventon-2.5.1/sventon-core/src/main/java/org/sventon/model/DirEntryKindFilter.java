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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DirEntryKindFilter filter.
 * Filters out given entry kind and removes entries with non-matching kinds.
 *
 * @author jesper@sventon.org
 */
public final class DirEntryKindFilter {

  /**
   * The kind to filter.
   */
  private DirEntry.Kind kind;

  /**
   * Constructor.
   *
   * @param kind Entry kind to filter (i.e. keep).
   */
  public DirEntryKindFilter(final DirEntry.Kind kind) {
    if (kind == null
        || DirEntry.Kind.ANY == kind
        || DirEntry.Kind.UNKNOWN == kind
        || DirEntry.Kind.NONE == kind) {
      throw new IllegalArgumentException("Illegal kind: " + kind);
    }
    this.kind = kind;
  }

  /**
   * Filter a given list of entries.
   *
   * @param entries List of entries to filter.
   * @return List of entries matching given kind.
   */
  public List<DirEntry> filter(final List<DirEntry> entries) {
    final List<DirEntry> list = Collections.checkedList(new ArrayList<DirEntry>(), DirEntry.class);

    for (final DirEntry entry : entries) {
      if (kind == entry.getKind()) {
        list.add(entry);
      }
    }
    return list;
  }
}
