/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.rss;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedOutput;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.LogEntry;
import org.sventon.model.RepositoryName;
import org.sventon.util.HTMLCreator;
import org.sventon.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to generate <code>RSS</code> feeds from Subversion log information.
 * Uses the <a href="https://rome.dev.java.net/">ROME</a> library.
 *
 * @author jesper@sventon.org
 */
public final class DefaultRssFeedGenerator implements RssFeedGenerator {

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
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * The date formatter instance.
   */
  private DateFormat dateFormat;

  @Override
  public void outputFeed(final RepositoryConfiguration configuration, final List<LogEntry> logEntries,
                         final HttpServletRequest request, final HttpServletResponse response) throws Exception {

    final SyndFeed feed = new SyndFeedImpl();
    final String baseURL = WebUtils.extractBaseURLFromRequest(request);
    final RepositoryName repositoryName = configuration.getName();
    feed.setTitle(repositoryName + " sventon feed - " + baseURL);
    feed.setLink(baseURL);
    feed.setDescription("sventon feed for " + repositoryName + " - " + logEntries.size() + " latest repository changes");
    feed.setEntries(createEntries(configuration, logEntries, baseURL, response));
    feed.setFeedType(feedType);
    new SyndFeedOutput().output(feed, response.getWriter());
  }


  /**
   * Create the entries, one for each revision.
   *
   * @param configuration Repository name.
   * @param logEntries    List of log entries (revisions)
   * @param baseURL       Application base URL
   * @param response      Response
   * @return List of RSS feed items
   * @throws IOException if unable to produce feed items.
   */
  private List<SyndEntry> createEntries(final RepositoryConfiguration configuration, final List<LogEntry> logEntries,
                                        final String baseURL, final HttpServletResponse response) throws IOException {

    final List<SyndEntry> entries = new ArrayList<SyndEntry>();
    final RepositoryName repositoryName = configuration.getName();

    SyndEntry entry;
    SyndContent description;

    logger.debug("Generating [" + logEntries.size() + "] RSS feed items for repository [" + repositoryName + "]");

    // One logEntry is one commit (or revision)
    for (final LogEntry logEntry : logEntries) {
      if (logEntry.isAccessible()) {
        entry = new SyndEntryImpl();
        entry.setTitle("Revision " + logEntry.getRevision() + " - " + StringUtils.trimToEmpty(getAbbreviatedLogMessage(
            StringEscapeUtils.escapeHtml(logEntry.getMessage()), logMessageLength)));
        entry.setAuthor(logEntry.getAuthor());
        entry.setLink(baseURL + "repos/" + repositoryName + "/info?revision=" + logEntry.getRevision());
        entry.setPublishedDate(logEntry.getDate());

        description = new SyndContentImpl();
        description.setType("text/html");
        description.setValue(HTMLCreator.createRevisionDetailBody(configuration.getRssTemplate(), logEntry, baseURL,
            configuration.getName(), dateFormat, response));
        entry.setDescription(description);
        entries.add(entry);
      }
    }
    return entries;
  }

  /**
   * Sets the date format.
   *
   * @param dateFormat Date format.
   */
  public void setDateFormat(final String dateFormat) {
    this.dateFormat = new SimpleDateFormat(dateFormat);
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
