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
package de.berlios.sventon.repository.export;

import java.io.FilenameFilter;
import java.io.File;

/**
 * Filter to match temporary export files using the name format <code>sventon-[millis].zip</code>.
 */
public class ExportFileFilter implements FilenameFilter {

  public boolean accept(final File dir, final String name) {
    return name.matches("[a-z0-9]+-[0-9]{17}\\.zip");
  }

}
