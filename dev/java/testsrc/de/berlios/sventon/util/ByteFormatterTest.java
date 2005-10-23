package de.berlios.sventon.util;

import junit.framework.TestCase;

public class ByteFormatterTest extends TestCase {

  public void testFormat() throws Exception {
    
    //TODO: This test is plattform/locale dependent, fix this when i18n is handled.
    
    assertEquals("1000", ByteFormatter.format(1000L));
    assertEquals("1 kB", ByteFormatter.format(1200L));
    assertEquals("1 kB", ByteFormatter.format(2047L));
    assertEquals("2 kB", ByteFormatter.format(2048L));
    assertEquals("12 kB", ByteFormatter.format(12345L));
    assertEquals("120 kB", ByteFormatter.format(123456L));
    assertEquals("1,18 MB", ByteFormatter.format(1234567L));
    assertEquals("11,77 MB", ByteFormatter.format(12345678L));
    assertEquals("117,74 MB", ByteFormatter.format(123456789L));
    assertEquals("1,15 GB", ByteFormatter.format(1234567890L));
    assertEquals("11,50 GB", ByteFormatter.format(12345678901L));
    assertEquals("114,98 GB", ByteFormatter.format(123456789012L));
    assertEquals("1,12 TB", ByteFormatter.format(1234567890123L));
  }
}