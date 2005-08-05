package de.berlios.sventon.svnsupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.util.SVNDebugLoggerAdapter;

/**
 * Log4J sventon adapter for JavaSVN logging.
 * 
 * @author patrikfr@berlios.de
 */
public class SventonSVNLogger extends SVNDebugLoggerAdapter {

  //TODO: Rename this class to SVNLog4JAdapter, make it possible to set name space in constructor.
  private final Log logger = LogFactory.getLog("sventon.javasvn");

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
