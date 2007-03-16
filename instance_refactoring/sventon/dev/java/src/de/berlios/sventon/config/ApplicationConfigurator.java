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
package de.berlios.sventon.config;

import de.berlios.sventon.Version;
import de.berlios.sventon.logging.SVNLog4JAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.util.SVNDebugLog;

import java.io.IOException;

/**
 * Application configurator class.
 * Reads given configuration instance and initializes sventon.
 * <p/>
 * The class also performs SVNKit initialization, such as setting up logging
 * and repository access. It should be instanciated once (and only once), when
 * the application starts.
 *
 * @author jesper@users.berlios.de
 * @see <a href="http://www.svnkit.com">SVNKit</a>
 */
public class ApplicationConfigurator {

  /**
   * The logging instance.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * Constructor.
   *
   * @param configuration Application configuration instance to populate, not {@code null}
   * @throws IOException if IO error occur
   */
  public ApplicationConfigurator(final ApplicationConfiguration configuration) throws IOException {

    if (configuration == null) {
      throw new IllegalArgumentException("Parameters cannot be null");
    }
    initSvnSupport();
    configuration.loadInstanceConfigurations();
  }

  /**
   * Initializes the logger and the SVNKit library.
   */
  private void initSvnSupport() {
    SVNDebugLog.setDefaultLog(new SVNLog4JAdapter("sventon.svnkit"));
    logger.info("Initializing sventon version "
        + Version.getVersion() + " (revision " + Version.getRevision() + ")");
    SVNRepositoryFactoryImpl.setup();
    DAVRepositoryFactory.setup();
    FSRepositoryFactory.setup();
  }


}