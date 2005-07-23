package de.berlios.sventon.svnsupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.util.DebugLoggerAdapter;

/**
 * Log4J sventon adapter for JavaSVN logging.
 * 
 * @author patrikfr@berlios.de
 */
public class SventonSVNLogger extends DebugLoggerAdapter {

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
