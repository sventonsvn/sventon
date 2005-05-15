package de.berlios.sventon.colorizer;

import java.io.LineNumberReader;
import java.io.Reader;
import java.io.IOException;

/**
 * Stream colorizer.
 *
 * @author jesper@users.berlios.de
 */
public class Colorizer extends LineNumberReader {

  /** The source file format specific formatter instance. */
  private Formatter formatter;

  /**
   * Create a new line-numbering reader, using the default input-buffer
   * size.
   *
   * @param in a Reader object to provide the underlying stream.
   */
  public Colorizer(final Reader in) {
    super(in);
    init();
  }

  /**
   * Create a new line-numbering reader, reading characters into a buffer of
   * the given size.
   *
   * @param in a Reader object to provide the underlying stream.
   * @param size an int specifying the size of the buffer.
   */
  public Colorizer(Reader in, int size) {
    super(in, size);
    init();
  }

  /**
   * Initializes the colorizer and determines
   * how to handle the input.
   */
  private void init() {
    formatter = new JavaFormatter();
  }

  /**
   * Reads a line from the stream and returns it colorized.
   * @return The <code>HTML</code> colorized String.
   * @throws IOException
   */
  @Override
  public String readLine() throws IOException {
    String line = super.readLine();
    if (line != null) {
      return formatter.format(line);
    } else {
      return null;
    }
  }

  /**
   * Marking is not supported.
   * @return Always <code>false</code>.
   */
  @Override
  public boolean markSupported() {
    return false;
  }

  /**
   * Not implemented. Will throw UnsupportedOperationException.
   * @throws UnsupportedOperationException as method not implemented.
   */
  @Override
  public void mark(int readAheadLimit) {
    throw new UnsupportedOperationException();
  }

  /**
   * Not implemented. Will throw UnsupportedOperationException.
   * @throws UnsupportedOperationException as method not implemented.
   */
  @Override
  public int read() {
    throw new UnsupportedOperationException();
  }

  /**
   * Not implemented. Will throw UnsupportedOperationException.
   * @throws UnsupportedOperationException as method not implemented.
   */
  @Override
  public int read(char[] cbuf, int off, int len) {
    throw new UnsupportedOperationException();
  }

  /**
   * Not implemented. Will throw UnsupportedOperationException.
   * @throws UnsupportedOperationException as method not implemented.
   */
  @Override
  public void reset() {
    throw new UnsupportedOperationException();
  }

  /**
   * Not implemented. Will throw UnsupportedOperationException.
   * @throws UnsupportedOperationException as method not implemented.
   */
  @Override
  public long skip(long n) {
    throw new UnsupportedOperationException();
  }

}