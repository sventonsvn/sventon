package de.berlios.sventon.util;

import junit.framework.TestCase;

public class EncodingUtilsTest extends TestCase {

  public void testEncode() throws Exception {
    assertEquals("%2F", EncodingUtils.encode("/"));
    assertEquals("+", EncodingUtils.encode(" "));
  }

  public void testEncodeUrl() throws Exception {
    assertEquals("http://localhost/svn/repobrowser.svn?path=/trunk&revision=HEAD&name=test",
        EncodingUtils.encodeUrl("http://localhost/svn/repobrowser.svn?path=%2ftrunk&revision=HEAD&name=test"));

    assertEquals("http://localhost/svn/showfile.svn?path=/trunk/public/%c3%a5%c3%a4%c3%b6.txt&revision=HEAD&name=test",
        EncodingUtils.encodeUrl("http://localhost/svn/showfile.svn?path=%2ftrunk%2fpublic%2f%c3%a5%c3%a4%c3%b6.txt&revision=HEAD&name=test"));

    assertEquals(";", EncodingUtils.encodeUrl("%3b"));
  }

}