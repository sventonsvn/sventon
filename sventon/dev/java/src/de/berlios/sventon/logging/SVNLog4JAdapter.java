/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.util.SVNDebugLogAdapter;

/**
 * Log4J sventon adapter for JavaSVN logging.
 *
 * @author patrikfr@users.berlios.de
 */
public class SVNLog4JAdapter extends SVNDebugLogAdapter {

  /**
   * The <tt>Log</tt> instance.
   */
  private final Log logger;

  /**
   * Constructor.
   *
   * @param namespace Logging name space to use.
   */
  public SVNLog4JAdapter(final String namespace) {
    logger = LogFactory.getLog(namespace);
  }

  /**
   * {@inheritDoc}
   */
  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  /**
   * {@inheritDoc}
   */
  public boolean isFineEnabled() {
    return logger.isDebugEnabled();
  }

  /**
   * {@inheritDoc}
   */
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  /**
   * {@inheritDoc}
   */
  public void logError(final String message, final Throwable th) {
    logger.error(message, th);
  }

  /**
   * {@inheritDoc}
   */
  public void logFine(String message) {
    logger.debug(message);
  }

  /**
   * {@inheritDoc}
   */
  public void logInfo(String message) {
    logger.info(message);
  }

}
