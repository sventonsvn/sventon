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
import de.berlios.sventon.ctrl.LogEntryActionType;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.Writer;

/**
 * Class to generate RSS feeds from Subversion log information.
 * Uses the ROME library (https://rome.dev.java.net/)
 *
 * @author jesper@users.berlios.de
 */
public class SyndFeedGenerator implements FeedGenerator {

  /**
   * The feed.
   */
  private SyndFeed cachedFeed;

  /**
   * The generated feed type, default set to <tt>rss_2.0</tt>.
   */
  private String feedType = "rss_2.0";

  /**
   * Number of characters in the abbreviated commit message, default set to 40.
   */
  private int commitMessageLength = 40;

  /**
   * {@inheritDoc}
   */
  public void generateFeed(final List<SVNLogEntry> logEntries, final String baseURL) {
    final SyndFeed feed = new SyndFeedImpl();
    feed.setTitle("sventon feed - " + baseURL);
    feed.setLink(baseURL);
    feed.setDescription("sventon feed - " + logEntries.size() + " latest repository changes");
    feed.setEntries(createEntries(logEntries, baseURL));
    feed.setFeedType(feedType);
    cachedFeed = feed;
  }

  /**
   * {@inheritDoc}
   */
  public void outputFeed(final Writer writer) throws Exception {
    new SyndFeedOutput().output(cachedFeed, writer);
  }

  private List createEntries(final List<SVNLogEntry> logEntries, final String baseURL) {
    final List<SyndEntry> entries = new ArrayList<SyndEntry>();

    SyndEntry entry;
    SyndContent description;

    // One logEntry is one commit (or revision)
    for (SVNLogEntry logEntry : logEntries) {
      entry = new SyndEntryImpl();
      entry.setTitle("Revision " + logEntry.getRevision() + " - "
          + getAbbreviatedCommitMessage(logEntry.getMessage(), commitMessageLength));
      entry.setAuthor(logEntry.getAuthor());
      entry.setLink(baseURL + "revinfo.svn?&revision=" + logEntry.getRevision());
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

      for (String entryPath : latestPathsList) {
        final LogEntryActionType type = LogEntryActionType.valueOf(String.valueOf(map.get(entryPath).getType()));
        switch (type) {
          case A:
            added++;
            break;
          case M:
            modified++;
            break;
          case R:
            relocated++;
            break;
          case D:
            deleted++;
            break;
        }
      }

      final StringBuffer sb = new StringBuffer();
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
      sb.append(LogEntryActionType.A);
      sb.append("</td><td>");
      sb.append(added);
      sb.append("</td></tr>");

      sb.append("<tr><td>");
      sb.append(LogEntryActionType.M);
      sb.append("</td><td>");
      sb.append(modified);
      sb.append("</td></tr>");

      sb.append("<tr><td>");
      sb.append(LogEntryActionType.R);
      sb.append("</td><td>");
      sb.append(relocated);
      sb.append("</td></tr>");

      sb.append("<tr><td>");
      sb.append(LogEntryActionType.D);
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
   * Gets the abbreviated version of given commit message.
   *
   * @param message The original commit message
   * @param length  Length, shortened string length
   * @return The abbreviated commit message
   */
  protected String getAbbreviatedCommitMessage(final String message, final int length) {
    if (message != null && message.length() <= length) {
      return message;
    } else {
      return StringUtils.abbreviate(message, length);
    }

  }

  /**
   * Sets the feed type. For available types check ROME documentation.
   *
   * @param feedType
   * @link https://rome.dev.java.net/
   */
  public void setFeedType(final String feedType) {
    this.feedType = feedType;
  }

  /**
   * Sets the length of the commit message to be displayed.
   *
   * @param length The length.
   */
  public void setCommitMessageLength(final int length) {
    this.commitMessageLength = length;
  }
}
