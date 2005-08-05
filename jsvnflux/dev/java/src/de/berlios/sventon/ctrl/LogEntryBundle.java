package de.berlios.sventon.ctrl;

import org.tmatesoft.svn.core.SVNLogEntry;

/**
 * @author patrikfr@users.berlios.de
 */
public class LogEntryBundle {

  /** Subversion log entry. */
  private SVNLogEntry svnLogEntry;

  /** The path at revision. */
  private String pathAtRevision;
  
  /**
   * @param logEntry
   * @param pathAtRevision
   */
  public LogEntryBundle(SVNLogEntry logEntry, String pathAtRevision) {
    svnLogEntry = logEntry;
    this.pathAtRevision = pathAtRevision;
  }

  /**
   * @return Returns the pathAtRevision.
   */
  public final String getPathAtRevision() {
    return pathAtRevision;
  }

  /**
   * @return Returns the svnLogEntry.
   */
  public final SVNLogEntry getSvnLogEntry() {
    return svnLogEntry;
  }
  
}
