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
package org.sventon.service.svnkit;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.*;
import org.sventon.colorer.Colorer;
import org.sventon.diff.DiffException;
import org.sventon.diff.DiffProducer;
import org.sventon.diff.IdenticalFilesException;
import org.sventon.export.ExportDirectory;
import org.sventon.model.*;
import org.sventon.model.Properties;
import org.sventon.service.AbstractRepositoryService;
import org.sventon.web.command.DiffCommand;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.*;

import java.io.*;
import java.util.*;

/**
 * Service class for accessing the subversion repository.
 *
 * @author jesper@sventon.org
 */
public class SVNKitRepositoryService extends AbstractRepositoryService {

  /**
   * Logger for this class and subclasses.
   */
  final Log logger = LogFactory.getLog(getClass());

  @Override
  public LogEntry getLogEntry(final RepositoryName repositoryName, final SVNConnection connection, final long revision)
      throws SventonException {

    final SVNRepository repository = ((SVNKitConnection) connection).getDelegate();
    try {
      return SVNKitConverter.toLogEntry((SVNLogEntry) repository.log(new String[]{"/"}, null, revision, revision,
          true, false).iterator().next());
    } catch (SVNException ex) {
      return translateException("Error getting log entry: ", ex);
    }
  }

  @Override
  public final List<LogEntry> getLogEntriesFromRepositoryRoot(final SVNConnection connection, final long fromRevision,
                                                              final long toRevision) throws SventonException {

    final SVNRepository repository = ((SVNKitConnection) connection).getDelegate();
    final List<LogEntry> revisions = new ArrayList<LogEntry>();
    try {
      repository.log(new String[]{"/"}, fromRevision, toRevision, true, false, new ISVNLogEntryHandler() {
        public void handleLogEntry(final SVNLogEntry logEntry) {
          revisions.add(SVNKitConverter.toLogEntry(logEntry));
        }
      });
    } catch (SVNException ex) {
      return translateException("Unable to get logs", ex);
    }
    return revisions;
  }

  @Override
  public List<LogEntry> getLogEntries(final RepositoryName repositoryName, final SVNConnection connection,
                                      final long fromRevision, final long toRevision, final String path,
                                      final long limit, final boolean stopOnCopy, boolean includeChangedPaths)
      throws SventonException {

    logger.debug("Fetching [" + limit + "] revisions in the interval [" + toRevision + "-" + fromRevision + "]");
    final SVNRepository repository = ((SVNKitConnection) connection).getDelegate();
    final List<LogEntry> logEntries = new ArrayList<LogEntry>();
    try {
      repository.log(new String[]{path}, fromRevision, toRevision, includeChangedPaths, stopOnCopy, limit, new ISVNLogEntryHandler() {
        public void handleLogEntry(final SVNLogEntry logEntry) {
          logEntries.add(SVNKitConverter.toLogEntry(logEntry));
        }
      });
    } catch (SVNException ex) {
      return translateException("Unable to get logs", ex);
    }
    return logEntries;
  }

  @Override
  public final void export(final SVNConnection connection, final List<PathRevision> targets, final long pegRevision,
                           final ExportDirectory exportDirectory) throws SventonException {

    final SVNRepository repository = ((SVNKitConnection) connection).getDelegate();
    for (final PathRevision fileRevision : targets) {
      final String path = fileRevision.getPath();
      final long revision = fileRevision.getRevision().getNumber();
      final File revisionRootDir = new File(exportDirectory.getDirectory(), String.valueOf(revision));

      if (!revisionRootDir.exists() && !revisionRootDir.mkdirs()) {
        throw new RuntimeException("Unable to create directory: " + revisionRootDir.getAbsolutePath());
      }

      try {
        final File destination = new File(revisionRootDir, path);
        final SVNClientManager clientManager = SVNClientManager.newInstance(null, repository.getAuthenticationManager());
        final SVNUpdateClient updateClient = clientManager.getUpdateClient();
        final String pathToExport = repository.getLocation().toDecodedString() + path;

        logger.debug("Exporting file [" + pathToExport + "] revision [" + revision + "]");
        updateClient.doExport(org.tmatesoft.svn.core.SVNURL.parseURIDecoded(pathToExport), destination,
            SVNRevision.create(pegRevision), SVNRevision.create(revision), null, true, SVNDepth.INFINITY);
      } catch (SVNException ex) {
        translateException("Error exporting [" + path + "@" + revision + "]", ex);
      }
    }
  }


  @Override
  public void getFileContents(final SVNConnection connection, final String path, final long revision,
                              final OutputStream output) throws SventonException {
    final SVNRepository repository = ((SVNKitConnection) connection).getDelegate();
    try {
      final SVNClientManager clientManager = SVNClientManager.newInstance(null, repository.getAuthenticationManager());
      final SVNWCClient wcClient = clientManager.getWCClient();
      final org.tmatesoft.svn.core.SVNURL fileURL = org.tmatesoft.svn.core.SVNURL.parseURIDecoded(
          repository.getLocation().toDecodedString() + path);
      wcClient.doGetFileContents(fileURL, SVNRevision.create(revision), SVNRevision.create(revision), true, output);
    } catch (SVNException ex) {
      translateException("Cannot get contents of file [" + path + "@" + revision + "]", ex);
    }
  }

  @Override
  public final Properties listProperties(final SVNConnection connection, final String path, final long revision)
      throws SventonException {
    final SVNProperties props = new SVNProperties();
    final SVNRepository repository = ((SVNKitConnection) connection).getDelegate();
    try {
      repository.getFile(path, revision, props, null);
    } catch (SVNException e) {
      return translateException("Could not get file properties for " + path + " at revision " + revision, e);
    }

    final Properties properties = new Properties();
    for (Object o : props.nameSet()) {
      final String key = (String) o;
      final String value = SVNPropertyValue.getPropertyAsString(props.getSVNPropertyValue(key));
      properties.put(new Property(key), new PropertyValue(value));
    }

    return properties;
  }

  @Override
  public final Long getLatestRevision(final SVNConnection connection) throws SventonException {
    final SVNRepository repository = ((SVNKitConnection) connection).getDelegate();
    try {
      return repository.getLatestRevision();
    } catch (SVNException ex) {
      return translateException("Cannot get latest revision", ex);
    }
  }

  @Override
  public final DirEntry.Kind getNodeKind(final SVNConnection connection, final String path, final long revision)
      throws SventonException {
    try {
      final SVNRepository repository = ((SVNKitConnection) connection).getDelegate();
      final SVNNodeKind nodeKind = repository.checkPath(path, revision);
      return DirEntry.Kind.valueOf(nodeKind.toString().toUpperCase());
    } catch (SVNException svnex) {
      return translateException("Unable to get node kind for: " + path + "@" + revision, svnex);
    }
  }

  @Override
  public Map<String, DirEntryLock> getLocks(final SVNConnection connection, final String startPath, final boolean recursive) {
    final String path = startPath == null ? "/" : startPath;
    logger.debug("Getting lock info for path [" + path + "] and below");

    final Map<String, DirEntryLock> locks = new HashMap<String, DirEntryLock>();
    final SVNRepository repository = ((SVNKitConnection) connection).getDelegate();

    try {
      for (final SVNLock lock : repository.getLocks(path)) {
        logger.debug("Lock found: " + lock);
        final DirEntryLock dirEntryLock = new DirEntryLock(lock.getID(), lock.getPath(), lock.getOwner(),
            lock.getComment(), lock.getCreationDate(), lock.getExpirationDate());
        locks.put(lock.getPath(), dirEntryLock);
      }
    } catch (SVNException svne) {
      logger.debug("Unable to get locks for path [" + path + "]. Directory may not exist in HEAD", svne);
    }
    return locks;
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public final DirList list(final SVNConnection connection, final String path, final long revision
  ) throws SventonException {
    final SVNRepository repository = ((SVNKitConnection) connection).getDelegate();
    final SVNProperties properties = new SVNProperties();
    final Collection<SVNDirEntry> entries;
    try {
      entries = repository.getDir(path, revision, properties, (Collection) null);
    } catch (SVNException ex) {
      return translateException("Could not get directory listing from [" + path + "@" + revision + "]", ex);
    }

    return DirEntry.createDirectoryList(SVNKitConverter.convertDirEntries(entries, path), SVNKitConverter.convertProperties(properties));
  }

  @Override
  public final DirEntry getEntryInfo(final SVNConnection connection, final String path, final long revision)
      throws SventonException {

    final SVNDirEntry dirEntry;
    try {
      final SVNRepository repository = ((SVNKitConnection) connection).getDelegate();
      dirEntry = repository.info(path, revision);
    } catch (SVNException ex) {
      return translateException("Cannot get info for [" + path + "@" + revision + "]", ex);
    }

    if (dirEntry != null) {
      return SVNKitConverter.createDirEntry(dirEntry, FilenameUtils.getFullPath(path));
    } else {
      logger.warn("Entry [" + path + "] does not exist in revision [" + revision + "]");
      throw new DirEntryNotFoundException(path + "@" + revision);
    }
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public final List<FileRevision> getFileRevisions(final SVNConnection connection, final String path, final long revision)
      throws SventonException {

    //noinspection unchecked
    final List<SVNFileRevision> svnFileRevisions;
    try {
      final SVNRepository repository = ((SVNKitConnection) connection).getDelegate();
      svnFileRevisions = (List<SVNFileRevision>) repository.getFileRevisions(
          path, null, 0, revision);
    } catch (SVNException ex) {
      return translateException("Cannot get file revisions for [" + path + "@" + "]", ex);
    }

    if (logger.isDebugEnabled()) {
      final List<Long> fileRevisionNumbers = new ArrayList<Long>();
      for (final SVNFileRevision fileRevision : svnFileRevisions) {
        fileRevisionNumbers.add(fileRevision.getRevision());
      }
      logger.debug("Found revisions: " + fileRevisionNumbers);
    }
    return SVNKitConverter.convertFileRevisions(svnFileRevisions);
  }


  @Override
  public final String diffUnified(final SVNConnection connection, final DiffCommand command, final Revision pegRevision,
                                  final String charset) throws SventonException, DiffException {

    assertNotBinary(connection, command, pegRevision);

    try {
      final TextFile leftFile;
      final TextFile rightFile;

      if (Revision.UNDEFINED.equals(pegRevision)) {
        leftFile = getTextFile(connection, command.getFromPath(), command.getFromRevision().getNumber(), charset);
        rightFile = getTextFile(connection, command.getToPath(), command.getToRevision().getNumber(), charset);
      } else {
        leftFile = getTextFile(connection, command.getFromPath(), pegRevision.getNumber(), charset);
        rightFile = getTextFile(connection, command.getToPath(), pegRevision.getNumber(), charset);
      }
      return createUnifiedDiff(command, charset, leftFile, rightFile);
    } catch (final IOException ioex) {
      throw new DiffException("Unable to produce unified diff", ioex);
    }
  }

  @Override
  public final List<DiffStatus> diffPaths(final SVNConnection connection, final DiffCommand command)
      throws SventonException {

    final SVNRepository repository = ((SVNKitConnection) connection).getDelegate();
    final SVNClientManager clientManager = SVNClientManager.newInstance(null, repository.getAuthenticationManager());
    final SVNDiffClient diffClient = clientManager.getDiffClient();
    final List<DiffStatus> result = new ArrayList<DiffStatus>();
    final String repoRoot = repository.getLocation().toDecodedString();

    try {
      diffClient.doDiffStatus(
          org.tmatesoft.svn.core.SVNURL.parseURIDecoded(repoRoot + command.getFromPath()), SVNRevision.parse(command.getFromRevision().toString()),
          org.tmatesoft.svn.core.SVNURL.parseURIDecoded(repoRoot + command.getToPath()), SVNRevision.parse(command.getToRevision().toString()),
          SVNDepth.INFINITY, false, new ISVNDiffStatusHandler() {
            public void handleDiffStatus(final org.tmatesoft.svn.core.wc.SVNDiffStatus diffStatus) throws SVNException {
              if (diffStatus.getModificationType() != org.tmatesoft.svn.core.wc.SVNStatusType.STATUS_NONE || diffStatus.isPropertiesModified()) {
                result.add(new DiffStatus(ChangeType.parse(diffStatus.getModificationType().getCode()),
                    diffStatus.getPath(), diffStatus.isPropertiesModified()));
              }
            }
          });
    } catch (SVNException e) {
      return translateException("Could not calculate diff for " + command.toString(), e);
    }
    return result;
  }

  @Override
  public final AnnotatedTextFile blame(final SVNConnection connection, final String path, final long revision,
                                       final String charset, final Colorer colorer) throws SventonException {

    try {
      final SVNRepository repository = ((SVNKitConnection) connection).getDelegate();
      final long blameRevision;
      if (Revision.UNDEFINED.getNumber() == revision) {
        blameRevision = repository.getLatestRevision();
      } else {
        blameRevision = revision;
      }

      logger.debug("Blaming file [" + path + "] revision [" + revision + "]");
      final AnnotatedTextFile annotatedTextFile = new AnnotatedTextFile(path, charset, colorer);
      final SVNClientManager clientManager = SVNClientManager.newInstance(null, repository.getAuthenticationManager());
      final SVNLogClient logClient = clientManager.getLogClient();
      final AnnotationHandler handler = new AnnotationHandler(annotatedTextFile);
      final SVNRevision startRev = SVNRevision.create(0);
      final SVNRevision endRev = SVNRevision.create(blameRevision);

      logClient.doAnnotate(org.tmatesoft.svn.core.SVNURL.parseURIDecoded(repository.getLocation().toDecodedString() + path), endRev, startRev,
          endRev, false, handler, charset);
      try {
        annotatedTextFile.colorize();
      } catch (IOException ioex) {
        logger.warn("Unable to colorize [" + path + "]", ioex);
      }
      return annotatedTextFile;
    } catch (SVNException ex) {
      return translateException("Error blaming [" + path + "@" + revision + "]", ex);
    }
  }

  @Override
  public Revision translateRevision(final Revision revision, final long headRevision, final SVNConnection connection) throws SventonException {
    final long revisionNumber = revision.getNumber();

    try {
      if (revision.isHeadRevision() || revisionNumber == headRevision) {
        return Revision.createHeadRevision(headRevision);
      }

      if (revisionNumber < 0) {
        final Date date = revision.getDate();
        if (date != null) {
          final SVNRepository repository = ((SVNKitConnection) connection).getDelegate();
          return Revision.create(repository.getDatedRevision(date));
        } else {
          logger.warn("Unexpected revision: " + revision);
          return Revision.createHeadRevision(headRevision);
        }
      }
      return Revision.create(revisionNumber);
    } catch (SVNException ex) {
      return translateException("Unable to translate revision: " + revision, ex);
    }
  }


  @Override
  public List<Long> getRevisionsForPath(final SVNConnection connection, final String path, final long fromRevision,
                                        final long toRevision, final boolean stopOnCopy, final long limit)
      throws SventonException {
    final List<Long> revisions = new ArrayList<Long>();

    try {
      final SVNRepository repository = ((SVNKitConnection) connection).getDelegate();
      repository.log(new String[]{path}, fromRevision, toRevision, false, stopOnCopy, limit, new ISVNLogEntryHandler() {
        public void handleLogEntry(final SVNLogEntry logEntry) {
          revisions.add(logEntry.getRevision());
        }
      });
    } catch (SVNException ex) {
      return translateException("Unable to get logs for path: " + path, ex);
    }
    return revisions;
  }

  protected String createUnifiedDiff(DiffCommand command, String charset, TextFile leftFile, TextFile rightFile) throws IOException {
    final ByteArrayOutputStream diffResult = new ByteArrayOutputStream();
    final DiffProducer diffProducer = new DiffProducer(new ByteArrayInputStream(leftFile.getContent().getBytes()),
        new ByteArrayInputStream(rightFile.getContent().getBytes()), charset);

    diffProducer.doUnifiedDiff(diffResult);

    final String diffResultString = diffResult.toString(charset);
    if ("".equals(diffResultString)) {
      throw new IdenticalFilesException(command.getFromPath() + ", " + command.getToPath());
    }
    return diffResultString;
  }

  private <T extends Object> T translateException(String errorMessage, SVNException exception) throws SventonException {
    if (exception instanceof SVNAuthenticationException) {
      throw new AuthenticationException(exception.getMessage(), exception);
    }

    if (SVNErrorCode.FS_NO_SUCH_REVISION == exception.getErrorMessage().getErrorCode()) {
      throw new NoSuchRevisionException("No such revision: " + exception.getMessage());
    }

    throw new SventonException(errorMessage, exception);
  }

  private static class AnnotationHandler implements ISVNAnnotateHandler {

    private final AnnotatedTextFile annotatedTextFile;

    /**
     * Constructor.
     *
     * @param annotatedTextFile File
     */
    public AnnotationHandler(final AnnotatedTextFile annotatedTextFile) {
      this.annotatedTextFile = annotatedTextFile;
    }

    @Deprecated
    public void handleLine(final Date date, final long revision, final String author, final String line)
        throws SVNException {
      handleLine(date, revision, author, line, null, -1, null, null, 0);
    }

    public void handleLine(final Date date, final long revision, final String author, final String line,
                           final Date mergedDate, final long mergedRevision, final String mergedAuthor,
                           final String mergedPath, final int lineNumber) throws SVNException {
      annotatedTextFile.addRow(date, revision, author, line);
    }

    public boolean handleRevision(Date date, long revision, String author, File contents) throws SVNException {
      // We do not want our file to be annotated for each revision of the range, but only for the last
      // revision of it, so we return false
      return false;
    }

    public void handleEOF() {
      // Nothing to do.
    }

  }
}
