package de.berlios.sventon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.util.DebugLoggerAdapter;

public class SventonSVNLogger extends DebugLoggerAdapter {
  
  protected final Log logger = LogFactory.getLog("sventon.javasvn");

  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  public boolean isFineEnabled() {
    return logger.isDebugEnabled();
  }

  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  public void logError(String message, Throwable th) {
    logger.error(message, th);
  }

  public void logFine(String message) {
    logger.debug(message);
  }

  public void logInfo(String message) {
    logger.info(message);
  }
  
}
