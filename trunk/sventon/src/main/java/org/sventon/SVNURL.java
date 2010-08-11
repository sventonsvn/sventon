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
package org.sventon;

/**
 * SVNURL.
 */
public class SVNURL {

  private final String url;

  /**
   * Constructor.
   *
   * @param url URL to subversion repository.
   */
  public SVNURL(String url) {
    this.url = url;
  }

  public static SVNURL parse(String url) throws SVNException {
    try {
      return new SVNURL(org.tmatesoft.svn.core.SVNURL.parseURIDecoded(url).toString());
    } catch (org.tmatesoft.svn.core.SVNException e) {
      throw new SVNException("Unable to parse URL: " + url);
    }
  }

  public String getUrl() {
    return url;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SVNURL)) return false;
    final SVNURL svnurl = (SVNURL) o;
    return !(url != null ? !url.equals(svnurl.url) : svnurl.url != null);
  }

  @Override
  public int hashCode() {
    return url != null ? url.hashCode() : 0;
  }

  @Override
  public String toString() {
    return url;
  }
}
