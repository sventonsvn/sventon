/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.command;

import de.berlios.sventon.util.PathUtil;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * SVNBaseCommand.
 * <p/>
 * Command class used to bind and pass servlet parameter arguments in sventon.
 * <p/>
 * A newly created instance is initialized to have path <code>/</code> and
 * revision <code>null</code>.
 *
 * @author patrikfr@users.berlios.de
 * @author jesper@users.berlios.de
 */
public final class SVNBaseCommand {

  /**
   * The full path.
   */
  private String path = "/";

  /**
   * The revision.
   */
  private String revision = null;

  /**
   * Repository instance name.
   */
  private String name;

  /**
   * The sort type.
   */
  private String sortType;

  /**
   * Sort mode.
   */
  private String sortMode;

  /**
   * Gets the path.
   *
   * @return The path.
   */
  public String getPath() {
    return path;
  }

  /**
   * Set path. <code>null</code> and <code>""</code> arguments will be
   * converted <code>/</code>
   *
   * @param path The path to set.
   */
  public void setPath(final String path) {
    if (path == null || "".equals(path)) {
      this.path = "/";
    } else {
      this.path = path.trim();
    }
  }

  /**
   * @return Returns the revision.
   */
  public String getRevision() {
    return revision;
  }

  /**
   * Set revision. Any revision is legal here (but may be rejected by the
   * validator, {@link SVNBaseCommandValidator}).
   * <p/>
   * All case variations of the logical name "HEAD" will be converted to HEAD,
   * all other revision arguments will be set as is.
   *
   * @param revision The revision to set.
   */
  public void setRevision(final String revision) {
    if (revision != null) {
      final String trimmedRevision = revision.trim();
      if ("HEAD".equalsIgnoreCase(trimmedRevision)) {
        this.revision = "HEAD";
      } else {
        this.revision = trimmedRevision;
      }
    }
  }

  /**
   * Get target (leaf/end) part of the <code>path</code>, it could be a file
   * or a directory.
   * <p/>
   * The returned string will have no final "/", even if it is a directory.
   *
   * @return Target part of the path.
   */
  public String getTarget() {
    return PathUtil.getTarget(getPath());
  }

  /**
   * Get path, excluding the end/leaf. For complete path including target,see
   * {@link SVNBaseCommand#getPath()}.
   * <p/>
   * The returned string will have a final "/". If the path info is empty, ""
   * (empty string) will be returned.
   *
   * @return Path excluding taget (end/leaf)
   */
  public String getPathNoLeaf() {
    return PathUtil.getPathNoLeaf(getPath());
  }


  /**
   * Get path, excluding the leaf. For complete path including target,see
   * {@link SVNBaseCommand#getPath()}.
   * <p/>
   * The returned string will have a final "/". If the path info is empty, ""
   * (empty string) will be returned.
   *
   * @return Path excluding taget (end/leaf)
   */
  public String getPathPart() {
    return PathUtil.getPathPart(getPath());
  }

  /**
   * Gets the sort type, i.e. the field to sort on.
   *
   * @return Sort type
   */
  public String getSortType() {
    return sortType;
  }

  /**
   * Sets the sort type, i.e. which field to sort on.
   *
   * @param sortType Sort type
   */
  public void setSortType(final String sortType) {
    if (sortType != null) {
      this.sortType = sortType;
    }
  }

  /**
   * Gets the sort mode, ascending or descending.
   *
   * @return Sort mode
   */
  public String getSortMode() {
    return sortMode;
  }

  /**
   * Sets the sort mode.
   *
   * @param sortMode Sort mode
   */
  public void setSortMode(final String sortMode) {
    if (sortMode != null) {
      this.sortMode = sortMode;
    }
  }

  /**
   * Sets the repository instance name.
   *
   * @param name Repository instance name
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Gets the repository instance name.
   *
   * @return The repository instance name.
   */
  public String getName() {
    return name;
  }

  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
