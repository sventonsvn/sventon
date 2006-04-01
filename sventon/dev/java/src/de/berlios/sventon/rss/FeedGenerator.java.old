package de.berlios.sventon.rss;

import com.sun.syndication.feed.synd.*;
import de.berlios.sventon.ctrl.LogEntryActionType;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to generate RSS feeds from Subversion log information.
 * Uses the ROME library (https://rome.dev.java.net/)
 *
 * @author jesper@users.berlios.de
 */
public class FeedGenerator {

  /**
   * Number of characters in the abbreviated commit message, default set to 40.
   */
  private static final int SHORT_COMMIT_MESSAGE_LENGTH = 40;

  /**
   * Gets a feed for given log entries.
   *
   * @param logEntries The log entries
   * @param baseURL Base URL used to build anchor links. Must end with a slash (/).
   * @return The feed object
   */
  @SuppressWarnings({"unchecked"})
  public SyndFeed generateFeed(final List<SVNLogEntry> logEntries, final String baseURL) {
    final SyndFeed feed = new SyndFeedImpl();

    feed.setTitle("sventon feed - " + baseURL);
    feed.setLink(baseURL);
    feed.setDescription("sventon feed - " + logEntries.size() + " latest repository changes");

    final List<SyndEntry> entries = new ArrayList<SyndEntry>();

    SyndEntry entry;
    SyndContent description;

    // One logEntry is one commit (or revision)
    for (SVNLogEntry logEntry : logEntries) {
      entry = new SyndEntryImpl();
      entry.setTitle("Revision " + logEntry.getRevision() + " - "
          + getAbbreviatedCommitMessage(logEntry.getMessage(), SHORT_COMMIT_MESSAGE_LENGTH));
      entry.setAuthor(logEntry.getAuthor());
      entry.setLink(baseURL + "revinfo.svn?&rev=" + logEntry.getRevision());
      entry.setPublishedDate(logEntry.getDate());

      description = new SyndContentImpl();
      description.setType("text/html");

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
    feed.setEntries(entries);
    return feed;
  }

  /**
   * Gets the abbreviated version of given commit message.
   *
   * @param message The original commit message
   * @param length Length, shortened string length
   * @return The abbreviated commit message
   */
  protected String getAbbreviatedCommitMessage(final String message, final int length) {
    if (message != null && message.length() <= length) {
      return message;
    } else {
      return StringUtils.abbreviate(message, length);
    }

  }
}
