package org.sventon.appl;

import junit.framework.TestCase;
import static org.sventon.appl.RepositoryConfiguration.*;
import org.tmatesoft.svn.core.SVNURL;

import java.util.Properties;

public class RepositoryConfigurationTest extends TestCase {

  public void testDefault() {
    final String repositoryUrl = "svn://repositoryserver/repository";
    final Properties props = new Properties();
    props.setProperty(PROPERTY_KEY_REPOSITORY_URL, repositoryUrl);
    final RepositoryConfiguration conf = RepositoryConfiguration.create("test", props);

    assertNull(conf.getCredentials().getUsername());
    assertNull(conf.getCredentials().getPassword());
    assertEquals("test", conf.getName().toString());
    assertEquals("svn://repositoryserver/repository", conf.getRepositoryDisplayUrl());
    assertEquals("svn://repositoryserver/repository", conf.getRepositoryUrl());
    assertEquals("/mailtemplate.html", conf.getMailTemplateFile());
    assertEquals("/rsstemplate.html", conf.getRssTemplateFile());
    assertEquals(20, conf.getRssItemsCount());

    assertFalse(conf.isAccessControlEnabled());
    assertFalse(conf.isCacheUsed());
    assertFalse(conf.isIssueTrackerIntegrationEnabled());
    assertFalse(conf.isZippedDownloadsAllowed());
    assertTrue(conf.isEntryTrayEnabled());
  }

  public void testCreateRepositoryConfigurationTest() throws Exception {
    final String repositoryUrl = "svn://repositoryserver/repository";
    final Properties props = new Properties();
    props.setProperty(PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS, "true");
    props.setProperty(PROPERTY_KEY_ENABLE_ACCESS_CONTROL, "true");
    props.setProperty(PROPERTY_KEY_PASSWORD, "pwd");
    props.setProperty(PROPERTY_KEY_USERNAME, "uid");

    props.setProperty(PROPERTY_KEY_REPOSITORY_URL, repositoryUrl);
    props.setProperty(PROPERTY_KEY_USE_CACHE, "false");
    props.setProperty(PROPERTY_KEY_RSS_ITEMS_COUNT, "20");

    final RepositoryConfiguration conf = RepositoryConfiguration.create("test", props);

    assertTrue(conf.isZippedDownloadsAllowed());
    assertTrue(conf.isAccessControlEnabled());
    assertEquals("uid", conf.getCredentials().getUsername());
    assertEquals("pwd", conf.getCredentials().getPassword());
    assertEquals(repositoryUrl, conf.getRepositoryUrl());
    assertEquals(repositoryUrl, conf.getRepositoryDisplayUrl());
    assertEquals(SVNURL.parseURIDecoded(conf.getRepositoryUrl()), conf.getSVNURL());

    assertFalse(conf.isCacheUsed());
    assertEquals(20, conf.getRssItemsCount());
  }

  public void testSetRepositoryUrl() throws Exception {
    RepositoryConfiguration conf = new RepositoryConfiguration("test");

    conf.setRepositoryUrl("svn://localhost/svn");
    assertEquals("svn://localhost/svn", conf.getRepositoryUrl());
    assertEquals(SVNURL.parseURIDecoded("svn://localhost/svn"), conf.getSVNURL());

    //trailing slashes should be removed
    conf.setRepositoryUrl("svn://localhost/svn/");
    assertEquals("svn://localhost/svn", conf.getRepositoryUrl());
    assertEquals(SVNURL.parseURIDecoded("svn://localhost/svn"), conf.getSVNURL());

    //so should trailing spaces
    conf.setRepositoryUrl("svn://localhost/svn/    ");
    assertEquals("svn://localhost/svn", conf.getRepositoryUrl());
    assertEquals(SVNURL.parseURIDecoded("svn://localhost/svn"), conf.getSVNURL());

    conf.setRepositoryUrl("svn://localhost/svn    ");
    assertEquals("svn://localhost/svn", conf.getRepositoryUrl());
    assertEquals(SVNURL.parseURIDecoded("svn://localhost/svn"), conf.getSVNURL());

  }

  public void testCacheUsedAndAccessControlEnabledCombinations() {
    final RepositoryConfiguration conf = new RepositoryConfiguration("test");
    conf.setCacheUsed(true);
    conf.setEnableAccessControl(false);

    assertTrue(conf.isCacheUsed());
    assertFalse(conf.isAccessControlEnabled());

    //Can't have both caching and access control
    conf.setEnableAccessControl(true);
    assertFalse(conf.isCacheUsed());
    assertTrue(conf.isAccessControlEnabled());

    conf.setCacheUsed(false);
    assertFalse(conf.isCacheUsed());
    assertTrue(conf.isAccessControlEnabled());

    conf.setEnableAccessControl(false);
    assertFalse(conf.isCacheUsed());
    assertFalse(conf.isAccessControlEnabled());

    conf.setCacheUsed(true);
    assertTrue(conf.isCacheUsed());
    assertFalse(conf.isAccessControlEnabled());
  }


  public void testGetAsProperties() throws Exception {
    final RepositoryConfiguration conf = new RepositoryConfiguration("test");

    try {
      conf.getAsProperties();
      fail("Should throw NPE");
    } catch (NullPointerException npe) {
      // expected
    }

    conf.setRepositoryUrl("http://localhost");

    final Properties props = conf.getAsProperties();
    assertEquals(10, props.size());

    assertEquals("http://localhost", props.get(PROPERTY_KEY_REPOSITORY_URL));
    assertEquals("http://localhost", props.get(PROPERTY_KEY_REPOSITORY_DISPLAY_URL));

    assertNull(props.get(PROPERTY_KEY_USERNAME));
    assertNull(props.get(PROPERTY_KEY_PASSWORD));

    assertEquals("false", props.get(PROPERTY_KEY_USE_CACHE));
    assertEquals("false", props.get(PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS));
    assertEquals("false", props.get(PROPERTY_KEY_ENABLE_ISSUE_TRACKER_INTEGRATION));

    assertEquals("false", props.get(PROPERTY_KEY_ENABLE_ACCESS_CONTROL));
    assertEquals("20", props.get(PROPERTY_KEY_RSS_ITEMS_COUNT));

    assertEquals("/rsstemplate.html", props.get(PROPERTY_KEY_RSS_TEMPLATE_FILE));
    assertEquals("/mailtemplate.html", props.get(PROPERTY_KEY_MAIL_TEMPLATE_FILE));
  }
}
