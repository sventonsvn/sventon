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
package org.sventon.cache.objectcache;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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
  private final String checksum;

  /**
   * Constructor.
   *
   * @param checksum Subversion entry checksum
   * @param path     Path
   */
  public ObjectCacheKey(final String path, final String checksum) {
    this.path = path;
    this.checksum = checksum;
  }

  @Override
  public boolean equals(final Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
