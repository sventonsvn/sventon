/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.rss;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedOutput;
import de.berlios.sventon.util.HTMLCreator;
import de.berlios.sventon.util.WebUtils;
import de.berlios.sventon.web.support.SVNUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNLogEntry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to generate <code>RSS</code> feeds from Subversion log information.
 * Uses the <a href="https://rome.dev.java.net/">ROME</a> library.
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

  /**
   * Cached rss HTML body template.
   */
  private String bodyTemplate = null;

  /**
   * The rss body template file. Default set to <tt>rsstemplate.html</tt> in classpath root.
   */
  private String bodyTemplateFile = "/rsstemplate.html";

  /**
   * The date formatter instance.
   */
  private DateFormat dateFormat;

  /**
   * {@inheritDoc}
   */
  public void outputFeed(final String instanceName, final List<SVNLogEntry> logEntries,
                         final HttpServletRequest request, final HttpServletResponse response) throws Exception {

    final SyndFeed feed = new SyndFeedImpl();
    final String baseURL = WebUtils.extractBaseURLFromRequest(request);
    feed.setTitle(instanceName + " sventon feed - " + baseURL);
    feed.setLink(baseURL);
    feed.setDescription("sventon feed for " + instanceName + " - " + logEntries.size() + " latest repository changes");
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
      if (SVNUtils.isAccessible(logEntry)) {
        entry = new SyndEntryImpl();
        entry.setTitle("Revision " + logEntry.getRevision() + " - " + StringUtils.trimToEmpty(getAbbreviatedLogMessage(
            StringEscapeUtils.escapeHtml(logEntry.getMessage()), logMessageLength)));
        entry.setAuthor(logEntry.getAuthor());
        entry.setLink(baseURL + "revinfo.svn?name=" + instanceName + "&revision=" + logEntry.getRevision());
        entry.setPublishedDate(logEntry.getDate());

        description = new SyndContentImpl();
        description.setType("text/html");
        description.setValue(HTMLCreator.createRevisionDetailBody(
            getBodyTemplate(), logEntry, baseURL, instanceName, dateFormat, response));
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
   * Gets the rss item HTML body template.
   *
   * @return The template.
   * @throws IOException if unable to load template.
   */
  protected String getBodyTemplate() throws IOException {
    if (bodyTemplate == null) {
      final InputStream is = this.getClass().getResourceAsStream(bodyTemplateFile);
      if (is == null) {
        throw new FileNotFoundException("Unable to find: " + bodyTemplateFile);
      }
      bodyTemplate = IOUtils.toString(is);
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
