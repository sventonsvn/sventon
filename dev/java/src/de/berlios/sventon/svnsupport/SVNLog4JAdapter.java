package de.berlios.sventon.svnsupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.util.SVNDebugLoggerAdapter;

/**
 * Log4J sventon adapter for JavaSVN logging.
 * 
 * @author patrikfr@users.berlios.de
 */
public class SVNLog4JAdapter extends SVNDebugLoggerAdapter {

  /** The <tt>Log</tt> instance. */
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
  public void logError(String message, Throwable th) {
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
