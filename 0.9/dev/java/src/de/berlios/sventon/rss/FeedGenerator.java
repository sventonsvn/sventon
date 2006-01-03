package de.berlios.sventon.rss;

import com.sun.syndication.feed.synd.*;
import de.berlios.sventon.svnsupport.LogEntryActionType;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

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
   * Gets a feed for given log entries.
   *
   * @param logEntries The log entries
   * @param baseURL Base URL used to build anchor links. Must end with a slash (/).
   * @return The feed object
   */
  @SuppressWarnings({"unchecked"})
  public SyndFeed generateFeed(final List<SVNLogEntry> logEntries, final String baseURL) {
    SyndFeed feed = new SyndFeedImpl();

    feed.setTitle("sventon feed - " + baseURL);
    feed.setLink(baseURL);
    feed.setDescription("sventon feed - " + logEntries.size() + " latest repository changes");

    List<SyndEntry> entries = new ArrayList<SyndEntry>();

    SyndEntry entry;
    SyndContent description;

    // One logEntry is one commit (or revision)
    for (SVNLogEntry logEntry : logEntries) {
      entry = new SyndEntryImpl();
      entry.setTitle("Revision " + logEntry.getRevision());
      entry.setAuthor(logEntry.getAuthor());
      entry.setLink(baseURL + "repobrowser.svn?path=/&revision=" + logEntry.getRevision());
      entry.setPublishedDate(logEntry.getDate());

      description = new SyndContentImpl();
      description.setType("text/html");

      Map<String, SVNLogEntryPath> map = logEntry.getChangedPaths();
      List<String> latestPathsList = new ArrayList<String>(map.keySet());

      StringBuffer sb = new StringBuffer();
      sb.append("<table border=\"0\">");
      sb.append("<tr colspan=\"4\">");
      sb.append("<td>");
      sb.append(logEntry.getMessage());
      sb.append("</td>");
      sb.append("</tr>");
      sb.append("<tr>");
      sb.append("<th>Action</th>");
      sb.append("<th>Path</th>");
      sb.append("<th>Copy From Path</th>");
      sb.append("<th>Revision</th>");
      sb.append("</tr>");

      for (String entryPath : latestPathsList) {
        SVNLogEntryPath logEntryPath = map.get(entryPath);
        sb.append("<tr><td><i>");
        sb.append(LogEntryActionType.valueOf(String.valueOf(logEntryPath.getType())));
        sb.append("</i></td><td>");
        sb.append(logEntryPath.getPath());
        sb.append("</td><td>");
        sb.append(logEntryPath.getCopyPath() == null ? "&nbsp;" : logEntryPath.getCopyPath());
        sb.append("</td><td>");
        sb.append(logEntryPath.getCopyPath() == null ? "&nbsp;" : Long.toString(logEntryPath.getCopyRevision()));
        sb.append("</td></tr>");
      }

      sb.append("</table>");
      description.setValue(sb.toString());
      entry.setDescription(description);
      entries.add(entry);
    }

/*

        <tr>
          <c:url value="goto.svn" var="goToUrl">
            <c:param name="path" value="<%= logEntryPath.getPath() %>" />
            <c:param name="revision" value="head" />
          </c:url>

          <c:url value="diff.svn" var="diffUrl">
            <c:param name="path" value="<%= logEntryPath.getPath() %>" />
            <c:param name="revision" value="head" />
          </c:url>

          <td><i><%= actionType %></i></td>
          <% if (LogEntryActionType.A == actionType || LogEntryActionType.R == actionType) { %>
          <td><a href="${goToUrl}" title="Show file"><%= logEntryPath.getPath() %></a></td>
          <% } else if (LogEntryActionType.M == actionType) { %>
          <td><a href="${diffUrl}&rev=<%= logEntryPath.getPath() %>;;<%= latestCommitInfo.getRevision() %>&rev=<%= logEntryPath.getPath() %>;;<%= latestCommitInfo.getRevision() - 1 %>" title="Diff with previous version"><%= logEntryPath.getPath() %></a></td>
          <% } else { %>
          <td><%= logEntryPath.getPath() %></td>
          <% } %>
          <td><%= logEntryPath.getCopyPath() == null ? "" : logEntryPath.getCopyPath() %></td>
          <td><%= logEntryPath.getCopyPath() == null ? "" : Long.toString(logEntryPath.getCopyRevision()) %></td>
        </tr>
        <%
          }
        %>
        </table>
*/

    feed.setEntries(entries);
    return feed;
  }

}
