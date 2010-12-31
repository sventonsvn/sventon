/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
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
import org.sventon.model.ChangeType;
import org.sventon.model.ChangedPath;
import org.sventon.model.LogEntry;
import org.sventon.model.RepositoryName;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Matcher;

import static org.sventon.util.EncodingUtils.encode;

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
  public static String createRevisionDetailBody(final String bodyTemplate, final LogEntry logEntry, final String baseURL,
                                                final RepositoryName repositoryName, final DateFormat dateFormat,
                                                final HttpServletResponse response) {

    final Map<String, String> valueMap = new HashMap<String, String>();

    int added = 0;
    int modified = 0;
    int replaced = 0;
    int deleted = 0;

    //noinspection unchecked
    final SortedSet<ChangedPath> latestChangedPaths = logEntry.getChangedPaths();

    for (final ChangedPath entryPath : latestChangedPaths) {
      final ChangeType type = entryPath.getType();
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
        default:
          throw new IllegalArgumentException("Unsupported type: " + type);
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
  public static String createChangedPathsTable(final SortedSet<ChangedPath> changedPaths, final long revision,
                                               final String pathAtRevision, final String baseURL,
                                               final RepositoryName repositoryName, final boolean showLatestRevInfo,
                                               final boolean linkToHead, final HttpServletResponse response) {

    final StringBuilder sb = new StringBuilder("<table class=\"changedPathsTable\">\n");
    sb.append("  <tr>\n");
    sb.append("    <th align=\"left\">Action</th>\n");
    sb.append("    <th align=\"left\">Path</th>\n");
    sb.append("  </tr>\n");

    for (final ChangedPath path : changedPaths) {
      final ChangeType changeType = path.getType();

      sb.append("  <tr>\n");
      sb.append("    <td valign=\"top\"><i>").append(changeType).append("</i></td>\n");

      sb.append("    <td>");

      String goToUrl;
      switch (changeType) {
        case ADDED: // fall thru
        case REPLACED:
          // goToUrl
          goToUrl = createGoToUrl(baseURL, path.getPath(), revision, repositoryName, linkToHead);
          if (response != null) {
            goToUrl = response.encodeURL(goToUrl);
          }
          sb.append("<a href=\"").append(goToUrl);
          if (showLatestRevInfo) {
            sb.append("&showlatestrevinfo=true");
          }
          sb.append("\" title=\"Show\">");
          if (path.getPath().equals(pathAtRevision)) {
            sb.append("<i>").append(path.getPath()).append("</i>").append("</a>");
          } else {
            sb.append(path.getPath()).append("</a>");
          }
          break;
        case MODIFIED:
          // diffUrl
          String diffUrl = createDiffUrl(baseURL, path.getPath(), revision, repositoryName, linkToHead);
          if (response != null) {
            diffUrl = response.encodeURL(diffUrl);
          }
          sb.append("<a href=\"").append(diffUrl);
          if (showLatestRevInfo) {
            sb.append("&showlatestrevinfo=true");
          }
          sb.append("\" title=\"Diff with previous version\">");
          if (path.getPath().equals(pathAtRevision)) {
            sb.append("<i>").append(path.getPath()).append("</i>").append("</a>");
          } else {
            sb.append(path.getPath()).append("</a>");
          }
          break;
        case DELETED:
          // del
          goToUrl = createGoToUrl(baseURL, path.getPath(), revision - 1, repositoryName, false);
          if (response != null) {
            goToUrl = response.encodeURL(goToUrl);
          }
          sb.append("<a href=\"").append(goToUrl);
          sb.append("\" title=\"Show previous revision\"><del>").append(path.getPath()).append("</del></a>");
          break;
        default:
          throw new IllegalArgumentException("Unsupported type: " + changeType);
      }

      if (path.getCopyPath() != null) {
        sb.append("<br>(<i>Copy from</i> ");
        goToUrl = createGoToUrl(baseURL, path.getCopyPath(), path.getCopyRevision(), repositoryName, false);
        if (response != null) {
          goToUrl = response.encodeURL(goToUrl);
        }
        sb.append("<a href=\"").append(goToUrl);
        if (showLatestRevInfo) {
          sb.append("&showlatestrevinfo=true");
        }
        sb.append("\" title=\"Show\">").append(path.getCopyPath()).append("</a>").append(" @ ");
        String revisionInfoUrl = createRevisionInfoUrl(baseURL, path.getCopyRevision(), repositoryName);
        if (response != null) {
          revisionInfoUrl = response.encodeURL(revisionInfoUrl);
        }
        sb.append("<a href=\"").append(revisionInfoUrl);
        sb.append("\">");
        sb.append(path.getCopyRevision()).append("</a>)");
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

    final String entry1 = path + "@" + revision;
    final String entry2 = path + "@" + (revision - 1);

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
  protected static String createRevisionInfoUrl(final String baseURL, final long revision, final RepositoryName repositoryName) {
    final StringBuilder sb = new StringBuilder(baseURL);
    sb.append("repos/");
    sb.append(encode(repositoryName.toString()));
    sb.append("/info");
    sb.append("?revision=").append(encode(String.valueOf(revision)));
    return sb.toString();
  }

}

