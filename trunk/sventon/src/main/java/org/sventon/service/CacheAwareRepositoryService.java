/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.sventon.SVNConnection;
import org.sventon.SventonException;
import org.sventon.appl.Application;
import org.sventon.cache.CacheGateway;
import org.sventon.colorer.Colorer;
import org.sventon.diff.DiffException;
import org.sventon.export.ExportDirectory;
import org.sventon.model.*;
import org.sventon.web.command.DiffCommand;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Cache aware sub classed implementation of RepositoryService.
 *
 * @author jesper@sventon.org
 */
public final class CacheAwareRepositoryService implements RepositoryService {

  /**
   * Logger for this class and subclasses.
   */
  final Log logger = LogFactory.getLog(getClass());

  /**
   * The cache instance.
   */
  private CacheGateway cacheGateway;

  /**
   * The application.
   */
  private Application application;

  /**
   * Delegate service.
   */
  private RepositoryService delegate;

  /**
   * Constructor.
   *
   * @param delegate Repository service to delegate to.
   */
  public CacheAwareRepositoryService(final RepositoryService delegate) {
    this.delegate = delegate;
  }

  /**
   * Sets the cache gateway instance.
   *
   * @param cacheGateway Cache gateway instance
   */
  public void setCacheGateway(final CacheGateway cacheGateway) {
    this.cacheGateway = cacheGateway;
  }

  /**
   * Sets the application.
   *
   * @param application Application
   */
  @Autowired
  public void setApplication(final Application application) {
    this.application = application;
  }

  /**
   * If the instance is configured to use the cache, and the cache is not
   * currently busy updating, a cached log entry instance will be returned.
   */
  @Override
  public LogEntry getLogEntry(final RepositoryName repositoryName, final SVNConnection connection, final long revision)
      throws SventonException {

    final LogEntry logEntry;
    if (canReturnCachedRevisionsFor(repositoryName)) {
      logger.debug("Fetching cached revision: " + revision);
      logEntry = cacheGateway.getRevision(repositoryName, revision);
    } else {
      logEntry = delegate.getLogEntry(repositoryName, connection, revision);
    }
    return logEntry;
  }

  /**
   * If the instance is configured to use the cache, and the cache is not
   * currently busy updating, a cached log entry instance will be returned.
   */
  @Override
  public List<LogEntry> getLogEntries(final RepositoryName repositoryName, final SVNConnection connection,
                                      final long fromRevision, final long toRevision, final String path,
                                      final long limit, final boolean stopOnCopy, boolean includeChangedPaths)
      throws SventonException {

    final List<LogEntry> logEntries = new ArrayList<LogEntry>();
    if (canReturnCachedRevisionsFor(repositoryName)) {
      final List<Long> revisions = new ArrayList<Long>();
      if ("/".equals(path)) {
        // Requested path is root - simply return the revisions without checking with the repository
        revisions.addAll(calculateRevisionsToFetch(fromRevision, limit));
      } else {
        // To be able to return cached revisions, we first have to get the revision numbers for given path
        // Doing a logs-call, skipping the details, to get them.
        revisions.addAll(delegate.getRevisionsForPath(connection, path, fromRevision, toRevision, stopOnCopy, limit));
      }
      logger.debug("Fetching [" + limit + "] cached revisions: " + revisions);
      logEntries.addAll(cacheGateway.getRevisions(repositoryName, revisions));
    } else {
      logEntries.addAll(delegate.getLogEntries(repositoryName, connection, fromRevision, toRevision, path, limit,
          stopOnCopy, includeChangedPaths));
    }
    return logEntries;
  }

  protected List<Long> calculateRevisionsToFetch(final long fromRevision, final long limit) {
    final List<Long> revisions = new ArrayList<Long>();
    for (long i = fromRevision; i > (fromRevision - limit) && (i > 0); i--) {
      revisions.add(i);
    }
    return revisions;
  }

  private boolean canReturnCachedRevisionsFor(final RepositoryName repositoryName) {
    return application.getConfiguration(repositoryName).isCacheUsed() && !application.isUpdating(repositoryName);
  }

  @Override
  public List<LogEntry> getLogEntriesFromRepositoryRoot(SVNConnection connection, long fromRevision, long toRevision) throws SventonException {
    return delegate.getLogEntriesFromRepositoryRoot(connection, fromRevision, toRevision);
  }

  @Override
  public void export(SVNConnection connection, List<PathRevision> targets, long pegRevision, ExportDirectory exportDirectory) throws SventonException {
    delegate.export(connection, targets, pegRevision, exportDirectory);
  }

  @Override
  public void getFileContents(SVNConnection connection, String path, long revision, OutputStream output) throws SventonException {
    delegate.getFileContents(connection, path, revision, output);
  }

  @Override
  public Properties getFileProperties(SVNConnection connection, String path, long revision) throws SventonException {
    return delegate.getFileProperties(connection, path, revision);
  }

  @Override
  public String getFileChecksum(SVNConnection connection, String path, long revision) throws SventonException {
    return delegate.getFileChecksum(connection, path, revision);
  }

  @Override
  public Long getLatestRevision(SVNConnection connection) throws SventonException {
    return delegate.getLatestRevision(connection);
  }

  @Override
  public DirEntry.Kind getNodeKind(SVNConnection connection, String path, long revision) throws SventonException {
    return delegate.getNodeKind(connection, path, revision);
  }

  @Override
  public Map<String, DirEntryLock> getLocks(SVNConnection connection, String startPath) {
    return delegate.getLocks(connection, startPath);
  }

  @Override
  public DirList list(SVNConnection connection, String path, long revision) throws SventonException {
    return delegate.list(connection, path, revision);
  }

  @Override
  public DirEntry getEntryInfo(SVNConnection connection, String path, long revision) throws SventonException {
    return delegate.getEntryInfo(connection, path, revision);
  }

  @Override
  public List<FileRevision> getFileRevisions(SVNConnection connection, String path, long revision) throws SventonException {
    return delegate.getFileRevisions(connection, path, revision);
  }

  @Override
  public List<SideBySideDiffRow> diffSideBySide(SVNConnection connection, DiffCommand command, Revision pegRevision, String charset) throws SventonException, DiffException {
    return delegate.diffSideBySide(connection, command, pegRevision, charset);
  }

  @Override
  public String diffUnified(SVNConnection connection, DiffCommand command, Revision pegRevision, String charset) throws SventonException, DiffException {
    return delegate.diffUnified(connection, command, pegRevision, charset);
  }

  @Override
  public List<InlineDiffRow> diffInline(SVNConnection connection, DiffCommand command, Revision pegRevision, String charset) throws SventonException, DiffException {
    return delegate.diffInline(connection, command, pegRevision, charset);
  }

  @Override
  public List<DiffStatus> diffPaths(SVNConnection connection, DiffCommand command) throws SventonException {
    return delegate.diffPaths(connection, command);
  }

  @Override
  public AnnotatedTextFile blame(SVNConnection connection, String path, long revision, String charset, Colorer colorer) throws SventonException {
    return delegate.blame(connection, path, revision, charset, colorer);
  }

  @Override
  public DirEntry.Kind getNodeKindForDiff(SVNConnection connection, DiffCommand command) throws SventonException, DiffException {
    return delegate.getNodeKindForDiff(connection, command);
  }

  @Override
  public Revision translateRevision(Revision revision, long headRevision, SVNConnection connection) throws SventonException {
    return delegate.translateRevision(revision, headRevision, connection);
  }

  @Override
  public List<LogEntry> getLatestRevisions(RepositoryName repositoryName, SVNConnection connection, int revisionCount) throws SventonException {
    return delegate.getLatestRevisions(repositoryName, connection, revisionCount);
  }

  @Override
  public List<Long> getRevisionsForPath(SVNConnection connection, String path, long fromRevision, long toRevision, boolean stopOnCopy, long limit) throws SventonException {
    return delegate.getRevisionsForPath(connection, path, fromRevision, toRevision, stopOnCopy, limit);
  }


}
