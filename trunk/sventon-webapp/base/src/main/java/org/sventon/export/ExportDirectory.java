/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.export;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Represents a temporary export directory.
 *
 * @author jesper@sventon.org
 */
public interface ExportDirectory {

  /**
   * Compresses the temporary export directory.
   *
   * @return The <code>File</code> instance of the compressed file.
   * @throws IOException if IO error occurs.
   */
  File compress() throws IOException;

  /**
   * Gets the export directory.
   *
   * @return The directory.
   */
  File getDirectory();

  /**
   * Deletes the temporary export directory.
   *
   * @throws IOException if deletion was unsuccessful.
   */
  void delete() throws IOException;

  /**
   * Gets the UUID for this export directory.
   *
   * @return UUID
   */
  UUID getUUID();

  /**
   * Creates the export directory.
   *
   * @return True if successfully created, false if not.
   */
  boolean mkdirs();
}
