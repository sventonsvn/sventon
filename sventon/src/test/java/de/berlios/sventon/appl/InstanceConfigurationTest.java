package de.berlios.sventon.appl;

import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNURL;

import java.util.Properties;

public class InstanceConfigurationTest extends TestCase {
  private static final String I = "test";

  public void testCreateInstanceConfigurationTest() throws Exception {
    final Properties p = new Properties();
    p.setProperty(I + InstanceConfiguration.PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS, "true");
    p.setProperty(I + InstanceConfiguration.PROPERTY_KEY_ENABLE_ACCESS_CONTROL, "true");
    p.setProperty(I + InstanceConfiguration.PROPERTY_KEY_PASSWORD, "pwd");
    p.setProperty(I + InstanceConfiguration.PROPERTY_KEY_USERNAME, "uid");
    p.setProperty(I + InstanceConfiguration.PROPERTY_KEY_REPOSITORY_URL, "svn://repositoryserver/repository");
    p.setProperty(I + InstanceConfiguration.PROPERTY_KEY_USE_CACHE, "false");
    p.setProperty(I + InstanceConfiguration.PROPERTY_KEY_RSS_ITEMS_COUNT, "20");

    InstanceConfiguration conf = InstanceConfiguration.create(I, p);

    assertTrue(conf.isZippedDownloadsAllowed());
    assertTrue(conf.isAccessControlEnabled());
    assertEquals("pwd", conf.getPwd());
    assertEquals("uid", conf.getUid());
    assertEquals("svn://repositoryserver/repository", conf.getRepositoryUrl());
    assertEquals(SVNURL.parseURIDecoded(conf.getRepositoryUrl()), conf.getSVNURL());

    assertFalse(conf.isCacheUsed());
    assertEquals(20, conf.getRssItemsCount());

  }

  public void testSetRepositoryUrl() throws Exception {
    InstanceConfiguration conf = new InstanceConfiguration("test");

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

  public void testasdf() {
    InstanceConfiguration conf = new InstanceConfiguration("test");
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


}