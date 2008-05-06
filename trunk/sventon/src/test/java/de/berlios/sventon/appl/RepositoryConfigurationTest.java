package de.berlios.sventon.appl;

import static de.berlios.sventon.appl.RepositoryConfiguration.*;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNURL;

import java.util.Properties;

public class RepositoryConfigurationTest extends TestCase {

  public void testCreateInstanceConfigurationTest() throws Exception {
    final String repositoryUrl = "svn://repositoryserver/repository";
    final Properties props = new Properties();
    props.setProperty(PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS, "true");
    props.setProperty(PROPERTY_KEY_ENABLE_ACCESS_CONTROL, "true");
    props.setProperty(PROPERTY_KEY_PASSWORD, "pwd");
    props.setProperty(PROPERTY_KEY_USERNAME, "uid");

    props.setProperty(PROPERTY_KEY_REPOSITORY_URL, repositoryUrl);
    props.setProperty(PROPERTY_KEY_USE_CACHE, "false");
    props.setProperty(PROPERTY_KEY_RSS_ITEMS_COUNT, "20");

    RepositoryConfiguration conf = RepositoryConfiguration.create("test", props);

    assertTrue(conf.isZippedDownloadsAllowed());
    assertTrue(conf.isAccessControlEnabled());
    assertEquals("pwd", conf.getPwd());
    assertEquals("uid", conf.getUid());
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

  public void testasdf() {
    RepositoryConfiguration conf = new RepositoryConfiguration("test");
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
    assertEquals(6, props.size());

    assertEquals("http://localhost", props.get(PROPERTY_KEY_REPOSITORY_URL));
    assertEquals("http://localhost", props.get(PROPERTY_KEY_REPOSITORY_DISPLAY_URL));

    assertNull(props.get(PROPERTY_KEY_USERNAME));
    assertNull(props.get(PROPERTY_KEY_PASSWORD));

    assertEquals("false", props.get(PROPERTY_KEY_USE_CACHE));
    assertEquals("false", props.get(PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS));

    assertEquals("false", props.get(PROPERTY_KEY_ENABLE_ACCESS_CONTROL));
    assertEquals("20", props.get(PROPERTY_KEY_RSS_ITEMS_COUNT));
  }
}
