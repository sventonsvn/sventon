/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.export;

import org.sventon.appl.ConfigDirectory;
import org.sventon.model.RepositoryName;

import java.io.File;
import java.nio.charset.Charset;

/**
 * ExportDirectoryFactory default implementation.
 *
 * @author jesper@sventon.org
 */
public class ExportDirectoryFactoryImpl implements ExportDirectoryFactory {

  /**
   * Fallback character set, default set to ISO-8859-1.
   */
  public static final String FALLBACK_CHARSET = "ISO-8859-1";

  /**
   * The charset to use for filenames and comments in compressed archive file.
   */
  private Charset archiveFileCharset = Charset.forName(FALLBACK_CHARSET);

  /**
   * Export root directory.
   */
  private final File exportRootDirectory;

  /**
   * Constructor.
   *
   * @param configDirectory The configuration directory. The export directory will be extracted.
   */
  public ExportDirectoryFactoryImpl(final ConfigDirectory configDirectory) {
    this.exportRootDirectory = configDirectory.getExportDirectory();
  }

  /**
   * {@inheritDoc}
   */
  public ExportDirectory create(final RepositoryName repositoryName) {
    final ExportDirectoryImpl exportDirectory = new ExportDirectoryImpl(repositoryName,
        exportRootDirectory, archiveFileCharset);
    exportDirectory.getDirectory().mkdirs();
    return exportDirectory;
  }

  /**
   * Sets the archive file charset to use for filenames and comments.
   * <p/>
   * If given charset does not exist, <code>iso-8859-1</code> will be used as a fallback.
   *
   * @param archiveFileCharset Charset.
   * @see #FALLBACK_CHARSET
   */
  public void setArchiveFileCharset(final String archiveFileCharset) {
    try {
      this.archiveFileCharset = Charset.forName(archiveFileCharset);
    } catch (IllegalArgumentException iae) {
      this.archiveFileCharset = Charset.forName(FALLBACK_CHARSET);
    }
  }

  /**
   * Gets the archive file charset.
   *
   * @return Charset.
   */
  protected Charset getArchiveFileCharset() {
    return archiveFileCharset;
  }
}
