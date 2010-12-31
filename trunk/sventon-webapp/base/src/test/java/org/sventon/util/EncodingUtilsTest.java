/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EncodingUtilsTest {

  @Test
  public void testEncode() throws Exception {
    assertEquals("%2F", EncodingUtils.encode("/"));
    assertEquals("%2B", EncodingUtils.encode("+"));
    assertEquals("%23", EncodingUtils.encode("#"));
    assertEquals("+", EncodingUtils.encode(" "));
  }

  @Test
  public void testEncodeUrl() throws Exception {
    assertEquals("/test/%C3%BC.txt", EncodingUtils.encodeUrl("/test/\u00fc.txt"));
    assertEquals("/%C3%A5%C3%A4%C3%B6.txt", EncodingUtils.encodeUrl("/\u00e5\u00e4\u00f6.txt"));
    assertEquals("/%2B/test.txt", EncodingUtils.encodeUrl("/+/test.txt"));
    assertEquals("/%23/test.txt", EncodingUtils.encodeUrl("/#/test.txt"));
    assertEquals("+", EncodingUtils.encodeUrl(" "));
    assertEquals("", EncodingUtils.encodeUrl(""));

    try {
      EncodingUtils.encodeUrl(null);
      fail("Should cause NPE");
    } catch (Exception e) {
      // expected
    }
  }

  @Test
  public void testEncodeUri() throws Exception {
    assertEquals("/test/%C3%BC.txt", EncodingUtils.encodeUri("/test/\u00fc.txt"));
    assertEquals("/%C3%A5%C3%A4%C3%B6.txt", EncodingUtils.encodeUri("/\u00e5\u00e4\u00f6.txt"));
    assertEquals("/+/test.txt", EncodingUtils.encodeUri("/+/test.txt"));
    assertEquals("/%23/test.txt", EncodingUtils.encodeUri("/#/test.txt"));
    assertEquals("%20", EncodingUtils.encodeUri(" "));
    assertEquals("", EncodingUtils.encodeUri(""));
    assertEquals("", EncodingUtils.encodeUri(null));
  }

  @Test
  public void testEncodeFilename() throws Exception {
    assertEquals("file", EncodingUtils.encodeFilename("file", "MSIE"));
    assertEquals("file", EncodingUtils.encodeFilename("file", "Mozilla"));

    assertEquals("a+b", EncodingUtils.encodeFilename("a b", "MSIE"));
    assertEquals("a b", EncodingUtils.encodeFilename("a b", "Mozilla"));

    assertEquals("%23", EncodingUtils.encodeFilename("#", "MSIE"));
    assertEquals("#", EncodingUtils.encodeFilename("#", "Mozilla"));

    assertEquals("%C3%A5%C3%A4%C3%B6", EncodingUtils.encodeFilename("åäö", "MSIE"));
    assertEquals("=?UTF-8?B?w6XDpMO2?=", EncodingUtils.encodeFilename("åäö", "Mozilla"));

    assertEquals("%C3%BC", EncodingUtils.encodeFilename("\u00fc", "MSIE"));
    assertEquals("=?UTF-8?B?w7w=?=", EncodingUtils.encodeFilename("\u00fc", "Mozilla"));
  }
}