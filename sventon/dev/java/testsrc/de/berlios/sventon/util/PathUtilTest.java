package de.berlios.sventon.util;

import junit.framework.TestCase;

public class PathUtilTest extends TestCase {

  public void testGetTarget() {
    assertEquals("File.java", PathUtil.getTarget("trunk/src/File.java"));
    assertEquals("src", PathUtil.getTarget("trunk/src/"));
    assertEquals("src", PathUtil.getTarget("trunk/src"));
    assertEquals("", PathUtil.getTarget(""));
    assertEquals("", PathUtil.getTarget("/"));
    assertEquals("", PathUtil.getTarget(null));
  }

  public void testGetPathPart() {
    assertEquals("/trunk/src/", PathUtil.getPathPart("/trunk/src/File.java"));
    assertEquals("trunk/src/", PathUtil.getPathPart("trunk/src/File.java"));
    assertEquals("trunk/src/", PathUtil.getPathPart("trunk/src/"));
    assertEquals("trunk/", PathUtil.getPathPart("trunk/src"));  // 'src' will be treated as a target leaf
    assertEquals("", PathUtil.getPathPart(""));
    assertEquals("/", PathUtil.getPathPart("/"));
    assertEquals("", PathUtil.getPathPart(null));
  }

  public void testGetPathNoLeaf() {
    assertEquals("/trunk/src/", PathUtil.getPathNoLeaf("/trunk/src/File.java"));
    assertEquals("trunk/src/", PathUtil.getPathNoLeaf("trunk/src/File.java"));
    assertEquals("trunk/", PathUtil.getPathNoLeaf("trunk/src/"));
    assertEquals("", PathUtil.getPathNoLeaf(""));
    assertEquals("", PathUtil.getPathNoLeaf("/"));
    assertEquals("", PathUtil.getPathNoLeaf(null));
  }

  public void testGetFileExtension() throws Exception {
    assertEquals("java", PathUtil.getFileExtension("trunk/src/File.java"));
    assertEquals("", PathUtil.getFileExtension("trunk/src/File"));
    assertEquals("htpasswd", PathUtil.getFileExtension("trunk/src/.htpasswd"));
    assertEquals("", PathUtil.getFileExtension(null));
    assertEquals("", PathUtil.getFileExtension("trunk/src.test/htpasswd"));
  }

}