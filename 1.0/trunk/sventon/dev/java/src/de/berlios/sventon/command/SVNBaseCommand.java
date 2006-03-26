/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.command;

import de.berlios.sventon.util.PathUtil;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * SVNBaseCommand.
 * <p>
 * Command class used to bind and pass servlet parameter arguments in sventon.
 * <p>
 * A newly created instance is initialized to have path <code>/</code> and
 * revision <code>null</code>.
 * 
 * @author patrikfr@users.berlios.de
 */
public class SVNBaseCommand {

  /** The full path. */
  private String path = "/";

  /** The revision. */
  private String revision = null;


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
   * <p>
   * All case variations of the logical name "HEAD" will be converted to HEAD,
   * all other revision arguments will be set as is.
   * 
   * @param revision The revision to set.
   */
  public void setRevision(final String revision) {
    if (revision != null && "HEAD".equalsIgnoreCase(revision)) {
      this.revision = "HEAD";
    } else {
      this.revision = revision;
    }
  }

  /**
   * Get target (leaf/end) part of the <code>path</code>, it could be a file
   * or a directory.
   * <p>
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
   * <p>
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
   * <p>
   * The returned string will have a final "/". If the path info is empty, ""
   * (empty string) will be returned.
   *
   * @return Path excluding taget (end/leaf)
   */
  public String getPathPart() {
    return PathUtil.getPathPart(getPath());
  }

  /**

  /**
   * Return the contents of this object as a map model.
   * <p>
   * Model data keys:
   * <ul>
   * <li><code>completePath</code></li>
   * <li><code>revision</code></li>
   * <li><code>path</code></li>
   * </ul>
   * 
   * @return The model map.
   */
  public Map<String, Object> asModel() {
    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("revision", getRevision());
    model.put("path", getPath());
    return model;
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return "SVNBaseCommand{path='" + path + "', " +
    "revision='" + revision + "'}";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj instanceof SVNBaseCommand) {
      SVNBaseCommand o = (SVNBaseCommand) obj;
      return (StringUtils.equals(o.path, path) && StringUtils.equals(o.revision, revision));
    } else {
      return false;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    // TODO: Impelement!
    return super.hashCode();
  }

}
