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
 * Represents a command containing a revision range, ie. a (from) revision and a stop-revision.
 */
public class RevisionRangeCommand extends BaseCommand {

  /**
   * The revision to stop at in range searches, like showLogs.
   */
  private Revision stopRevision = Revision.FIRST;

  /**
   * @return Returns the revision to stop at when performing range searches.
   */
  public Revision getStopRevision() {
    return stopRevision;
  }

  /**
   * Set stop revision.
   *
   * @param stopRevision The revision to stop at when performing range searches.
   */
  public void setStopRevision(final Revision stopRevision) {
    this.stopRevision = stopRevision;
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
