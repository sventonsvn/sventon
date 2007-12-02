package de.berlios.sventon.appl;

import junit.framework.TestCase;

import java.io.File;
import java.util.Properties;
import java.util.List;

public class ApplicationTest extends TestCase {

  private static final String TEMPDIR = System.getProperty("java.io.tmpdir");
  
  public void testApplication() throws Exception {
    try {
      new Application(null, null);
      fail("Should throw IAE");
    } catch (IllegalArgumentException iae) {
      // exptected
    }

    final Application application = new Application(new File(TEMPDIR), "filename");
    assertFalse(application.isConfigured());
    assertEquals(0, application.getInstanceCount());
    assertNotNull(application.getConfigurationDirectory());
    assertEquals("filename", application.getConfigurationFilename());
  }

  public void testStoreInstanceConfigurations() throws Exception {
    final Application application = new Application(new File(TEMPDIR), "tmpconfigfilename");
    final InstanceConfiguration instanceConfiguration1 = new InstanceConfiguration("test");
    instanceConfiguration1.setRepositoryUrl("http://localhost/1");
    instanceConfiguration1.setUid("user1");
    instanceConfiguration1.setPwd("abc123");
    instanceConfiguration1.setCacheUsed(false);
    instanceConfiguration1.setZippedDownloadsAllowed(false);

    final InstanceConfiguration instanceConfiguration2 = new InstanceConfiguration("test");
    instanceConfiguration2.setRepositoryUrl("http://localhost/2");
    instanceConfiguration2.setUid("user2");
    instanceConfiguration2.setPwd("123abc");
    instanceConfiguration2.setCacheUsed(false);
    instanceConfiguration2.setZippedDownloadsAllowed(false);

    application.addInstance("testrepos1", instanceConfiguration1);
    application.addInstance("testrepos2", instanceConfiguration2);

    final File propFile = new File(TEMPDIR, "tmpconfigfilename");
    assertFalse(propFile.exists());

    application.storeInstanceConfigurations();

    //File should now be written
    assertTrue(propFile.exists());
    propFile.delete();
    assertFalse(propFile.exists());
  }

  public void testGetConfigurationAsProperties() throws Exception {
    final Application application = new Application(new File(TEMPDIR), "filename");
    final InstanceConfiguration config1 = new InstanceConfiguration("test");
    config1.setRepositoryUrl("http://repo1");
    config1.setUid("");
    config1.setPwd("");

    final InstanceConfiguration config2 = new InstanceConfiguration("test");
    config2.setRepositoryUrl("http://repo2");
    config2.setUid("");
    config2.setPwd("");

    application.addInstance("test1", config1);
    application.addInstance("test2", config2);

    final List<Properties> configurations = application.getConfigurationAsProperties();
    Properties props = configurations.get(0);
    assertEquals(7, props.size());
    props = configurations.get(1);
    assertEquals(7, props.size());
  }

}