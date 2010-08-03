/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.appl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.SVNException;
import org.sventon.SVNURL;
import org.sventon.model.Credentials;
import org.sventon.model.RepositoryName;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Sventon application configuration class holding configuration parameters
 * and repository connection information for a single configured repositories.
 *
 * @author patrik@sventon.org
 * @author jesper@sventon.org
 */
public final class RepositoryConfiguration {

  /**
   * Default number of RSS feed items, default set to 20.
   */
  public static final int DEFAULT_RSS_ITEMS_COUNT = 20;

  /**
   * Default template, /rsstemplate.html
   */
  public static final String DEFAULT_RSS_TEMPLATE_FILE = "/rsstemplate.html";

  /**
   * Default template, /mailtemplate.html
   */
  public static final String DEFAULT_MAIL_TEMPLATE_FILE = "/mailtemplate.html";

  public static final String PROPERTY_KEY_REPOSITORY_URL = "repositoryRootUrl";
  public static final String PROPERTY_KEY_REPOSITORY_DISPLAY_URL = "repositoryDisplayRootUrl";
  public static final String PROPERTY_KEY_USER_NAME = "userName";
  public static final String PROPERTY_KEY_USER_PASSWORD = "userPassword";

  public static final String PROPERTY_KEY_USE_CACHE = "useCache";
  public static final String PROPERTY_KEY_CACHE_USER_NAME = "cacheUserName";
  public static final String PROPERTY_KEY_CACHE_USER_PASSWORD = "cacheUserPassword";

  public static final String PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS = "allowZipDownloads";
  public static final String PROPERTY_KEY_ENABLE_ACCESS_CONTROL = "enableAccessControl";
  public static final String PROPERTY_KEY_ENABLE_ISSUE_TRACKER_INTEGRATION = "enableIssueTrackerIntegration";
  public static final String PROPERTY_KEY_RSS_ITEMS_COUNT = "rssItemsCount";
  public static final String PROPERTY_KEY_RSS_TEMPLATE_FILE = "rssTemplateFile";
  public static final String PROPERTY_KEY_MAIL_TEMPLATE_FILE = "mailTemplateFile";
  public static final String PROPERTY_KEY_ENABLE_ENTRY_TRAY = "enableEntryTray";

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Repository name.
   */
  private final RepositoryName repositoryName;

  /**
   * The repository URL.
   */
  private String repositoryUrl;

  /**
   * The repository URL displayed to the user.
   */
  private String repositoryDisplayUrl;

  /**
   * The repository location.
   */
  private SVNURL svnUrl;

  /**
   * If a global user is configured for repository browsing, the credentials property should be set.
   */
  private Credentials userCredentials;

  /**
   * Credentials used by the cache to access the repository.
   */
  private Credentials cacheCredentials;

  /**
   * Decides whether the caching feature will be used.
   */
  private boolean useCache;

  /**
   * Decides whether <i>download as zip</i> is allowed.
   */
  private boolean zipDownloadsAllowed;

  /**
   * Decides whether repository access control is enforced (this is configured on the
   * SVN server).
   */
  private boolean enableAccessControl;

  /**
   * Decides whether the <i>entry tray</i> is enabled.
   */
  private boolean enableEntryTray = true;

  /**
   * Decides whether the issue tracker integration should be used.
   */
  private boolean enableIssueTrackerIntegration;

  /**
   * Number of items in the generated RSS feed, default set to {@link #DEFAULT_RSS_ITEMS_COUNT}.
   */
  private int rssItemsCount = DEFAULT_RSS_ITEMS_COUNT;

  /**
   * RSS template file, default set to {@link #DEFAULT_RSS_TEMPLATE_FILE}.
   */
  private String rssTemplateFile = DEFAULT_RSS_TEMPLATE_FILE;

  /**
   * RSS template string.
   */
  private String rssTemplate;

  /**
   * Mail template file, default set to {@link #DEFAULT_MAIL_TEMPLATE_FILE}.
   */
  private String mailTemplateFile = DEFAULT_MAIL_TEMPLATE_FILE;

  /**
   * Mail template string.
   */
  private String mailTemplate;

  /**
   * Flag indicating if configuration is persisted or not.
   */
  private boolean persisted;

  /**
   * Constructor.
   *
   * @param repositoryName Repository name.
   */
  public RepositoryConfiguration(final String repositoryName) {
    Validate.isTrue(RepositoryName.isValid(repositoryName), "The name must not contain whitespace");
    this.repositoryName = new RepositoryName(repositoryName);
  }

  /**
   * Creates a repository configuration using given name and properties.
   *
   * @param repositoryName Repository name
   * @param properties     Properties
   * @return The RepositoryConfiguration
   */
  public static RepositoryConfiguration create(final String repositoryName, final Properties properties) {
    final RepositoryConfiguration ic = new RepositoryConfiguration(repositoryName);
    ic.setRepositoryUrl((String) properties.get(PROPERTY_KEY_REPOSITORY_URL));
    ic.setRepositoryDisplayUrl((String) properties.get(PROPERTY_KEY_REPOSITORY_DISPLAY_URL));
    ic.setUserCredentials(new Credentials((String) properties.get(PROPERTY_KEY_USER_NAME), (String) properties.get(PROPERTY_KEY_USER_PASSWORD)));
    ic.setCacheCredentials(new Credentials((String) properties.get(PROPERTY_KEY_CACHE_USER_NAME), (String) properties.get(PROPERTY_KEY_CACHE_USER_PASSWORD)));
    ic.setCacheUsed(Boolean.parseBoolean((String) properties.get(PROPERTY_KEY_USE_CACHE)));
    ic.setZippedDownloadsAllowed(Boolean.parseBoolean((String) properties.get(PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS)));
    ic.setEnableAccessControl(Boolean.parseBoolean((String) properties.get(PROPERTY_KEY_ENABLE_ACCESS_CONTROL)));
    ic.setEnableIssueTrackerIntegration(Boolean.parseBoolean((String) properties.get(PROPERTY_KEY_ENABLE_ISSUE_TRACKER_INTEGRATION)));
    ic.setRssItemsCount(Integer.parseInt(properties.getProperty(PROPERTY_KEY_RSS_ITEMS_COUNT, String.valueOf(DEFAULT_RSS_ITEMS_COUNT))));

    if (properties.get(PROPERTY_KEY_ENABLE_ENTRY_TRAY) != null) {
      ic.setEnableEntryTray(Boolean.parseBoolean((String) properties.get(PROPERTY_KEY_ENABLE_ENTRY_TRAY)));
    }

    if (properties.get(PROPERTY_KEY_RSS_TEMPLATE_FILE) != null) {
      ic.setRssTemplateFile((String) properties.get(PROPERTY_KEY_RSS_TEMPLATE_FILE));
    }

    if (properties.get(PROPERTY_KEY_MAIL_TEMPLATE_FILE) != null) {
      ic.setMailTemplateFile((String) properties.get(PROPERTY_KEY_MAIL_TEMPLATE_FILE));
    }
    return ic;
  }

  /**
   * Gets the configuration as a properties instance.
   *
   * @return Populated properties instance.
   */
  public Properties getAsProperties() {
    final Properties properties = new Properties();
    properties.put(PROPERTY_KEY_REPOSITORY_URL, getRepositoryUrl());
    properties.put(PROPERTY_KEY_REPOSITORY_DISPLAY_URL, getRepositoryDisplayUrl());
    if (userCredentials != null) {
      properties.put(PROPERTY_KEY_USER_NAME, userCredentials.getUserName());
      properties.put(PROPERTY_KEY_USER_PASSWORD, userCredentials.getPassword());
    }
    if (cacheCredentials != null) {
      properties.put(PROPERTY_KEY_CACHE_USER_NAME, cacheCredentials.getUserName());
      properties.put(PROPERTY_KEY_CACHE_USER_PASSWORD, cacheCredentials.getPassword());
    }
    properties.put(PROPERTY_KEY_USE_CACHE, isCacheUsed() ? "true" : "false");
    properties.put(PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS, isZippedDownloadsAllowed() ? "true" : "false");
    properties.put(PROPERTY_KEY_ENABLE_ACCESS_CONTROL, isAccessControlEnabled() ? "true" : "false");
    properties.put(PROPERTY_KEY_ENABLE_ENTRY_TRAY, isEntryTrayEnabled() ? "true" : "false");
    properties.put(PROPERTY_KEY_ENABLE_ISSUE_TRACKER_INTEGRATION, isIssueTrackerIntegrationEnabled() ? "true" : "false");
    properties.put(PROPERTY_KEY_RSS_ITEMS_COUNT, String.valueOf(getRssItemsCount()));
    properties.put(PROPERTY_KEY_RSS_TEMPLATE_FILE, getRssTemplateFile());
    properties.put(PROPERTY_KEY_MAIL_TEMPLATE_FILE, getMailTemplateFile());
    return properties;
  }

  /**
   * Gets the configured user credentials, if any.
   * The credentials will be used for repository access.
   *
   * @return Returns the credentials, or null.
   */
  public Credentials getUserCredentials() {
    return userCredentials;
  }

  /**
   * Sets the configured user credentials.
   * The credentials will be used for repository access.
   *
   * @param userCredentials Credentials if any
   */
  public void setUserCredentials(final Credentials userCredentials) {
    this.userCredentials = userCredentials;
  }


  /**
   * Gets the configured cache credentials, if any.
   * The credentials will be used by the cache for repository access.
   *
   * @return Returns the credentials, or null.
   */
  public Credentials getCacheCredentials() {
    return cacheCredentials;
  }

  /**
   * Sets the credentials used by the cache to connect to the repository.
   *
   * @param cacheCredentials Credentials used by the cache.
   */
  public void setCacheCredentials(final Credentials cacheCredentials) {
    this.cacheCredentials = cacheCredentials;
  }

  /**
   * Get the configured repository URL.
   *
   * @return Returns the repository url.
   */
  public String getRepositoryUrl() {
    return repositoryUrl;
  }

  /**
   * Sets the repository root URL. Trailing slashes will be trimmed.
   *
   * @param repositoryUrl The root url.
   */
  public void setRepositoryUrl(String repositoryUrl) {
    Validate.notEmpty(repositoryUrl, "Repository URL cannot be empty");
    repositoryUrl = repositoryUrl.trim();

    // Strip last slash if any.
    if (repositoryUrl.endsWith("/")) {
      repositoryUrl = repositoryUrl.substring(0, repositoryUrl.length() - 1);
    }

    this.repositoryUrl = repositoryUrl;

    try {
      svnUrl = SVNURL.parse(this.repositoryUrl);
    } catch (final SVNException ex) {
      logger.warn("Unable to parse URL [" + repositoryUrl + "]", ex);
    }
  }

  /**
   * Sets the repository URL displayed to the user.
   *
   * @param repositoryDisplayUrl Display URL.
   */
  public void setRepositoryDisplayUrl(final String repositoryDisplayUrl) {
    this.repositoryDisplayUrl = repositoryDisplayUrl;
  }

  /**
   * Gets the repository URL displayed to the user.
   *
   * @return Display URL, never <tt>null</tt>. If no display URL is set, the
   *         repository URL will be returned instead.
   */
  public String getRepositoryDisplayUrl() {
    return repositoryDisplayUrl != null ? repositoryDisplayUrl : repositoryUrl;
  }

  /**
   * Get the SVNURL, this is the typed version of the URL set using method
   * {@link #setRepositoryUrl(String)}.
   *
   * @return Returns the location.
   */
  public SVNURL getSVNURL() {
    return svnUrl;
  }

  /**
   * Sets the 'useCache' flag.
   *
   * @param useCache <code>true</code> if cache should be enabled, <code>false</code> if not.
   */
  public void setCacheUsed(final boolean useCache) {
    this.useCache = useCache;
  }

  /**
   * Checks if the cache should be used.
   *
   * @return <code>true</code> if cache is enabled, <code>false</code> if not.
   */
  public boolean isCacheUsed() {
    return this.useCache;
  }

  /**
   * Checks if repository access control is enabled.
   *
   * @return {@code true} if access control is enabled.
   */
  public boolean isAccessControlEnabled() {
    return enableAccessControl;
  }

  /**
   * Checks if the entry tray is enabled.
   *
   * @return {@code true} if entry tray is enabled.
   */
  public boolean isEntryTrayEnabled() {
    return enableEntryTray;
  }

  /**
   * Sets the 'enableEntryTray' flag.
   *
   * @param enableEntryTray <code>true</code> if <i>entry tray</i> is enabled.
   */
  public void setEnableEntryTray(final boolean enableEntryTray) {
    this.enableEntryTray = enableEntryTray;
  }

  /**
   * Sets the 'enableAccessControl' flag.
   * <b>Note</b> Enabling access control <i>disables</i> cache.
   *
   * @param enableAccessControl {@code true} enables repository access control.
   */
  public void setEnableAccessControl(final boolean enableAccessControl) {
    this.enableAccessControl = enableAccessControl;
  }

  /**
   * Sets the 'zipDownloadsAllowed' flag.
   *
   * @param zipDownloadsAllowed <code>true</code> if <i>download as zip</i> is allowed.
   */
  public void setZippedDownloadsAllowed(final boolean zipDownloadsAllowed) {
    this.zipDownloadsAllowed = zipDownloadsAllowed;
  }

  /**
   * Checks if <i>download as zip</i> is allowed.
   *
   * @return <code>true</code> if zipped downloads are allowed.
   */
  public boolean isZippedDownloadsAllowed() {
    return this.zipDownloadsAllowed;
  }

  /**
   * Gets the RSS feed items count.
   *
   * @return Number of rss feed items generated for this repository.
   */
  public int getRssItemsCount() {
    return rssItemsCount;
  }

  /**
   * Sets the number of rss items.
   *
   * @param count Count.
   */
  private void setRssItemsCount(final int count) {
    rssItemsCount = count;
  }

  /**
   * Gets the repository name.
   *
   * @return Repository name.
   */
  public RepositoryName getName() {
    return repositoryName;
  }

  /**
   * Checks if the issue tracker integration is enabled.
   *
   * @return {@code true} if enabled.
   */
  public boolean isIssueTrackerIntegrationEnabled() {
    return enableIssueTrackerIntegration;
  }

  /**
   * Enables or disables the issue tracker integration.
   *
   * @param enableIssueTrackerIntegration True or false.
   */
  public void setEnableIssueTrackerIntegration(final boolean enableIssueTrackerIntegration) {
    this.enableIssueTrackerIntegration = enableIssueTrackerIntegration;
  }

  /**
   * @param rssTemplateFile RSS template file.
   */
  public void setRssTemplateFile(final String rssTemplateFile) {
    this.rssTemplateFile = rssTemplateFile;
  }

  /**
   * @param mailTemplateFile Mail template file.
   */
  public void setMailTemplateFile(final String mailTemplateFile) {
    this.mailTemplateFile = mailTemplateFile;
  }

  /**
   * @return RSS template file.
   */
  public String getRssTemplateFile() {
    return rssTemplateFile;
  }

  /**
   * @return mail template file.
   */
  public String getMailTemplateFile() {
    return mailTemplateFile;
  }

  /**
   * @return RSS template
   * @throws IOException if unable to load template.
   */
  public String getRssTemplate() throws IOException {
    if (rssTemplate == null) {
      rssTemplate = loadTemplateFile(rssTemplateFile);
    }
    return rssTemplate;
  }

  /**
   * @return Mail template
   * @throws IOException if unable to load template.
   */
  public String getMailTemplate() throws IOException {
    if (mailTemplate == null) {
      mailTemplate = loadTemplateFile(mailTemplateFile);
    }
    return mailTemplate;
  }

  /**
   * Marks the configuration as persisted.
   */
  public void setPersisted() {
    this.persisted = true;
  }

  /**
   * @return True if the configuration is persisted, false if not.
   */
  public boolean isPersisted() {
    return persisted;
  }

  private String loadTemplateFile(final String filename) throws IOException {
    InputStream is = null;
    try {
      is = this.getClass().getResourceAsStream(filename);
      if (is == null) {
        throw new FileNotFoundException("Unable to find: " + filename);
      }
      return IOUtils.toString(is);
    } finally {
      IOUtils.closeQuietly(is);
    }
  }

}

