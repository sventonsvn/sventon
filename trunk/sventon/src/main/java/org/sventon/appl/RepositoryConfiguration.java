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
package org.sventon.appl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.model.RepositoryName;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Sventon application configuration class holding configuration parameters
 * and repository connection information for a single configured repositories.
 *
 * @author patrikfr@user.berlios.de
 * @author jesper@sventon.org
 */
public final class RepositoryConfiguration {

  /**
   * Default number of RSS feed items (20).
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

  public static final String PROPERTY_KEY_REPOSITORY_URL = "root";
  public static final String PROPERTY_KEY_REPOSITORY_DISPLAY_URL = "displayRoot";
  public static final String PROPERTY_KEY_USERNAME = "uid";
  public static final String PROPERTY_KEY_PASSWORD = "pwd";
  public static final String PROPERTY_KEY_USE_CACHE = "useCache";
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
  protected final Log logger = LogFactory.getLog(getClass());

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
   * If a global user is configured for repository browsing, this property
   * should be set.
   */
  private String uid;

  /**
   * If a global user is configured for repository browsing, this property
   * should be set.
   */
  private String pwd;

  /**
   * Decides whether the caching feature will be used.
   */
  private boolean useCache;

  /**
   * Decides whether <i>download as zip</i> is allowed.
   */
  private boolean zipDownloadsAllowed;

  /**
   * Decides wheter repository access control is enforced (this is configured on the
   * SVN server). Note that enabling access control _disables_ caching.
   */
  private boolean enableAccessControl;

  /**
   * Decides whether the <i>entry tray</i> is enabled.
   */
  private boolean enableEntryTray = true;

  /**
   * Decides wheter the issue tracker integration should be used.
   */
  private boolean enableIssueTrackerIntegration;

  /**
   * Number of items in the generated RSS feed.
   */
  private int rssItemsCount = DEFAULT_RSS_ITEMS_COUNT;

  private String rssTemplateFile = DEFAULT_RSS_TEMPLATE_FILE;

  private String rssTemplate;

  private String mailTemplateFile = DEFAULT_MAIL_TEMPLATE_FILE;

  private String mailTemplate;


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
   * Creates an instance using given name and properties.
   *
   * @param repositoryName Repository name
   * @param properties     Properties
   * @return The RepositoryConfiguration
   */
  public static RepositoryConfiguration create(final String repositoryName, final Properties properties) {
    final RepositoryConfiguration ic = new RepositoryConfiguration(repositoryName);
    ic.setRepositoryUrl((String) properties.get(PROPERTY_KEY_REPOSITORY_URL));
    ic.setRepositoryDisplayUrl((String) properties.get(PROPERTY_KEY_REPOSITORY_DISPLAY_URL));
    ic.setUid((String) properties.get(PROPERTY_KEY_USERNAME));
    ic.setPwd((String) properties.get(PROPERTY_KEY_PASSWORD));
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
    if (getUid() != null) {
      properties.put(PROPERTY_KEY_USERNAME, getUid());
    }
    if (getPwd() != null) {
      properties.put(PROPERTY_KEY_PASSWORD, getPwd());
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
   * Get configured Password, if any.
   *
   * @return Returns the pwd.
   */
  public String getPwd() {
    return pwd;
  }

  /**
   * Set a configured pwd. This pwd will be used for repository
   * access, together with configured user ID, {@link #setUid(String)}
   *
   * @param pwd The pwd to set, may be <code>null</code>.
   */
  public void setPwd(final String pwd) {
    this.pwd = pwd;
  }

  /**
   * Get configured user ID, if any.
   *
   * @return Returns the uid.
   */
  public String getUid() {
    return uid;
  }

  /**
   * Set a configured user ID. This user ID will be used for repository access,
   * together with configured pwd, {@link #setPwd(String)}
   *
   * @param uid The uid to set, may be <code>null</code>
   */
  public void setUid(final String uid) {
    this.uid = uid;
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
      svnUrl = SVNURL.parseURIDecoded(this.repositoryUrl);
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
    return this.useCache && !this.enableAccessControl;
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
   * @return Number of rss feed items generated for this instance.
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

  public void setRssTemplateFile(final String rssTemplateFile) {
    this.rssTemplateFile = rssTemplateFile;
  }

  public void setMailTemplateFile(final String mailTemplateFile) {
    this.mailTemplateFile = mailTemplateFile;
  }

  public String getRssTemplateFile() {
    return rssTemplateFile;
  }

  public String getMailTemplateFile() {
    return mailTemplateFile;
  }

  private String getTemplate(final String name) throws IOException {
    final InputStream is = this.getClass().getResourceAsStream(name);
    if (is == null) {
      throw new FileNotFoundException("Unable to find: " + name);
    }
    return IOUtils.toString(is);
  }

  public String getRssTemplate() throws IOException {
    if (rssTemplate == null) {
      rssTemplate = getTemplate(rssTemplateFile);
    }
    return rssTemplate;
  }

  public String getMailTemplate() throws IOException {
    if (mailTemplate == null) {
      mailTemplate = getTemplate(mailTemplateFile);
    }
    return mailTemplate;
  }

}

