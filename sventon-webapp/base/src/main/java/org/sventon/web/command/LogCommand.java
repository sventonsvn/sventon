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
package org.sventon.web.command;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.sventon.model.Revision;

/**
 * Command class used to get log details for a file/dir entry.
 */
public class LogCommand extends RevisionRangeCommand {

  private String nextPath;

  private Revision nextRevision;

  private boolean stopOnCopy = true;

  private boolean paging = true;

  public String getNextPath() {
    return nextPath;
  }

  public void setNextPath(final String nextPath) {
    this.nextPath = nextPath;
  }

  public Revision getNextRevision() {
    return nextRevision;
  }

  public void setNextRevision(final Revision nextRevision) {
    this.nextRevision = nextRevision;
  }

  public boolean isStopOnCopy() {
    return stopOnCopy;
  }

  public void setStopOnCopy(final boolean stopOnCopy) {
    this.stopOnCopy = stopOnCopy;
  }

  public boolean isPaging() {
    return paging;
  }

  public void setPaging(final boolean paging) {
    this.paging = paging;
  }

  public long calculateFromRevision(long headRevision) {
    final long fromRevision;
    final Revision nextRevision = calculateNextRevision();
    if (nextRevision.isHeadRevision()) {
      fromRevision = headRevision;
    } else {
      fromRevision = nextRevision.getNumber();
    }
    return fromRevision;
  }

  /**
   * @return NextRevision or Revision if undefined.
   */
  public Revision calculateNextRevision() {
    return nextRevision != null ? nextRevision : getRevision();
  }

  /**
   * @return NextPath or Path if undefined.
   */
  public String calculateNextPath() {
    return nextPath != null ? nextPath : getPath();
  }

  @Override
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
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
