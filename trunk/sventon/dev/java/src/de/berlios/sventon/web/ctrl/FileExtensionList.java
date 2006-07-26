/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.util.PathUtil;
import org.tmatesoft.svn.core.SVNDirEntry;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * Creates a set of file extensions existing in given collection of entries.
 *
 * @author jesper@users.berlios.de
 */
public class FileExtensionList {

  /**
   * The Set of existing extensions.
   */
  final Set<String> existingExtensions = new TreeSet<String>();

  /**
   * Constructor.
   * Iterates over the list of entries and parses the file extensions.
   * Blank (non-existing) extensions will be ignored, i.e. the set will
   * not in any case contain an empty-string entry.
   *
   * @param entries Collection of entries.
   */
  public FileExtensionList(final Collection<SVNDirEntry> entries) {
    for (final SVNDirEntry entry : entries) {
      final String fileExtension = PathUtil.getFileExtension(entry.getName()).toLowerCase();
      if (!"".equals(fileExtension)) {
        existingExtensions.add(fileExtension);
      }
    }
  }

  /**
   * Gets a set of file extensions existing in given collection of entries.
   *
   * @return Set of extensions.
   */
  public Set<String> getExtensions() {
    return existingExtensions;
  }
}
