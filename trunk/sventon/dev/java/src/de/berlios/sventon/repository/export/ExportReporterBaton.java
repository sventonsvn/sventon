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

import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.ISVNReporter;
import org.tmatesoft.svn.core.SVNException;

/**
 * ReporterBaton implementation that always reports 'empty wc' state.
 *
 * @author jesper@users.berlios.de
 */
public class ExportReporterBaton implements ISVNReporterBaton {

  /**
   * The revision to export.
   */
  private final long exportRevision;

  /**
   * Constructor.
   *
   * @param exportRevision Revision to export
   */
  public ExportReporterBaton(final long exportRevision) {
    this.exportRevision = exportRevision;
  }

  /**
   * {@inheritDoc}
   */
  public void report(final ISVNReporter reporter) throws SVNException {
    reporter.setPath("", null, exportRevision, true);
    reporter.finishReport();
  }
}
