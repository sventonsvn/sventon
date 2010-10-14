package org.sventon.appl;

import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.sventon.TestUtils;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ConfigDirectoryTest {

  private static final File TEMP_DIR = new File(TestUtils.TEMP_DIR);
  private static final String SEPARATOR = System.getProperty("file.separator");

  private static final String SERVLET_CONTEXT_PATH = "svn";
  private static final String EXPORT_DIR = "export_temp";
  private static final String REPOSITORIES_DIR = "repositories";

  @Test
  public void testConfigDirectory() throws Exception {

    final ConfigDirectory configDir = new ConfigDirectory(TEMP_DIR, EXPORT_DIR, REPOSITORIES_DIR);
    configDir.setCreateDirectories(false);

    try {
      configDir.getConfigRootDirectory();
      fail("Should cause IllegalStateException");
    } catch (IllegalStateException e) {
      // expected
    }

    try {
      configDir.getExportDirectory();
      fail("Should cause IllegalStateException");
    } catch (IllegalStateException e) {
      // expected
    }

    try {
      configDir.getRepositoriesDirectory();
      fail("Should cause IllegalStateException");
    } catch (IllegalStateException e) {
      // expected
    }

    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath(SERVLET_CONTEXT_PATH);
    configDir.setServletContext(servletContext);

    assertTrue(configDir.getConfigRootDirectory().getAbsolutePath().endsWith(SERVLET_CONTEXT_PATH));
    assertTrue(configDir.getExportDirectory().getAbsolutePath().endsWith(
        SERVLET_CONTEXT_PATH + SEPARATOR + EXPORT_DIR));
    assertTrue(configDir.getRepositoriesDirectory().getAbsolutePath().endsWith(
        SERVLET_CONTEXT_PATH + SEPARATOR + REPOSITORIES_DIR));
  }

  @Test
  public void testDirectoryOverrideBySettingSystemProperty() throws Exception {
    System.setProperty(ConfigDirectory.PROPERTY_KEY_SVENTON_DIR_SYSTEM, SEPARATOR + "override");

    final ConfigDirectory configDir = new ConfigDirectory(TEMP_DIR, EXPORT_DIR, REPOSITORIES_DIR);
    configDir.setCreateDirectories(false);

    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath(SERVLET_CONTEXT_PATH);
    configDir.setServletContext(servletContext);

    final String path = configDir.getConfigRootDirectory().getAbsolutePath();
    assertTrue(path.contains(SEPARATOR + "override" + SEPARATOR));
    assertTrue(path.endsWith(SERVLET_CONTEXT_PATH));

    System.clearProperty(ConfigDirectory.PROPERTY_KEY_SVENTON_DIR_SYSTEM);
  }
}
