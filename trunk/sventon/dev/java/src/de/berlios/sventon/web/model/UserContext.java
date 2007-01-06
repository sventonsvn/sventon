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
package de.berlios.sventon.web.model;

import de.berlios.sventon.repository.RepositoryEntryComparator;
import de.berlios.sventon.repository.RepositoryEntrySorter;

/**
 * Class containing user specific data.
 * An instance will be stored on the user's HTTPSession.
 *
 * @author jesper@users.berlios.de
 */
public class UserContext {

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
   * Search mode.
   * @see de.berlios.sventon.web.ctrl.AbstractSVNTemplateController#ENTRIES_SEARCH_MODE
   * @see de.berlios.sventon.web.ctrl.AbstractSVNTemplateController#LOGMESSAGES_SEARCH_MODE
   */
  private String searchMode;

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
   * @see de.berlios.sventon.web.ctrl.AbstractSVNTemplateController#ENTRIES_SEARCH_MODE
   * @see de.berlios.sventon.web.ctrl.AbstractSVNTemplateController#LOGMESSAGES_SEARCH_MODE
   */
  public String getSearchMode() {
    return searchMode;
  }

  /**
   * Sets the user's search mode.
   *
   * @param searchMode Search mode
   * @see de.berlios.sventon.web.ctrl.AbstractSVNTemplateController#ENTRIES_SEARCH_MODE
   * @see de.berlios.sventon.web.ctrl.AbstractSVNTemplateController#LOGMESSAGES_SEARCH_MODE
   */
  public void setSearchMode(String searchMode) {
    this.searchMode = searchMode;
  }

}
