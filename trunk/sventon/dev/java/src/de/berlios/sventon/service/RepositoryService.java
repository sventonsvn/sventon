package de.berlios.sventon.service;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.List;

/**
 * Service class for accessing the subversion repository.
 *
 * @author jesper@users.berlios.de
 */
public interface RepositoryService {

  /**
   * Gets revision details for a specific revision number.
   *
   * @param repository The repository
   * @param revision   Revision number
   * @return The revision log entry
   * @throws SVNException if subversion error
   */
  SVNLogEntry getRevision(final SVNRepository repository, final long revision) throws SVNException;

  /**
   * Gets revision details for a specifica revision number and path.
   *
   * @param repository The repository
   * @param revision   Revision number
   * @param path       The repository path
   * @return The revision log entry
   * @throws SVNException if subversion error
   */
  SVNLogEntry getRevision(final SVNRepository repository, final long revision, final String path) throws SVNException;

  /**
   * Gets revision details for given revision interval.
   *
   * @param repository   The repository
   * @param fromRevision From revision
   * @param toRevision   To revision
   * @return The revision log entries
   * @throws SVNException if subversion error
   */
  List<SVNLogEntry> getRevisions(final SVNRepository repository, final long fromRevision, final long toRevision)
      throws SVNException;

  /**
   * Gets revision details for given revision interval with limit.
   *
   * @param repository   The repository
   * @param fromRevision From revision
   * @param toRevision   To revision
   * @param limit        Revision limit
   * @return The revision log entries
   * @throws SVNException if subversion error
   */
  List<SVNLogEntry> getRevisions(final SVNRepository repository, final long fromRevision, final long toRevision,
                                 final long limit) throws SVNException;

  /**
   * Gets revision details for given revision interval and a specific path with limit.
   *
   * @param repository   The repository
   * @param fromRevision From revision
   * @param toRevision   To revision
   * @param path         The repository path
   * @param limit        Revision limit
   * @return The revision log entries
   * @throws SVNException if subversion error
   */
  List<SVNLogEntry> getRevisions(final SVNRepository repository, final long fromRevision, final long toRevision,
                                 final String path, final long limit) throws SVNException;

}
