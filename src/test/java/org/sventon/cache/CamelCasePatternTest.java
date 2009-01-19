package org.sventon.cache;

import junit.framework.TestCase;

public class CamelCasePatternTest extends TestCase {

  public void testCamelCasePattern() throws Exception {

    assertTrue("/ATestFile".matches("/" + new CamelCasePattern("A").getPattern()));
    assertTrue("/ATestFile".matches("/" + new CamelCasePattern("AT").getPattern()));

    assertTrue("/TestFile".matches("/" + new CamelCasePattern("TF").getPattern()));
    assertTrue("/TestFile1".matches("/" + new CamelCasePattern("TF").getPattern()));
    assertTrue("/TestFile1".matches("/" + new CamelCasePattern("TF1").getPattern()));

    assertTrue("/TestFileNumberTwo".matches("/" + new CamelCasePattern("TF").getPattern()));
    assertTrue("/TestFileNumberTwo".matches("/" + new CamelCasePattern("TFNT").getPattern()));
    assertFalse("/TestFileNumberTwo".matches("/" + new CamelCasePattern("TFT").getPattern()));

    assertTrue("/TestFileABC".matches("/" + new CamelCasePattern("TF").getPattern()));
    assertTrue("/TestFileABC".matches("/" + new CamelCasePattern("TFA").getPattern()));
    assertTrue("/TestFileABC".matches("/" + new CamelCasePattern("TFABC").getPattern()));

    assertTrue("/TestFile1ABC".matches("/" + new CamelCasePattern("TFA").getPattern()));
    assertTrue("/TestFile1ABC".matches("/" + new CamelCasePattern("TFABC").getPattern()));

    assertTrue("/HTML".matches("/" + new CamelCasePattern("HT").getPattern()));
    assertTrue("/HTML".matches("/" + new CamelCasePattern("HTML").getPattern()));

    assertTrue("/trunk/TestFile".matches("/trunk/" + new CamelCasePattern("TF").getPattern()));
    assertTrue("/trunk/dev/AnotherTestFile".matches("/trunk/dev/" + new CamelCasePattern("ATF").getPattern()));
  }
}