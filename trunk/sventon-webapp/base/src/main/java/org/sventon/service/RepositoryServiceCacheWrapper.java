package org.sventon.service;

import com.googlecode.ehcache.annotations.Cacheable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.Colorer;
import org.sventon.SVNConnection;
import org.sventon.SventonException;
import org.sventon.model.*;

import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Wrapper class for the repository service used to add the cache aspect.
 */
public class RepositoryServiceCacheWrapper implements RepositoryService {

  /**
   * Logger for this class.
   */
  private final Log logger = LogFactory.getLog(getClass().getName());

  private final RepositoryService delegate;

  /**
   * Constructor.
   *
   * @param delegate Repository Service implementation (JavaHL or SVNKit).
   */
  public RepositoryServiceCacheWrapper(final RepositoryService delegate) {
    logger.info("Creating service wrapper for: " + delegate.getClass());
    this.delegate = delegate;
  }

  @Override
  @Cacheable(cacheName = "latestRevisionCache")
  public Long getLatestRevision(SVNConnection connection) throws SventonException {
    if (logger.isDebugEnabled()) {
      logger.debug("Delegating call to: " + getMethodName(Thread.currentThread().getStackTrace()));
    }
    return delegate.getLatestRevision(connection);
  }

  @Override
  @Cacheable(cacheName = "translatedRevisionCache")
  public Revision translateRevision(SVNConnection connection, Revision revision, long headRevision) throws SventonException {
    if (logger.isDebugEnabled()) {
      logger.debug("Delegating call to: " + getMethodName(Thread.currentThread().getStackTrace()) + ", " +
          "revision=[" + revision + "], " +
          "headRevision=[" + headRevision + "]");
    }
    return delegate.translateRevision(connection, revision, headRevision);
  }

  @Override
  @Cacheable(cacheName = "logEntryCache")
  public LogEntry getLogEntry(SVNConnection connection, RepositoryName repositoryName, long revision) throws SventonException {
    if (logger.isDebugEnabled()) {
      logger.debug("Delegating call to: " + getMethodName(Thread.currentThread().getStackTrace()) + ", " +
          "revision=[" + revision + "], " +
          "name=[" + repositoryName + "]");
    }
    return delegate.getLogEntry(connection, repositoryName, revision);
  }

  @Override
  @Cacheable(cacheName = "nodeKindCache")
  public DirEntry.Kind getNodeKind(SVNConnection connection, String path, long revision) throws SventonException {
    if (logger.isDebugEnabled()) {
      logger.debug("Delegating call to: " + getMethodName(Thread.currentThread().getStackTrace()) + ", " +
          "path=[" + path + "], " +
          "revision=[" + revision + "]");
    }
    return delegate.getNodeKind(connection, path, revision);
  }

  @Override
  @Cacheable(cacheName = "nodeKindForDiffCache")
  public DirEntry.Kind getNodeKindForDiff(SVNConnection connection, PathRevision from, PathRevision to, Revision pegRevision) throws SventonException {
    if (logger.isDebugEnabled()) {
      logger.debug("Delegating call to: " + getMethodName(Thread.currentThread().getStackTrace()) + ", " +
          "from=[" + from + "], " +
          "to=[" + to + "], " +
          "pegRevision=[" + pegRevision + "]");
    }
    return delegate.getNodeKindForDiff(connection, from, to, pegRevision);
  }

  @Override
  @Cacheable(cacheName = "fileRevisionsCache")
  public List<FileRevision> getFileRevisions(SVNConnection connection, String path, long revision) throws SventonException {
    if (logger.isDebugEnabled()) {
      logger.debug("Delegating call to: " + getMethodName(Thread.currentThread().getStackTrace()) + ", " +
          "path=[" + path + "], " +
          "revision=[" + revision + "]");
    }
    return delegate.getFileRevisions(connection, path, revision);
  }

  @Override
  @Cacheable(cacheName = "propertiesCache")
  public Properties listProperties(SVNConnection connection, String path, long revision) throws SventonException {
    if (logger.isDebugEnabled()) {
      logger.debug("Delegating call to: " + getMethodName(Thread.currentThread().getStackTrace()) + ", " +
          "revision=[" + revision + "], " +
          "name=[" + path + "]");
    }
    return delegate.listProperties(connection, path, revision);
  }

  @Override
  @Cacheable(cacheName = "revisionsForPathCache")
  public List<Long> getRevisionsForPath(SVNConnection connection, String path, long fromRevision, long toRevision, boolean stopOnCopy, long limit) throws SventonException {
    if (logger.isDebugEnabled()) {
      logger.debug("Delegating call to: " + getMethodName(Thread.currentThread().getStackTrace()) + ", " +
          "path=[" + path + "], " +
          "from=[" + fromRevision + "], " +
          "to=[" + toRevision + "]" +
          "stopOnCopy=[" + stopOnCopy + "]" +
          "limit=[" + limit + "]");
    }
    return delegate.getRevisionsForPath(connection, path, fromRevision, toRevision, stopOnCopy, limit);
  }

  @Override
  @Cacheable(cacheName = "entryInfoCache")
  public DirEntry getEntryInfo(SVNConnection connection, String path, long revision) throws SventonException {
    if (logger.isDebugEnabled()) {
      logger.debug("Delegating call to: " + getMethodName(Thread.currentThread().getStackTrace()) + ", " +
          "path=[" + path + "], " +
          "revision=[" + revision + "]");
    }
    return delegate.getEntryInfo(connection, path, revision);
  }

  @Override
  @Cacheable(cacheName = "logEntriesCache")
  public List<LogEntry> getLogEntries(SVNConnection connection, RepositoryName repositoryName, long fromRevision, long toRevision, String path, long limit, boolean stopOnCopy, boolean includeChangedPaths) throws SventonException {
    return delegate.getLogEntries(connection, repositoryName, fromRevision, toRevision, path, limit, stopOnCopy, includeChangedPaths);
  }

  @Override
  @Cacheable(cacheName = "listCache")
  public DirList list(SVNConnection connection, String path, long revision) throws SventonException {
    if (logger.isDebugEnabled()) {
      logger.debug("Delegating call to: " + getMethodName(Thread.currentThread().getStackTrace()) + ", " +
          "path=[" + path + "], " +
          "revision=[" + revision + "]");
    }
    return delegate.list(connection, path, revision);
  }


  // Methods not suitable for caching...

  @Override
  public List<SideBySideDiffRow> diffSideBySide(SVNConnection connection, PathRevision from, PathRevision to, Revision pegRevision, String charset) throws SventonException {
    return delegate.diffSideBySide(connection, from, to, pegRevision, charset);
  }

  @Override
  public String diffUnified(SVNConnection connection, PathRevision from, PathRevision to, Revision pegRevision, String charset) throws SventonException {
    return delegate.diffUnified(connection, from, to, pegRevision, charset);
  }

  @Override
  public List<InlineDiffRow> diffInline(SVNConnection connection, PathRevision from, PathRevision to, Revision pegRevision, String charset) throws SventonException {
    return delegate.diffInline(connection, from, to, pegRevision, charset);
  }

  @Override
  public List<DiffStatus> diffPaths(SVNConnection connection, PathRevision from, PathRevision to) throws SventonException {
    return delegate.diffPaths(connection, from, to);
  }

  @Override
  public AnnotatedTextFile blame(SVNConnection connection, String path, long revision, String charset, Colorer colorer) throws SventonException {
    return delegate.blame(connection, path, revision, charset, colorer);
  }

  @Override
  public void export(SVNConnection connection, List<PathRevision> targets, long pegRevision, File exportDirectory) throws SventonException {
    delegate.export(connection, targets, pegRevision, exportDirectory);
  }

  @Override
  public void getFileContents(SVNConnection connection, String path, long revision, OutputStream output) throws SventonException {
    delegate.getFileContents(connection, path, revision, output);
  }

  @Override
  public List<LogEntry> getLatestRevisions(SVNConnection connection, RepositoryName repositoryName, int revisionCount) throws SventonException {
    return delegate.getLatestRevisions(connection, repositoryName, revisionCount);
  }

  @Override
  public Map<String, DirEntryLock> getLocks(SVNConnection connection, String startPath, boolean recursive) {
    return delegate.getLocks(connection, startPath, recursive);
  }

  @Override
  public List<LogEntry> getLogEntriesFromRepositoryRoot(SVNConnection connection, long fromRevision, long toRevision) throws SventonException {
    return delegate.getLogEntriesFromRepositoryRoot(connection, fromRevision, toRevision);
  }

  private String getMethodName(StackTraceElement[] element) {
    return element[1].getMethodName();
  }

}
