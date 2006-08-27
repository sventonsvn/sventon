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
package de.berlios.sventon.util;

import javax.servlet.ServletOutputStream;
import java.io.*;

/**
 * File and directory utility class.
 *
 * @author jesper@users.berlios.de
 */
public final class FileUtils {


  /**
   * Reads the given input stream and writes the buffered contents to the output.
   *
   * @param input  Input (source) stream
   * @param output Output (destination) stream
   * @throws IOException If unable to write to output stream.
   */
  public static void writeStream(final InputStream input, final ServletOutputStream output) throws IOException {
    byte[] buffer = new byte[8192];
    int bytesRead;
    while ((bytesRead = input.read(buffer)) >= 0) {
      output.write(buffer, 0, bytesRead);
    }
  }

  /**
   * Closes given <code>Closeable</code> instance.
   * <b>Exceptions will be ignored.</b>
   *
   * @param closable
   */
  public static void close(final Closeable closable) {
    if (closable != null) {
      try {
        closable.close();
      } catch (IOException e) {
        // ignore
      }
    }
  }
}
