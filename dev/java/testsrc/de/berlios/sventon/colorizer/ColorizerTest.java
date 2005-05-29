package de.berlios.sventon.colorizer;

import junit.framework.*;
import de.berlios.sventon.colorizer.Colorizer;

import java.io.StringReader;

public class ColorizerTest extends TestCase {

  public void testUnsupportedOperations() throws Exception {
    StringReader input = new StringReader("Test!");
    Colorizer colorizer = new Colorizer(input);
    assertFalse(colorizer.markSupported());
    try {
      colorizer.read();
      fail("Should cause UnsupportedOperationException");
    } catch (UnsupportedOperationException u) {/* Expected */}
    try {
      colorizer.mark(1);
      fail("Should cause UnsupportedOperationException");
    } catch (UnsupportedOperationException u) {/* Expected */}
    try {
      char[] cbuf = null;
      colorizer.read(cbuf, 0, 10);
      fail("Should cause UnsupportedOperationException");
    } catch (UnsupportedOperationException u) {/* Expected */}
    try {
      colorizer.reset();
      fail("Should cause UnsupportedOperationException");
    } catch (UnsupportedOperationException u) {/* Expected */}
    try {
      colorizer.skip(100L);
      fail("Should cause UnsupportedOperationException");
    } catch (UnsupportedOperationException u) {/* Expected */}
  }

}