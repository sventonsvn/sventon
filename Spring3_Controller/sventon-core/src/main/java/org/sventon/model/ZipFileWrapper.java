/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import de.schlichtherle.io.ArchiveDetector;
import de.schlichtherle.io.DefaultArchiveDetector;
import de.schlichtherle.io.archive.spi.ArchiveDriver;
import de.schlichtherle.io.archive.zip.Zip32Driver;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A compressed ZIP file.
 * <p/>
 * Uses the <a href="https://truezip.dev.java.net/">TrueZip</a> library.
 *
 * @author jesper@sventon.org
 */
public final class ZipFileWrapper {

  private File zipFile;
  private byte[] zippedData;

  /**
   * Constructor.
   *
   * @param zipFile Destination ZIP file.
   * @param charset Charset to use for file names and comments.
   */
  public ZipFileWrapper(final File zipFile, final Charset charset) {
    Validate.notNull(charset, "Charset was null");
    Validate.notNull(zipFile, "ZipFile was null");
    this.zipFile = zipFile;

    final ArchiveDriver driver = new Zip32Driver(charset.name());
    de.schlichtherle.io.File.setDefaultArchiveDetector(
        new DefaultArchiveDetector(ArchiveDetector.DEFAULT, "zip", driver));
  }

  /**
   * Constructor.
   *
   * @param zippedData The zipped data.
   */
  public ZipFileWrapper(final byte[] zippedData) {
    this.zippedData = zippedData;
  }

  /**
   * Zips the given directory recursively.
   *
   * @param directory The directory to zip.
   * @throws java.io.IOException if IO error occurs.
   */
  public void add(final File directory) throws IOException {
    synchronized (ZipFileWrapper.class) {
      final de.schlichtherle.io.File destinationFile = new de.schlichtherle.io.File(zipFile);
      final de.schlichtherle.io.File dirToZip = new de.schlichtherle.io.File(directory);
      destinationFile.archiveCopyAllFrom(dirToZip, ArchiveDetector.NULL);
      de.schlichtherle.io.File.umount();
    }
  }


  /**
   * Extracts given file from a zipped input stream.
   *
   * @param filename Filename to extract
   * @return The extracted file.
   * @throws IOException if unable to extract file.
   */
  public byte[] extractFile(final String filename) throws IOException {
    final ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zippedData));
    byte[] extractedFile = null;
    ZipEntry zipEntry;

    while ((zipEntry = zipInputStream.getNextEntry()) != null) {
      if (zipEntry.getName().equals(filename)) {
        extractedFile = IOUtils.toByteArray(zipInputStream);
        break;
      }
    }
    IOUtils.closeQuietly(zipInputStream);
    return extractedFile;
  }

  /**
   * Gets the ZIP file.
   *
   * @return ZIP file.
   */
  public File getZipFile() {
    return zipFile;
  }
}
