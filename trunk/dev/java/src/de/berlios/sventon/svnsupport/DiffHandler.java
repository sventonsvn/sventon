package de.berlios.sventon.svnsupport;

import de.regnis.q.sequence.line.diff.*;

import java.io.*;
import java.util.Map;
import java.util.HashMap;

/**
 * DiffHandler class for doing normal or unified diffing operations.
 *
 * @author jesper@users.berlios.de
 */
public class DiffHandler {

  /**
   * Left (old) input.
   */
  private InputStream left;

  /**
   * Right (new) input.
   */
  private InputStream right;

  /**
   * Character encoding. Default set to UTF-8.
   */
  private String encoding = "UTF-8";

  /**
   * Constructor.
   *
   * @param left The left (old) InputStream.
   * @param right The right (new) InputStream.
   * @param encoding Encoding to use. If <code>null</code>, UTF-8 will be used.
   */
  public DiffHandler(final InputStream left, final InputStream right, final String encoding) {
    this.left = left;
    this.right = right;
    if (encoding != null) {
      this.encoding = encoding;
    }
  }

  /**
   * Performs a normal diff of given left and right.
   *
   * @param output Result output
   * @throws IOException if IO error occurs.
   */
  public void doNormalDiff(final OutputStream output) throws IOException {
    QDiffNormalGenerator.setup();
    Map generatorProperties = new HashMap();
    generatorProperties.put(QDiffGeneratorFactory.COMPARE_EOL_PROPERTY, Boolean.TRUE.toString());
    QDiffGenerator generator = QDiffManager.getDiffGenerator(QDiffNormalGenerator.TYPE, generatorProperties);
    Writer writer = new OutputStreamWriter(output);
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
    Map generatorProperties = new HashMap();
    generatorProperties.put(QDiffGeneratorFactory.COMPARE_EOL_PROPERTY, Boolean.TRUE.toString());
    QDiffGenerator generator = QDiffManager.getDiffGenerator(QDiffUniGenerator.TYPE, generatorProperties);
    Writer writer = new OutputStreamWriter(output);
    QDiffManager.generateTextDiff(left, right, encoding, writer, generator);
    writer.flush();
    writer.close();
  }
}
