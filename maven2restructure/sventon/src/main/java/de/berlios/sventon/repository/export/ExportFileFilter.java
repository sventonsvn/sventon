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
package de.berlios.sventon.repository.export;

import de.berlios.sventon.appl.Instance;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filter to match temporary export files using the name format <code>sventon-[millis].zip</code>.
 */
public final class ExportFileFilter implements FilenameFilter {

  /**
   * {@inheritDoc}
   */
  public boolean accept(final File dir, final String name) {
    return name.matches(Instance.INSTANCE_NAME_PATTERN + "-[0-9]{17}\\.zip");
  }

}
