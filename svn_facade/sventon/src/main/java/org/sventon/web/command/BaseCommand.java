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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.SVNConnection;
import org.sventon.model.RepositoryName;
import org.sventon.model.Revision;
import org.sventon.util.PathUtil;
import org.sventon.util.RepositoryEntryComparator;
import org.sventon.util.RepositoryEntrySorter;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * BaseCommand.
 * <p/>
 * Command class used to bind and pass servlet parameter arguments in sventon.
 * <p/>
 * A newly created instance is initialized to have path <code>/</code> and
 * revision <code>null</code>.
 *
 * @author patrik@sventon.org
 * @author jesper@sventon.org
 */
public class BaseCommand {

  /**
   * Constructor.
   */
  public BaseCommand() {
  }

  /**
   * The full path.
   */
  private String path = "/";

  /**
   * The peg revision.
   */
  private long pegRevision = -1;

  /**
   * The revision.
   */
  private Revision revision = Revision.HEAD;

  /**
   * The revision number.
   */
  private long revisionNumber = -1;

  /**
   * Repository name.
   */
  private RepositoryName repositoryName;

  /**
   * The sort type.
   */
  private RepositoryEntryComparator.SortType sortType;

  /**
   * Sort mode.
   */
  private RepositoryEntrySorter.SortMode sortMode;

  /**
   * Logger for this class and sub classes.
   */
  protected final Log logger = LogFactory.getLog(getClass().getName());

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
    if (StringUtils.isEmpty(path)) {
      this.path = "/";
    } else {
      if (path.startsWith("/")) {
        this.path = path;
      } else {
        this.path = "/" + path;
      }
    }
  }

  /**
   * @return Returns the revision.
   */
  public Revision getRevision() {
    return revision;
  }

  /**
   * Set revision. Any revision is legal here (but may be rejected by the
   * validator, {@link BaseCommandValidator}).
   * <p/>
   * All case variations of the logical name "HEAD" will be converted to HEAD,
   * all other revision arguments will be set as is.
   *
   * @param revision The revision to set.
   */
  public void setRevision(final Revision revision) {
    Validate.notNull(revision);
    this.revision = revision;
    this.revisionNumber = revision.getNumber();
  }

  /**
   * Sets the peg revision.
   *
   * @param pegRevision Peg revision.
   */
  public void setPegRevision(final long pegRevision) {
    Validate.isTrue(pegRevision > 0);
    this.pegRevision = pegRevision;
  }

  /**
   * Gets the peg revision.
   *
   * @return Peg revision.
   */
  public long getPegRevision() {
    return this.pegRevision;
  }

  /**
   * Returns the revision number.
   *
   * @return Revision number.
   */
  public long getRevisionNumber() {
    return revision.getNumber() < 0 ? revisionNumber : revision.getNumber();
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
   * {@link BaseCommand#getPath()}.
   * <p/>
   * The returned string will have a final "/". If the path info is empty, ""
   * (empty string) will be returned.
   *
   * @return Path excluding target (end/leaf)
   */
  public String getParentPath() {
    String work = getPath();

    if (work.equals("/")) {
      return work;
    }

    if (work.endsWith("/")) {
      work = work.substring(0, work.length() - 1);
    }

    final int lastIndex = work.lastIndexOf('/');
    if (lastIndex == -1) {
      return "";
    } else {
      return work.substring(0, lastIndex) + "/";
    }
  }

  /**
   * Get path, excluding the leaf. For complete path including target,see
   * {@link BaseCommand#getPath()}.
   * <p/>
   * The returned string will have a final "/". If the path info is empty, ""
   * (empty string) will be returned.
   *
   * @return Path excluding target (end/leaf)
   */
  public String getPathPart() {
    return FilenameUtils.getFullPath(getPath());
  }

  /**
   * Gets the sort type, i.e. the field to sort on.
   *
   * @return Sort type
   */
  public RepositoryEntryComparator.SortType getSortType() {
    return sortType;
  }

  /**
   * Sets the sort type, i.e. which field to sort on.
   *
   * @param sortType Sort type
   */
  public void setSortType(final RepositoryEntryComparator.SortType sortType) {
    if (sortType != null) {
      this.sortType = sortType;
    }
  }

  /**
   * Gets the sort mode, ascending or descending.
   *
   * @return Sort mode
   */
  public RepositoryEntrySorter.SortMode getSortMode() {
    return sortMode;
  }

  /**
   * Sets the sort mode.
   *
   * @param sortMode Sort mode
   */
  public void setSortMode(final RepositoryEntrySorter.SortMode sortMode) {
    if (sortMode != null) {
      this.sortMode = sortMode;
    }
  }

  /**
   * Sets the repository name.
   *
   * @param name Repository name
   */
  public void setName(final RepositoryName name) {
    this.repositoryName = name;
  }

  /**
   * Gets the repository name.
   *
   * @return The repository name.
   */
  public RepositoryName getName() {
    return repositoryName;
  }

  @Override
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  /**
   * Translates the revision and the peg revision into a number, if needed.
   * <p/>
   * Handles the logical <i>HEAD</i> revision. Also handles date based revisions,
   * by getting the closest revision number before or at the specified date stamp.
   *
   * @param headRevision The current HEAD revision.
   * @param connection   Repository connection.
   * @return The revision number.
   * @throws SVNException if unable to communicate with repository.
   */
  public long translateRevision(long headRevision, final SVNConnection connection) throws SVNException {
    final SVNRepository repository = connection.getDelegate();
    if (revision.getNumber() < 0 && revisionNumber < 0) {
      if (Revision.HEAD.equals(revision)) {
        revisionNumber = headRevision;
      } else if (revision.getNumber() == -1 && revision.getDate() != null) {
        revisionNumber = repository.getDatedRevision(revision.getDate());
      } else {
        logger.warn("Unexpected revision: " + revision);
        revisionNumber = headRevision;
      }
    }
    return revisionNumber;
  }

  /**
   * @return True if peg revision is set.
   */
  public boolean hasPegRevision() {
    return pegRevision > -1;
  }

  /**
   * Creates a redirect url for browsing a directory.
   * <p/>
   * Note: A trailing slash ("/") will be appended if missing on path.
   *
   * @return Url
   */
  public String createListUrl() {
    assertNameSet();
    return "/repos/" + repositoryName.toString() + "/list" + getPathWithTrailingSlash();
  }

  /**
   * Creates a redirect url for viewing a file.
   * <p/>
   * Note: A trailing slash ("/") will be removed if found on path.
   *
   * @return Url
   */
  public String createShowFileUrl() {
    assertNameSet();
    return "/repos/" + repositoryName.toString() + "/show" + getPathStripTrailingSlash();
  }

  private void assertNameSet() {
    if (repositoryName == null) {
      throw new IllegalStateException("Name has not been set");
    }
  }

  public String getPathWithTrailingSlash() {
    String path = this.path;
    if (!path.endsWith("/")) {
      path += "/";
    }
    return path;
  }

  private String getPathStripTrailingSlash() {
    String path = this.path;
    if (path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }
    return path;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
