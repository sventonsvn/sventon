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
package org.sventon.model;

import org.springframework.web.util.UriUtils;
import org.sventon.SventonException;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SVNURL.
 */
public class SVNURL implements Serializable {

  private static final long serialVersionUID = -226312079488166629L;

  /**
   * URI pattern. See RFC 2396 Appendix B.
   */
  private static final Pattern URI_PATTERN = Pattern.compile("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");
  private static final int SCHEME_COMPONENT = 2;

  private final String url;

  /**
   * Constructor.
   *
   * @param url the encoded URI to subversion repository.
   */
  public SVNURL(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  /**
   * @param relativePath Path relative from root.
   * @return The full path, including the root URL.
   */
  public String getFullPath(final String relativePath) {
    return url + (relativePath.startsWith("/") ? "" : "/") + relativePath;
  }

  private static void validate(final String url) throws SventonException {
    final String scheme = getScheme(url);
    if (!Protocol.isValidProtocol(scheme)) {
      throw new SventonException("Unknown protocol " + scheme + " in URL " + url + ". Valid protocols are : " + Arrays.toString(Protocol.values()));
    }
  }

  private static String getScheme(final String url) throws SventonException {
    final Matcher matcher = URI_PATTERN.matcher(url);
    if (!matcher.matches()) {
      throw new SventonException("Malformed URI " + url);
    }
    return matcher.group(SCHEME_COMPONENT);
  }

  /**
   * Parse a URI into a SVNURL.
   * <p/>
   * This parser will URI encode the string and then check to see if it is a valid subversion protocol.
   *
   * @param url the un-encoded URI
   * @return a SVNURL object wrapping the encoded url string.
   * @throws SventonException if the given URI is malformed or not possible to URI encode or not having a valid subversion protocol.
   */
  public static SVNURL parse(final String url) throws SventonException {
    try {
      final String encodedUri = UriUtils.encodeUri(url, "UTF-8");
      validate(encodedUri);

      return new SVNURL(trim(encodedUri));
    } catch (UnsupportedEncodingException e) {
      throw new SventonException("Could not encode URI " + url, e);
    }
  }

  private static String trim(String url) {
    if (url.endsWith("/")) {
      return url.substring(0, url.length() - 1);
    }
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

  /**
   * Enumeration over valid SVN protocols.
   */
  public enum Protocol {
    HTTP("http"),
    HTTPS("https"),
    SVN("svn"),
    SVN_SSH("svn+ssh"),
    FILE("file");

    private final String name;

    Protocol(final String name) {
      this.name = name;
    }

    /**
     * Checks if given string is a valid svn protocol.
     *
     * @param protocol the string
     * @return true if given string is a valid svn protocol, otherwise false
     */
    public static boolean isValidProtocol(final String protocol) {
      for (Protocol p : Protocol.values()) {
        if (p.name.equalsIgnoreCase(protocol)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public String toString() {
      return name;
    }
  }

}
