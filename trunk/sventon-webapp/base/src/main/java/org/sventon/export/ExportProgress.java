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
package org.sventon.export;

/**
 * Defines the different export progress phases.
 *
 * @author jesper@sventon.org
 */
public enum ExportProgress {

  EXPORTING(33),
  COMPRESSING(66),
  DONE(100);

  private int progress;

  private ExportProgress(final int percentOfTotalProgress) {
    this.progress = percentOfTotalProgress;
  }

  public int getProgress() {
    return progress;
  }

}
