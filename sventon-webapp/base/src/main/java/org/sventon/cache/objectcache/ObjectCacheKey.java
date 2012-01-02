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
package org.sventon.cache.objectcache;

import java.io.Serializable;

/**
 * Cachekey class.
 *
 * @author jesper@sventon.org
 */
public final class ObjectCacheKey implements Serializable {

  private static final long serialVersionUID = 7546874623311095090L;

  /**
   * Path.
   */
  private final String path;
  /**
   * Checksum.
   */
  private final long revisionNumber;

  /**
   * Constructor.
   *
   * @param path           Path
   * @param revisionNumber Revision
   */
  public ObjectCacheKey(final String path, final long revisionNumber) {
    this.path = path;
    this.revisionNumber = revisionNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ObjectCacheKey)) return false;
    final ObjectCacheKey that = (ObjectCacheKey) o;
    return revisionNumber == that.revisionNumber && !(path != null ? !path.equals(that.path) : that.path != null);
  }

  @Override
  public int hashCode() {
    int result = path != null ? path.hashCode() : 0;
    result = 31 * result + (int) (revisionNumber ^ (revisionNumber >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "ObjectCacheKey{" + "path='" + path + '\'' + ", revisionNumber=" + revisionNumber + '}';
  }
}
