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
package org.sventon.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.sventon.model.LogEntryActionType;
import org.sventon.model.RepositoryName;
import static org.sventon.util.EncodingUtils.encode;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Class responsible for creating HTML formatted content.
 *
 * @author jesper@sventon.org
 */
public final class HTMLCreator {

  public static final String LOG_MESSAGE_KEY = "logMessage";
  public static final String AUTHOR_KEY = "author";
  public static final String DATE_KEY = "date";
  public static final String ADDED_COUNT_KEY = "addedCount";
  public static final String MODIFIED_COUNT_KEY = "modifiedCount";
  public static final String REPLACED_COUNT_KEY = "replacedCount";
  public static final String DELETED_COUNT_KEY = "deletedCount";
  public static final String CHANGED_PATHS_KEY = "changedPaths";

  /**
   * Prevent instantiation.
   */
  private HTMLCreator() {
  }

  /**
   * Creates a string containing the details for a given revision.
   *
   * @param bodyTemplate   Body template string.
   * @param logEntry       log entry revision.
   * @param baseURL        Application base URL.
   * @param repositoryName Repository name.
   * @param response       Response, null if n/a.
   * @param dateFormat     Date formatter instance.
   * @return Result
   */
  public static String createRevisionDetailBody(final String bodyTemplate, final SVNLogEntry logEntry, final String baseURL,
                                                final RepositoryName repositoryName, final DateFormat dateFormat,
                                                final HttpServletResponse response) {

    final Map<String, String> valueMap = new HashMap<String, String>();

    int added = 0;
    int modified = 0;
    int replaced = 0;
    int deleted = 0;

    //noinspection unchecked
    final Map<String, SVNLogEntryPath> latestChangedPaths = logEntry.getChangedPaths();
    final List<String> latestPathsList = new ArrayList<String>(latestChangedPaths.keySet());

    for (final String entryPath : latestPathsList) {
      final LogEntryActionType type = LogEntryActionType.parse(latestChangedPaths.get(entryPath).getType());
      switch (type) {
        case ADDED:
          added++;
          break;
        case MODIFIED:
          modified++;
          break;
        case REPLACED:
          replaced++;
          break;
        case DELETED:
          deleted++;
          break;
      }
    }
    valueMap.put(ADDED_COUNT_KEY, Matcher.quoteReplacement(String.valueOf(added)));
    valueMap.put(MODIFIED_COUNT_KEY, Matcher.quoteReplacement(String.valueOf(modified)));
    valueMap.put(REPLACED_COUNT_KEY, Matcher.quoteReplacement(String.valueOf(replaced)));
    valueMap.put(DELETED_COUNT_KEY, Matcher.quoteReplacement(String.valueOf(deleted)));
    valueMap.put(LOG_MESSAGE_KEY, Matcher.quoteReplacement(StringUtils.trimToEmpty(
        WebUtils.nl2br(StringEscapeUtils.escapeHtml(logEntry.getMessage()))))); //TODO: Parse to apply Bugtraq link
    valueMap.put(AUTHOR_KEY, Matcher.quoteReplacement(StringUtils.trimToEmpty(logEntry.getAuthor())));
    valueMap.put(DATE_KEY, dateFormat.format(logEntry.getDate()));
    valueMap.put(CHANGED_PATHS_KEY, Matcher.quoteReplacement(HTMLCreator.createChangedPathsTable(
        logEntry.getChangedPaths(), logEntry.getRevision(), null, baseURL, repositoryName, false, false, response)));

    final StrSubstitutor substitutor = new StrSubstitutor(valueMap);
    return substitutor.replace(bodyTemplate);
  }

  /**
   * Creates a HTML table containing the changed paths for given revision.
   *
   * @param changedPaths      Paths changed in this revision.
   * @param revision          Revision.
   * @param pathAtRevision    The target's path at current revision or <tt>null</tt> if unknown.
   * @param baseURL           Base application URL.
   * @param repositoryName    Repository name.
   * @param showLatestRevInfo If true, the latest revision details DIV will be displayed.
   * @param linkToHead        If true, navigation links will be pointing at HEAD if applicable.
   * @param response          The HTTP response, used to encode the session parameter to the generated URLs. Null if n/a.
   * @return The HTML table.
   */
  public static String createChangedPathsTable(final Map<String, SVNLogEntryPath> changedPaths, final long revision,
                                               final String pathAtRevision, final String baseURL,
                                               final RepositoryName repositoryName, final boolean showLatestRevInfo,
                                               final boolean linkToHead, final HttpServletResponse response) {

    final StringBuilder sb = new StringBuilder("<table class=\"changedPathsTable\">\n");
    sb.append("  <tr>\n");
    sb.append("    <th align=\"left\">Action</th>\n");
    sb.append("    <th align=\"left\">Path</th>\n");
    sb.append("  </tr>\n");

    final List<String> latestPathsList = new ArrayList<String>(changedPaths.keySet());
    Collections.sort(latestPathsList);

    for (final String path : latestPathsList) {
      final SVNLogEntryPath logEntryPath = changedPaths.get(path);
      final LogEntryActionType actionType = LogEntryActionType.parse(logEntryPath.getType());

      sb.append("  <tr>\n");
      sb.append("    <td valign=\"top\"><i>").append(actionType).append("</i></td>\n");

      sb.append("    <td>");

      String goToUrl;
      switch (actionType) {
        case ADDED: // fall thru
        case REPLACED:
          // goToUrl
          goToUrl = createGoToUrl(baseURL, logEntryPath.getPath(), revision, repositoryName, linkToHead);
          if (response != null) {
            goToUrl = response.encodeURL(goToUrl);
          }
          sb.append("<a href=\"").append(goToUrl);
          if (showLatestRevInfo) {
            sb.append("&showlatestrevinfo=true");
          }
          sb.append("\" title=\"Show\">");
          if (logEntryPath.getPath().equals(pathAtRevision)) {
            sb.append("<i>").append(logEntryPath.getPath()).append("</i>").append("</a>");
          } else {
            sb.append(logEntryPath.getPath()).append("</a>");
          }
          break;
        case MODIFIED:
          // diffUrl
          String diffUrl = createDiffUrl(baseURL, logEntryPath.getPath(), revision, repositoryName, linkToHead);
          if (response != null) {
            diffUrl = response.encodeURL(diffUrl);
          }
          sb.append("<a href=\"").append(diffUrl);
          if (showLatestRevInfo) {
            sb.append("&showlatestrevinfo=true");
          }
          sb.append("\" title=\"Diff with previous version\">");
          if (logEntryPath.getPath().equals(pathAtRevision)) {
            sb.append("<i>").append(logEntryPath.getPath()).append("</i>").append("</a>");
          } else {
            sb.append(logEntryPath.getPath()).append("</a>");
          }
          break;
        case DELETED:
          // del
          goToUrl = createGoToUrl(baseURL, logEntryPath.getPath(), revision - 1, repositoryName, false);
          if (response != null) {
            goToUrl = response.encodeURL(goToUrl);
          }
          sb.append("<a href=\"").append(goToUrl);
          sb.append("\" title=\"Show previous revision\"><del>").append(logEntryPath.getPath()).append("</del></a>");
          break;
      }

      if (logEntryPath.getCopyPath() != null) {
        sb.append("<br><b>Copy from</b> ");
        goToUrl = createGoToUrl(baseURL, logEntryPath.getCopyPath(), logEntryPath.getCopyRevision(), repositoryName, false);
        if (response != null) {
          goToUrl = response.encodeURL(goToUrl);
        }
        sb.append("<a href=\"").append(goToUrl);
        if (showLatestRevInfo) {
          sb.append("&showlatestrevinfo=true");
        }
        sb.append("\" title=\"Show\">").append(logEntryPath.getCopyPath()).append("</a>").append(" @ ");
        String revInfoUrl = createRevInfoUrl(baseURL, logEntryPath.getCopyRevision(), repositoryName);
        if (response != null) {
          revInfoUrl = response.encodeURL(revInfoUrl);
        }
        sb.append("<a href=\"").append(revInfoUrl);
        sb.append("\">");
        sb.append(logEntryPath.getCopyRevision()).append("</a>");
      }
      sb.append("</td>\n");
      sb.append("  </tr>\n");
    }
    sb.append("</table>");
    return sb.toString();
  }

  /**
   * Creates a <i>goto</i> URL based on given parameters.
   *
   * @param baseURL        Base application URL
   * @param path           Path
   * @param revision       Revision
   * @param repositoryName Repository name.
   * @param linkToHead     If true, the navigation link will point to head.
   * @return The URL
   */
  protected static String createGoToUrl(final String baseURL, final String path, final long revision,
                                        final RepositoryName repositoryName, final boolean linkToHead) {

    final StringBuilder sb = new StringBuilder(baseURL);
    sb.append("repos/");
    sb.append(encode(repositoryName.toString()));
    sb.append("/goto");
    sb.append(path);
    sb.append("?revision=").append(linkToHead ? "HEAD" : revision);
    return sb.toString();
  }

  /**
   * Creates a <i>diff</i> URL based on given parameters.
   *
   * @param baseURL        Base application URL
   * @param path           Path
   * @param revision       Revision
   * @param repositoryName Repository name.
   * @param linkToHead     If true, the navigation link will point to head.
   * @return The URL
   */
  protected static String createDiffUrl(final String baseURL, final String path, final long revision,
                                        final RepositoryName repositoryName, final boolean linkToHead) {

    final String entry1 = path + ";;" + revision;
    final String entry2 = path + ";;" + (revision - 1);

    final StringBuilder sb = new StringBuilder(baseURL);
    sb.append("repos/");
    sb.append(encode(repositoryName.toString()));
    sb.append("/diff");
    sb.append(path);
    sb.append("?revision=").append(linkToHead ? "HEAD" : revision);
    sb.append("&entries=").append(entry1);
    sb.append("&entries=").append(entry2);
    return sb.toString();
  }

  /**
   * Creates a <i>revision info</i> URL based on given parameters.
   *
   * @param baseURL        Base application URL
   * @param revision       Revision
   * @param repositoryName Repository name.
   * @return The URL
   */
  protected static String createRevInfoUrl(final String baseURL, final long revision, final RepositoryName repositoryName) {
    final StringBuilder sb = new StringBuilder(baseURL);
    sb.append("repos/");
    sb.append(encode(repositoryName.toString()));
    sb.append("/revinfo");
    sb.append("?revision=").append(encode(String.valueOf(revision)));
    return sb.toString();
  }

}

