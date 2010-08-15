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
package org.sventon.web.command;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.sventon.model.PathRevision;

/**
 * MultipleEntriesCommand.
 * <p/>
 * Command class used to bind and pass servlet parameter arguments in sventon, including a list
 * of selected entries.
 * <p/>
 * A newly created instance is initialized to have path <code>/</code> and
 * revision <code>null</code>.
 *
 * @author jesper@sventon.org
 */
public class MultipleEntriesCommand extends BaseCommand {

  /**
   * Entries.
   */
  private PathRevision[] entries = new PathRevision[0];

  /**
   * Gets the entries.
   *
   * @return Entries
   */
  public PathRevision[] getEntries() {
    return entries;
  }

  /**
   * Sets the entries.
   *
   * @param entries Entries.
   */
  public void setEntries(final PathRevision[] entries) {
    this.entries = entries;
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