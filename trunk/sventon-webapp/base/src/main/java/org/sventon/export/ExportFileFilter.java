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
import java.io.FilenameFilter;

import static org.sventon.export.DefaultExportDirectory.FILE_NAME_PATTERN;

/**
 * Filter to match temporary export files using the name format <code>sventon-[millis].zip</code>.
 */
public final class ExportFileFilter implements FilenameFilter {

  @Override
  public boolean accept(final File dir, final String name) {
    return FILE_NAME_PATTERN.matcher(name).matches();
  }

}
