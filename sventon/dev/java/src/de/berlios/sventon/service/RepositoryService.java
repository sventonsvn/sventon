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

import de.berlios.sventon.repository.export.ExportDirectory;
import de.berlios.sventon.web.model.RawTextFile;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

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
   * @param repository      The repository
   * @param targets         Targets to export.
   * @param revision        Revision to export
   * @param exportDirectory Destination directory
   * @throws SVNException if a subversion error occur
   */
  void export(final SVNRepository repository, final List<String> targets, final long revision,
              final ExportDirectory exportDirectory) throws SVNException;

  /**
   * Gets a file from the repository as a raw text file.
   *
   * @param repository The repository
   * @param path       Path
   * @param revision   Revision
   * @return The text file instance
   * @throws SVNException if a subversion error occur
   */
  RawTextFile getTextFile(final SVNRepository repository, final String path, final long revision)
      throws SVNException;

  /**
   * Gets a file from the repository.
   *
   * @param repository The repository
   * @param path       Target to get
   * @param revision   The revision
   * @param output     Output stream to write contents to
   * @throws SVNException if a subversion error occur
   */
  void getFile(final SVNRepository repository, final String path, final long revision, final OutputStream output)
      throws SVNException;

  /**
   * Gets a file from the repository.
   *
   * @param repository The repository
   * @param path       Target to get
   * @param revision   The revision
   * @param output     Output stream to write contents to
   * @param properties The map to be populated with the file's properties
   * @throws SVNException if a subversion error occur
   */
  void getFile(final SVNRepository repository, final String path, final long revision, final OutputStream output,
               final Map properties) throws SVNException;

  /**
   * Gets a file's properties from the repository.
   *
   * @param repository The repository
   * @param path       Target of target to get properties for
   * @param revision   The revision
   * @param properties The map to be populated with the file's properties
   * @throws SVNException if a subversion error occur
   */
  void getFileProperties(final SVNRepository repository, final String path, final long revision, final Map properties)
      throws SVNException;

  /**
   * Checks whether given target file is a text file, by inspecting it's mime-type property.
   *
   * @param repository The repository
   * @param path       Target of target to get properties for
   * @param revision   The revision
   * @return <code>true</code> if file is a text file, <code>false</code> if not.
   * @throws SVNException if a subversion error occur
   */
  boolean isTextFile(final SVNRepository repository, final String path, final long revision) throws SVNException;

  /**
   * Gets a file's checksum.
   *
   * @param repository The repository
   * @param path       Target of target to get properties for
   * @param revision   The revision
   * @return The file's checksum
   * @throws SVNException if a subversion error occur
   */
  String getFileChecksum(final SVNRepository repository, final String path, final long revision) throws SVNException;

}
