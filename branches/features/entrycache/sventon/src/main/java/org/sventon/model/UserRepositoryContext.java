/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.util.WebUtils;
import org.sventon.util.RepositoryEntryComparator;
import org.sventon.util.RepositoryEntrySorter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.UUID;

/**
 * Class containing user specific data, the data is stored per configured repository if more than
 * one repository is configured.
 *
 * @author jesper@sventon.org
 */
public final class UserRepositoryContext implements Serializable {

  private static final long serialVersionUID = -6699351885300583211L;

  /**
   * The sort type.
   */
  private RepositoryEntryComparator.SortType sortType;

  /**
   * Sort mode.
   */
  private RepositoryEntrySorter.SortMode sortMode;

  /**
   * Revisions display count. Default set to 1.
   */
  private int latestRevisionsDisplayCount = 1;

  /**
   * The user chosen charset.
   */
  private String charset;

  /**
   * The user's repository entry tray.
   */
  private final RepositoryEntryTray repositoryEntryTray = new RepositoryEntryTray();

  /**
   * Search mode.
   *
   * @see org.sventon.web.ctrl.template.AbstractTemplateController#ENTRIES_SEARCH_MODE
   * @see org.sventon.web.ctrl.template.AbstractTemplateController#LOGMESSAGES_SEARCH_MODE
   */
  private String searchMode;

  /**
   * User credentials.
   */
  private Credentials credentials = Credentials.EMPTY;

  private boolean isWaitingForExport = false;

  private UUID uuid;

  /**
   * Gets the UserContext instance from the user's HTTPSession.
   * If session does not exist, it will be created. If the attribute
   * <code>userContext</code> does not exists, a new instance will be
   * created and added to the session.
   *
   * @param request        The HTTP request.
   * @param repositoryName Instance name.
   * @return The UserContext instance.
   * @see UserContext
   */
  public static UserRepositoryContext getContext(final HttpServletRequest request, final RepositoryName repositoryName) {
    final HttpSession session = request.getSession(true);
    final String uid = ServletRequestUtils.getStringParameter(request, "uid", "");
    final String pwd = ServletRequestUtils.getStringParameter(request, "pwd", "");

    final UserContext userContext = (UserContext) WebUtils.getOrCreateSessionAttribute(
        session, "userContext", UserContext.class);

    UserRepositoryContext userRepositoryContext = userContext.getUserRepositoryContext(repositoryName);
    if (userRepositoryContext == null) {
      userRepositoryContext = new UserRepositoryContext();
      userContext.add(repositoryName, userRepositoryContext);
    }

    if (!StringUtils.isEmpty(uid) && !StringUtils.isEmpty(pwd)) {
      userRepositoryContext.setCredentials(new Credentials(uid, pwd));
    }
    return userRepositoryContext;
  }

  public boolean getIsWaitingForExport() {
    return isWaitingForExport;
  }

  public boolean getIsLoggedIn() {
    return hasCredentials();
  }

  public void setIsWaitingForExport(final boolean isWaitingForExport) {
    this.isWaitingForExport = isWaitingForExport;
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
   * Sets how many revisions that should be displayed in the <i>latest commit info</i> DIV.
   *
   * @param latestRevisionsDisplayCount Count
   */
  public void setLatestRevisionsDisplayCount(final int latestRevisionsDisplayCount) {
    this.latestRevisionsDisplayCount = latestRevisionsDisplayCount;
  }

  /**
   * Gets number of revisions that should be displayed in the <i>latest commit info</i> DIV.
   *
   * @return Count
   */
  public int getLatestRevisionsDisplayCount() {
    return latestRevisionsDisplayCount;
  }

  /**
   * Gets the user's charset.
   *
   * @return Charset
   */
  public String getCharset() {
    return charset;
  }

  /**
   * Sets the user's charset.
   *
   * @param charset Charset
   */
  public void setCharset(final String charset) {
    this.charset = charset;
  }

  /**
   * Gets the user's search mode.
   *
   * @return Search mode
   * @see org.sventon.web.ctrl.template.AbstractTemplateController#ENTRIES_SEARCH_MODE
   * @see org.sventon.web.ctrl.template.AbstractTemplateController#LOGMESSAGES_SEARCH_MODE
   */
  public String getSearchMode() {
    return searchMode;
  }

  /**
   * Sets the user's search mode.
   *
   * @param searchMode Search mode
   * @see org.sventon.web.ctrl.template.AbstractTemplateController#ENTRIES_SEARCH_MODE
   * @see org.sventon.web.ctrl.template.AbstractTemplateController#LOGMESSAGES_SEARCH_MODE
   */
  public void setSearchMode(final String searchMode) {
    this.searchMode = searchMode;
  }

  /**
   * Gets the repository entry tray.
   *
   * @return The entry tray instance.
   */
  public RepositoryEntryTray getRepositoryEntryTray() {
    return repositoryEntryTray;
  }

  /**
   * Sets the user's credentials.
   *
   * @param credentials Credentials.
   */
  public void setCredentials(final Credentials credentials) {
    Validate.notNull(credentials);
    this.credentials = credentials;
  }

  /**
   * Gets the user's credentials.
   *
   * @return User's credentials, or null.
   */
  public Credentials getCredentials() {
    return credentials;
  }

  /**
   * Checks if user id and password has been set.
   *
   * @return True if user id and password has been set, false if not.
   */
  public boolean hasCredentials() {
    return !credentials.isEmpty();
  }

  /**
   * Clears the credentials from memory.
   */
  public void clearCredentials() {
    credentials = Credentials.EMPTY;
  }

  /**
   * Gets the export UUID.
   *
   * @return Export UUID
   */
  public UUID getExportUuid() {
    return uuid;
  }

  /**
   * Sets the export UUID.
   *
   * @param uuid UUID
   */
  public void setExportUuid(UUID uuid) {
    this.uuid = uuid;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
        append("sortType", sortType).
        append("sortMode", sortMode).
        append("latestRevisionsDisplayCount", latestRevisionsDisplayCount).
        append("charset", charset).
        toString();
  }
}
