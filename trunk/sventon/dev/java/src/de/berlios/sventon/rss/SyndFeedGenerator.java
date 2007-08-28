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
package de.berlios.sventon.rss;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedOutput;
import de.berlios.sventon.model.LogEntryActionType;
import de.berlios.sventon.util.HTMLCreator;
import de.berlios.sventon.util.WebUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Class to generate <code>RSS</code> feeds from Subversion log information.
 * Uses the <a href="https://rome.dev.java.net/">ROME</code> library.
 *
 * @author jesper@users.berlios.de
 */
public final class SyndFeedGenerator implements FeedGenerator {

  /**
   * The generated feed type, default set to <tt>rss_2.0</tt>.
   */
  private String feedType = "rss_2.0";

  /**
   * Number of characters in the abbreviated log message, default set to 40.
   */
  private int logMessageLength = 40;

  /**
   * Logging instance.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  public static final String LOG_MESSAGE_KEY = "@LOG_MESSAGE@";
  public static final String ADDED_COUNT_KEY = "@ADDED_COUNT@";
  public static final String MODIFIED_COUNT_KEY = "@MODIFIED_COUNT@";
  public static final String REPLACED_COUNT_KEY = "@REPLACED_COUNT@";
  public static final String DELETED_COUNT_KEY = "@DELETED_COUNT@";
  public static final String CHANGED_PATHS_KEY = "@CHANGED_PATHS@";

  /**
   * Cached rss HTML body template.
   */
  private String bodyTemplate = null;

  /**
   * The rss body template file. Default set to <tt>rsstemplate.html</tt> in classpath root.
   */
  private String bodyTemplateFile = "/rsstemplate.html";

  /**
   * {@inheritDoc}
   */
  public void outputFeed(final String instanceName, final List<SVNLogEntry> logEntries,
                         final HttpServletRequest request, final HttpServletResponse response) throws Exception {

    final SyndFeed feed = new SyndFeedImpl();
    final String baseURL = WebUtils.extractBaseURLFromRequest(request);
    feed.setTitle("sventon feed - " + baseURL);
    feed.setLink(baseURL);
    feed.setDescription("sventon feed - " + logEntries.size() + " latest repository changes");
    feed.setEntries(createEntries(instanceName, logEntries, baseURL, response));
    feed.setFeedType(feedType);
    new SyndFeedOutput().output(feed, response.getWriter());
  }


  /**
   * Create the entries, one for each revision.
   *
   * @param instanceName Instance name
   * @param logEntries   List of log entries (revisions)
   * @param baseURL      Application base URL
   * @param response     Response
   * @return List of RSS feed items
   * @throws IOException if unable to produce feed items.
   */
  private List<SyndEntry> createEntries(final String instanceName, final List<SVNLogEntry> logEntries,
                                        final String baseURL, final HttpServletResponse response) throws IOException {

    final List<SyndEntry> entries = new ArrayList<SyndEntry>();

    SyndEntry entry;
    SyndContent description;

    logger.debug("Generating [" + logEntries.size() + "] RSS feed items for instance [" + instanceName + "]");

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
      description.setValue(createItemBody(getBodyTemplate(), logEntry, baseURL, instanceName, response));
      entry.setDescription(description);
      entries.add(entry);
    }
    return entries;
  }

  /**
   * Creates an RSS item body.
   *
   * @param bodyTemplate Body template string.
   * @param logEntry     log entry revision.
   * @param baseURL      Application base URL.
   * @param instanceName Instance name
   * @param response     Response
   * @return Result
   */
  protected String createItemBody(final String bodyTemplate, final SVNLogEntry logEntry, final String baseURL,
                                  final String instanceName, final HttpServletResponse response) {

    String itemBody = bodyTemplate;
    itemBody = addActionCounts(logEntry, itemBody);
    itemBody = addLogMessage(logEntry, itemBody);
    itemBody = addChangedPathsTable(logEntry, itemBody, baseURL, instanceName, response);
    return itemBody;
  }

  /**
   * Adds the changed paths table to the item body.
   *
   * @param logEntry     Log entry revision.
   * @param itemBody     String to add the table to.
   * @param baseURL      Application base URL
   * @param instanceName Instance name
   * @param response     Response
   * @return Result
   */
  private String addChangedPathsTable(final SVNLogEntry logEntry, final String itemBody, final String baseURL,
                                      final String instanceName, final HttpServletResponse response) {

    return itemBody.replaceAll(CHANGED_PATHS_KEY, Matcher.quoteReplacement(HTMLCreator.createChangedPathsTable(
        logEntry, baseURL, instanceName, false, false, response)));
  }

  /**
   * Adds the log message to the item body.
   *
   * @param logEntry Log entry revision.
   * @param itemBody String to add details to.
   * @return Result
   */
  protected String addLogMessage(final SVNLogEntry logEntry, final String itemBody) {
    return itemBody.replaceAll(LOG_MESSAGE_KEY, Matcher.quoteReplacement(WebUtils.nl2br(logEntry.getMessage())));
  }

  /**
   * Adds the count details to the item body.
   *
   * @param logEntry Log entry revision.
   * @param itemBody String to add details to.
   * @return Result
   */
  private String addActionCounts(final SVNLogEntry logEntry, final String itemBody) {
    String str = itemBody;

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
    str = str.replaceAll(ADDED_COUNT_KEY, Matcher.quoteReplacement(String.valueOf(added)));
    str = str.replaceAll(MODIFIED_COUNT_KEY, Matcher.quoteReplacement(String.valueOf(modified)));
    str = str.replaceAll(REPLACED_COUNT_KEY, Matcher.quoteReplacement(String.valueOf(replaced)));
    str = str.replaceAll(DELETED_COUNT_KEY, Matcher.quoteReplacement(String.valueOf(deleted)));
    return str;
  }

  /**
   * Gets the rss item HTML body template.
   *
   * @return The template.
   * @throws IOException if unable to load template.
   */
  protected String getBodyTemplate() throws IOException {
    if (bodyTemplate == null) {
      final StringBuilder sb = new StringBuilder();
      final InputStream is = this.getClass().getResourceAsStream(bodyTemplateFile);
      if (is == null) {
        throw new FileNotFoundException("Unable to find: " + bodyTemplateFile);
      }
      BufferedReader reader = null;
      try {
        reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
      } finally {
        if (reader != null) {
          reader.close();
        }
      }
      bodyTemplate = sb.toString();
    }
    return bodyTemplate;
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
   * Sets the file that should be used as the rss item body template.
   *
   * @param bodyTemplateFile Template file.
   */
  public void setBodyTemplateFile(final String bodyTemplateFile) {
    this.bodyTemplateFile = bodyTemplateFile;
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
