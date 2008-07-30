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
package org.sventon.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.util.SVNDebugLogAdapter;

/**
 * Log4J sventon adapter for SVNKit logging.
 *
 * @author patrik@sventon.org
 */
public final class SVNLog4JAdapter extends SVNDebugLogAdapter {

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
  public void log(final String message, final byte[] data) {
    logger.debug(message + "\n" + new String(data));
  }

  /**
   * {@inheritDoc}
   */
  public void logInfo(final String message) {
    logger.info(message);
  }

  /**
   * {@inheritDoc}
   */
  public void logError(final String message) {
    logger.error(message);
  }

  /**
   * {@inheritDoc}
   */
  public void logInfo(final Throwable th) {
    logger.info(th);
  }

  /**
   * {@inheritDoc}
   */
  public void logError(final Throwable th) {
    logger.error(th);
  }
}
