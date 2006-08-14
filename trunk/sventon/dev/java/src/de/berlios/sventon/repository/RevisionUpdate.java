package de.berlios.sventon.repository;

import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.List;

/**
 * A RevisionUpdate object will be created when a repository change has been detected.
 * The object will be communicated to all {@link RevisionObserver}s and contains the
 * new revisions including repository instance information.
 *
 * @author jesper@users.berlios.de
 */
public class RevisionUpdate {

  private final String instanceName;
  private final List<SVNLogEntry> logEntries;

  public RevisionUpdate(final String instanceName, final List<SVNLogEntry> logEntries) {
    this.instanceName = instanceName;
    this.logEntries = logEntries;
  }

  /**
   * Gets the updates log entries, one per revision.
   *
   * @return The log entries.
   */
  public List<SVNLogEntry> getRevisions() {
    return logEntries;
  }

  /**
   * Gets the instance name.
   *
   * @return The instance name
   */
  public String getInstanceName() {
    return instanceName;
  }

}
