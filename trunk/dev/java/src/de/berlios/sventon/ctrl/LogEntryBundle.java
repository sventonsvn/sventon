package de.berlios.sventon.ctrl;

import org.tmatesoft.svn.core.io.SVNLogEntry;

public class LogEntryBundle {
  private SVNLogEntry svnLogEntry;
  private String pathAtRevision;
  
  /**
   * @param entry
   * @param entry
   */
  public LogEntryBundle(SVNLogEntry logEntry, String pathAtRevision) {
    super();
    // TODO Auto-generated constructor stub
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
