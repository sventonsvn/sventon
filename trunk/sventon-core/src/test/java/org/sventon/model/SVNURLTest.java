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
import org.sventon.SventonException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SVNURLTest {

  @Test
  public void parseSpacesAndNonAscii() throws Exception {
    assertEquals("http://host/path%20with%20space/project/espa%C3%B1a",
        SVNURL.parse("http://host/path with space/project/españa").getUrl());
  }

  @Test
  public void parseHash() throws Exception {
    assertEquals("http://host/path%20with%20#/project",
        SVNURL.parse("http://host/path with #/project/").getUrl());
  }

  @Test
  public void parseTrailingSlash() throws Exception {
    assertEquals("http://host/path",
        SVNURL.parse("http://host/path/").getUrl());
  }

  @Test
  public void validProtocols() throws Exception {
    SVNURL.parse("http://foo/bar");
    SVNURL.parse("https:/foo/bar");
    SVNURL.parse("svn://foo/bar");
    SVNURL.parse("svn+ssh://host/foo/bar");
    SVNURL.parse("file://host/foo/bar");
  }

  @Test
  public void unknownProtocol() throws Exception {
    try {
      SVNURL.parse("ftp://foo/bar");
      fail("Unknown subversion protocol should throw SventonException");
    } catch (SventonException e) {
      //Expected
    }
  }

  @Test
  public void testGetFullPath() throws Exception {
    assertEquals("http://localhost/", SVNURL.parse("http://localhost/").getFullPath(""));
    assertEquals("http://localhost/", SVNURL.parse("http://localhost/").getFullPath("/"));
    assertEquals("http://localhost/trunk", SVNURL.parse("http://localhost/").getFullPath("trunk"));
  }

}
