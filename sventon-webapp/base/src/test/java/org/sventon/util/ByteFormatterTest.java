/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
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

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class ByteFormatterTest {

  @Test
  public void testFormat() throws Exception {

    //TODO: This test is platform/locale dependent, fix this when i18n is handled.
    assertEquals("1000", ByteFormatter.format(1000L, new Locale("sv", "SE")));
    assertEquals("1 kB", ByteFormatter.format(1200L, new Locale("sv", "SE")));
    assertEquals("1 kB", ByteFormatter.format(2047L, new Locale("sv", "SE")));
    assertEquals("2 kB", ByteFormatter.format(2048L, new Locale("sv", "SE")));
    assertEquals("12 kB", ByteFormatter.format(12345L, new Locale("sv", "SE")));
    assertEquals("120 kB", ByteFormatter.format(123456L, new Locale("sv", "SE")));
    assertEquals("1,18 MB", ByteFormatter.format(1234567L, new Locale("sv", "SE")));
    assertEquals("11,77 MB", ByteFormatter.format(12345678L, new Locale("sv", "SE")));
    assertEquals("117,74 MB", ByteFormatter.format(123456789L, new Locale("sv", "SE")));
    assertEquals("1,15 GB", ByteFormatter.format(1234567890L, new Locale("sv", "SE")));
    assertEquals("11,50 GB", ByteFormatter.format(12345678901L, new Locale("sv", "SE")));
    assertEquals("114,98 GB", ByteFormatter.format(123456789012L, new Locale("sv", "SE")));
    assertEquals("1,12 TB", ByteFormatter.format(1234567890123L, new Locale("sv", "SE")));
  }
}