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

public class DirEntryNameSplitterTest {

  @Test
  public void testSplit() throws Exception {
    assertEquals("[a]", new DirEntryNameSplitter("a").split().toString());
    assertEquals("[A]", new DirEntryNameSplitter("A").split().toString());
    assertEquals("[AB]", new DirEntryNameSplitter("AB").split().toString());
    assertEquals("[aa]", new DirEntryNameSplitter("aa").split().toString());
    assertEquals("[aaa]", new DirEntryNameSplitter("aaa").split().toString());
    assertEquals("[ABC]", new DirEntryNameSplitter("ABC").split().toString());
    assertEquals("[ABc]", new DirEntryNameSplitter("ABc").split().toString());
    assertEquals("[Ab, C]", new DirEntryNameSplitter("AbC").split().toString());
    assertEquals("[Camel, Case]", new DirEntryNameSplitter("CamelCase").split().toString());
    assertEquals("[camel, Case]", new DirEntryNameSplitter("camelCase").split().toString());
    assertEquals("[File, 1]", new DirEntryNameSplitter("File1").split().toString());
    assertEquals("[File, 11]", new DirEntryNameSplitter("File11").split().toString());
    assertEquals("[Test, File, A]", new DirEntryNameSplitter("TestFileA").split().toString());
    assertEquals("[Test, File, A, java]", new DirEntryNameSplitter("TestFileA.java").split().toString());
    assertEquals("[Test, file, java]", new DirEntryNameSplitter("Test!file#java").split().toString());
    assertEquals("[apache, maven, 2, 2, 1]", new DirEntryNameSplitter("apache-maven-2.2.1").split().toString());
    assertEquals("[test, file, one]", new DirEntryNameSplitter("test_file_one").split().toString());
    assertEquals("[Recycle, Bin]", new DirEntryNameSplitter("$Recycle.Bin").split().toString());
    assertEquals("[S, 1, 5, 21, 1818, 20664, 312]", new DirEntryNameSplitter("S-1-5-21-1818-20664-312").split().toString());
    assertEquals("[SFDNWIN, exe]", new DirEntryNameSplitter("SFDNWIN.exe").split().toString());
    assertEquals("[76, 00]", new DirEntryNameSplitter("{76-00}").split().toString());
  }

}
