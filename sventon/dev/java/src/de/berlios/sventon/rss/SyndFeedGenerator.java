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
package de.berlios.sventon.rss;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedOutput;
import de.berlios.sventon.web.model.LogEntryActionType;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.Writer;

/**
 * Class to generate <code>RSS</code> feeds from Subversion log information.
 * Uses the <a href="https://rome.dev.java.net/">ROME</code> library.
 *
 * @author jesper@users.berlios.de
 */
public class SyndFeedGenerator implements FeedGenerator {

  /**
   * The generated feed type, default set to <tt>rss_2.0</tt>.
   */
  private String feedType = "rss_2.0";

  /**
   * Number of characters in the abbreviated log message, default set to 40.
   */
  private int logMessageLength = 40;

  /**
   * {@inheritDoc}
   */
  public void outputFeed(final String instanceName, final List<SVNLogEntry> logEntries, final String baseURL,
                         final Writer writer) throws Exception {

    final SyndFeed feed = new SyndFeedImpl();
    feed.setTitle("sventon feed - " + baseURL);
    feed.setLink(baseURL);
    feed.setDescription("sventon feed - " + logEntries.size() + " latest repository changes");
    feed.setEntries(createEntries(instanceName, logEntries, baseURL));
    feed.setFeedType(feedType);
    new SyndFeedOutput().output(feed, writer);
  }

  private List createEntries(final String instanceName, final List<SVNLogEntry> logEntries, final String baseURL) {
    final List<SyndEntry> entries = new ArrayList<SyndEntry>();

    SyndEntry entry;
    SyndContent description;

    // One logEntry is one commit (or revision)
    for (final SVNLogEntry logEntry : logEntries) {
      entry = new SyndEntryImpl();
      entry.setTitle("Revision " + logEntry.getRevision() + " - "
          + getAbbreviatedLogMessage(logEntry.getMessage(), logMessageLength));
      entry.setAuthor(logEntry.getAuthor());
      entry.setLink(baseURL + "revinfo.svn?name=" + instanceName + "&revision=" + logEntry.getRevision());
      entry.setPublishedDate(logEntry.getDate());

      description = new SyndContentImpl();
      description.setType("text/html");

      //noinspection unchecked
      final Map<String, SVNLogEntryPath> map = logEntry.getChangedPaths();
      final List<String> latestPathsList = new ArrayList<String>(map.keySet());

      int added = 0;
      int modified = 0;
      int relocated = 0;
      int deleted = 0;

      for (final String entryPath : latestPathsList) {
        final LogEntryActionType type = LogEntryActionType.parse(map.get(entryPath).getType());
        switch (type) {
          case ADDED:
            added++;
            break;
          case MODIFIED:
            modified++;
            break;
          case REPLACED:
            relocated++;
            break;
          case DELETED:
            deleted++;
            break;
        }
      }

      final StringBuilder sb = new StringBuilder();
      sb.append("<table border=\"0\">");
      sb.append("<tr colspan=\"2\">");
      sb.append("<td>");
      sb.append(logEntry.getMessage());
      sb.append("</td>");
      sb.append("</tr>");
      sb.append("<tr>");
      sb.append("<td><b>Action</b></td>");
      sb.append("<td><b>Count</b></td>");

      sb.append("<tr><td>");
      sb.append(LogEntryActionType.ADDED);
      sb.append("</td><td>");
      sb.append(added);
      sb.append("</td></tr>");

      sb.append("<tr><td>");
      sb.append(LogEntryActionType.MODIFIED);
      sb.append("</td><td>");
      sb.append(modified);
      sb.append("</td></tr>");

      sb.append("<tr><td>");
      sb.append(LogEntryActionType.REPLACED);
      sb.append("</td><td>");
      sb.append(relocated);
      sb.append("</td></tr>");

      sb.append("<tr><td>");
      sb.append(LogEntryActionType.DELETED);
      sb.append("</td><td>");
      sb.append(deleted);
      sb.append("</td></tr>");

      sb.append("</table>");

      description.setValue(sb.toString());
      entry.setDescription(description);
      entries.add(entry);
    }
    return entries;
  }

  /**
   * Gets the abbreviated version of given log message.
   *
   * @param message The original log message
   * @param length  Length, shortened string length
   * @return The abbreviated log message
   */
  protected String getAbbreviatedLogMessage(final String message, final int length) {
    if (message != null && message.length() <= length) {
      return message;
    } else {
      return StringUtils.abbreviate(message, length);
    }

  }

  /**
   * Sets the feed type. For available types check ROME documentation.
   *
   * @param feedType The feed type.
   * @link https://rome.dev.java.net/
   */
  public void setFeedType(final String feedType) {
    this.feedType = feedType;
  }

  /**
   * Sets the length of the log message to be displayed.
   *
   * @param length The length.
   */
  public void setLogMessageLength(final int length) {
    this.logMessageLength = length;
  }
}
