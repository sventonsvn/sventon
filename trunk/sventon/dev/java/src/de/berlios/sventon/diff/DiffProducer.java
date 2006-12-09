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
package de.berlios.sventon.diff;

import de.regnis.q.sequence.line.diff.*;

import java.io.*;
import java.util.Map;
import java.util.HashMap;

/**
 * Class for producing normal or unified diff results.
 *
 * @author jesper@users.berlios.de
 */
public final class DiffProducer {

  /**
   * Left (old) input.
   */
  private InputStream left;

  /**
   * Right (new) input.
   */
  private InputStream right;

  /**
   * Character encoding.
   */
  private final String encoding;

  /**
   * Constructor.
   *
   * @param left     The left (old) InputStream.
   * @param right    The right (new) InputStream.
   * @param encoding Encoding to use.
   */
  public DiffProducer(final InputStream left, final InputStream right, final String encoding) {
    this.left = left;
    this.right = right;
    this.encoding = encoding;
  }

  /**
   * Performs a normal diff of given left and right.
   *
   * @param output Result output
   * @throws IOException if IO error occurs.
   */
  public void doNormalDiff(final OutputStream output) throws IOException {
    QDiffNormalGenerator.setup();
    final Map<String, String> generatorProperties = new HashMap<String, String>();
    final QDiffGenerator generator = QDiffManager.getDiffGenerator(QDiffNormalGenerator.TYPE, generatorProperties);
    final Writer writer = new OutputStreamWriter(output);
    QDiffManager.generateTextDiff(this.left, this.right, this.encoding, writer, generator);
    writer.flush();
    writer.close();
  }

  /**
   * Performs a unified diff of given left and right.
   *
   * @param output Result output
   * @throws IOException if IO error occurs.
   */
  public void doUniDiff(final OutputStream output) throws IOException {
    QDiffUniGenerator.setup();
    final Map<String, String> generatorProperties = new HashMap<String, String>();
    final QDiffGenerator generator = QDiffManager.getDiffGenerator(QDiffUniGenerator.TYPE, generatorProperties);
    final Writer writer = new OutputStreamWriter(output);
    QDiffManager.generateTextDiff(left, right, encoding, writer, generator);
    writer.flush();
    writer.close();
  }
}
