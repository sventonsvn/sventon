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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Represents an archive file.
 *
 * @author jesper@sventon.org
 */
public final class ArchiveFile {

  /**
   * The archive entries.
   */
  private final List<ZipEntry> archiveEntries = new ArrayList<ZipEntry>();

  /**
   * Constructor.
   *
   * @param content The file contents.
   * @throws IOException if IO error.
   */
  public ArchiveFile(final byte[] content) throws IOException {
    final ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(content));

    ZipEntry zipEntry;
    while ((zipEntry = zip.getNextEntry()) != null) {
      archiveEntries.add(zipEntry);
    }
  }

  /**
   * Gets the archive entries (unmodifiable).
   *
   * @return Entries.
   */
  public List<ZipEntry> getEntries() {
    return Collections.unmodifiableList(archiveEntries);
  }

}
