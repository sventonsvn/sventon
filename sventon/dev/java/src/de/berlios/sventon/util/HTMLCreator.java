/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.util;

import de.berlios.sventon.model.LogEntryActionType;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Class responsible for creating HTML formatted content.
 *
 * @author jesper@users.berlios.de
 */
public final class HTMLCreator {

  private static final String DEFAULT_ENCODING = "UTF-8";

  private static final String GOTO_URL = "goto.svn";
  private static final String REV_INFO_URL = "revinfo.svn";
  private static final String DIFF_URL = "diff.svn";

  /**
   * Prevent instantiation.
   */
  private HTMLCreator() {
  }

  /**
   * Encodes given string using default encoding (UTF-8).
   *
   * @param str String to encode.
   * @return Encoded string.
   */
  protected static String encode(final String str) {
    String s = "";
    try {
      s = URLEncoder.encode(str, DEFAULT_ENCODING);
    } catch (UnsupportedEncodingException e) {
      // ignore
    }
    return s;
  }

  /**
   * Creates a HTML table containing the changed paths for given revision.
   *
   * @param logEntry          Log entry revision
   * @param baseURL           Base application URL.
   * @param instanceName      Instance name
   * @param showLatestRevInfo If true, the latest revision details DIV will be displayed.
   * @param linkToHead        If true, navigation links will be pointing at HEAD if applicable.
   * @param response          The HTTP response, used to encode the session parameter to the generated URLs.
   * @return The HTML table.
   */
  public static String createChangedPathsTable(final SVNLogEntry logEntry, final String baseURL,
                                               final String instanceName, final boolean showLatestRevInfo,
                                               final boolean linkToHead, final HttpServletResponse response) {

    final StringBuilder sb = new StringBuilder("<table border=\"0\">\n");
    sb.append("  <tr>\n");
    sb.append("    <th>Action</th>\n");
    sb.append("    <th>Path</th>\n");
    sb.append("  </tr>\n");

    //noinspection unchecked
    final Map<String, SVNLogEntryPath> latestChangedPaths = logEntry.getChangedPaths();
    final List<String> latestPathsList = new ArrayList<String>(latestChangedPaths.keySet());
    Collections.sort(latestPathsList);

    for (final String path : latestPathsList) {
      final SVNLogEntryPath logEntryPath = latestChangedPaths.get(path);
      final LogEntryActionType actionType = LogEntryActionType.parse(logEntryPath.getType());

      sb.append("  <tr>\n");
      sb.append("    <td valign=\"top\"><i>").append(actionType).append("</i></td>\n");

      sb.append("    <td>");

      switch (actionType) {
        case ADDED: // fall thru
        case REPLACED:
          // goToUrl
          sb.append("<a href=\"").append(response.encodeURL(createGoToUrl(
              baseURL, logEntryPath.getPath(), logEntry.getRevision(), instanceName, linkToHead)));
          if (showLatestRevInfo) {
            sb.append("&showlatestrevinfo=true");
          }
          sb.append("\" title=\"Show\">").append(logEntryPath.getPath()).append("</a>");
          break;
        case MODIFIED:
          // diffUrl
          sb.append("<a href=\"").append(response.encodeURL(createDiffUrl(
              baseURL, logEntryPath.getPath(), logEntry.getRevision(), instanceName, linkToHead)));
          if (showLatestRevInfo) {
            sb.append("&showlatestrevinfo=true");
          }
          sb.append("\" title=\"Diff with previous version\">").append(logEntryPath.getPath()).append("</a>");
          break;
        case DELETED:
          // strike
          sb.append("<strike>").append(logEntryPath.getPath()).append("</strike>");
          break;
      }

      if (logEntryPath.getCopyPath() != null) {
        sb.append("<br/><b>Copy from</b> ");
        sb.append("<a href=\"").append(response.encodeURL(createGoToUrl(
            baseURL, logEntryPath.getCopyPath(), logEntryPath.getCopyRevision(), instanceName, linkToHead)));
        if (showLatestRevInfo) {
          sb.append("&showlatestrevinfo=true");
        }
        sb.append("\" title=\"Show\">").append(logEntryPath.getCopyPath()).append("</a>").append(" @ ");
        sb.append("<a href=\"").append(response.encodeURL(createRevInfoUrl(
            baseURL, logEntryPath.getCopyRevision(), instanceName)));
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
   * @param baseURL      Base application URL
   * @param path         Path
   * @param revision     Revision
   * @param instanceName Instance name
   * @param linkToHead   If true, the navigation link will point to head.
   * @return The URL
   */
  protected static String createGoToUrl(final String baseURL, final String path, final long revision,
                                        final String instanceName, final boolean linkToHead) {

    final StringBuilder sb = new StringBuilder(baseURL);
    sb.append(GOTO_URL);
    sb.append("?path=").append(encode(path));
    sb.append("&revision=").append(linkToHead ? "head" : revision);
    sb.append("&name=").append(encode(instanceName));
    return sb.toString();
  }

  /**
   * Creates a <i>diff</i> URL based on given parameters.
   *
   * @param baseURL      Base application URL
   * @param path         Path
   * @param revision     Revision
   * @param instanceName Instance name
   * @param linkToHead   If true, the navigation link will point to head.
   * @return The URL
   */
  protected static String createDiffUrl(final String baseURL, final String path, final long revision,
                                        final String instanceName, final boolean linkToHead) {

    final String entry1 = path + ";;" + revision;
    final String entry2 = path + ";;" + (revision - 1);

    final StringBuilder sb = new StringBuilder(baseURL);
    sb.append(DIFF_URL);
    sb.append("?path=").append(encode(path));
    sb.append("&revision=").append(linkToHead ? "head" : revision);
    sb.append("&name=").append(encode(instanceName));
    sb.append("&entry=").append(encode(entry1));
    sb.append("&entry=").append(encode(entry2));
    return sb.toString();
  }

  /**
   * Creates a <i>revision info</i> URL based on given parameters.
   *
   * @param baseURL      Base application URL
   * @param revision     Revision
   * @param instanceName Instance name
   * @return The URL
   */
  protected static String createRevInfoUrl(final String baseURL, final long revision, final String instanceName) {
    final StringBuilder sb = new StringBuilder(baseURL);
    sb.append(REV_INFO_URL);
    sb.append("?revision=").append(encode(String.valueOf(revision)));
    sb.append("&name=").append(encode(instanceName));
    return sb.toString();
  }

}

