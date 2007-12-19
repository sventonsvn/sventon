package de.berlios.sventon.web.model;

import de.berlios.sventon.repository.RepositoryEntryComparator;
import de.berlios.sventon.repository.RepositoryEntrySorter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * Class containing user specific data, the data is stored per configured repository if more than
 * one repository is configured.
 *
 * @author jesper@users.berlios.de
 */
public class UserRepositoryContext implements Serializable {

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
  private RepositoryEntryTray repositoryEntryTray = new RepositoryEntryTray();

  /**
   * Search mode.
   *
   * @see de.berlios.sventon.web.ctrl.AbstractSVNTemplateController#ENTRIES_SEARCH_MODE
   * @see de.berlios.sventon.web.ctrl.AbstractSVNTemplateController#LOGMESSAGES_SEARCH_MODE
   */
  private String searchMode;

  /**
   * User id.
   */
  private String uid;

  /**
   * Password.
   */
  private String pwd;

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
   * Gets the user id.
   *
   * @return User id, or null.
   */
  public String getUid() {
    return decodeBase64(uid);
  }

  /**
   * Sets the user id.
   * <p/>
   * The uid is stored obfuscated as Base64.
   *
   * @param uid User id.
   */
  public void setUid(final String uid) {
    this.uid = encodeBase64(uid);
  }

  /**
   * Gets the password.
   *
   * @return Password, or null.
   */
  public String getPwd() {
    return decodeBase64(pwd);
  }

  /**
   * Sets the password.
   * The pwd is stored obfuscated as Base64.
   *
   * @param pwd Password.
   */
  public void setPwd(final String pwd) {
    this.pwd = encodeBase64(pwd);
  }

  /**
   * Checks if user id and password has been set.
   *
   * @return True if user id and password has been set, false if not.
   */
  public boolean hasCredentials() {
    return uid != null && pwd != null;
  }

  public void clearCredentials() {
    uid = null;
    pwd = null;
  }

  private String encodeBase64(final String s) {
    if (s != null) {
      return new String(Base64.encodeBase64(s.getBytes()));
    } else {
      return null;
    }
  }

  private String decodeBase64(final String s) {
    if (s != null) {
      return new String(Base64.decodeBase64(s.getBytes()));
    } else {
      return null;
    }
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
       append("sortType", sortType).
       append("sortMode", sortMode).
       append("latestRevisionsDisplayCount", latestRevisionsDisplayCount).
       append("charset", charset).
       append("uid", uid != null ? "*****" : "<null>").
       append("pwd", pwd != null ? "*****" : "<null>").
       toString();
  }
}
