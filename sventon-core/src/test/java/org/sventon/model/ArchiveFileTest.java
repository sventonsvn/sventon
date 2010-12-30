/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArchiveFileTest {

  @Test
  public void testArchiveFile() throws Exception {
    // A zip file containing one empty file called 'a'.
    byte[] content = new byte[]{
        80, 75, 3, 4, 20, 0, 2, 0, 8, 0, 17, 12, 2, 53, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 97, 3,
        0, 80, 75, 1, 2, 20, 0, 20, 0, 2, 0, 8, 0, 17, 12, 2, 53, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 97, 80, 75, 5, 6, 0, 0, 0, 0, 1, 0, 1, 0, 47, 0, 0, 0, 33,
        0, 0, 0, 0, 0
    };
    final ArchiveFile file = new ArchiveFile(content);
    assertEquals(1, file.getEntries().size());
  }
}