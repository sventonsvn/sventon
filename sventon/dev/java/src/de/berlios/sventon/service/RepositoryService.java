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
package de.berlios.sventon.service;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.List;
import java.io.File;

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

  /**
   * Exports given list of target entries to the given destination export directory.
   *
   * @param repository The repository
   * @param targets    Targets to export.
   * @param revision   Revision to export
   * @param exportDir  Destination directory
   */
  void export(final SVNRepository repository, final List<String> targets, final long revision, final File exportDir) throws SVNException;

}
