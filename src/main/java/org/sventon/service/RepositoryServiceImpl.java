/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.service;

import de.regnis.q.sequence.line.diff.QDiffGeneratorFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.SventonException;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.colorer.Colorer;
import org.sventon.diff.*;
import org.sventon.export.ExportDirectory;
import org.sventon.model.*;
import org.sventon.util.KeywordHandler;
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
public class RepositoryServiceImpl implements RepositoryService {

  /**
   * Logger for this class and subclasses.
   */
  final Log logger = LogFactory.getLog(getClass());

  /**
   * {@inheritDoc}
   */
  public SVNLogEntry getRevision(final RepositoryName repositoryName, final SVNRepository repository, final long revision)
      throws SVNException, SventonException {

    return (SVNLogEntry) repository.log(new String[]{"/"}, null, revision, revision,
        true, false).iterator().next();
  }

  /**
   * {@inheritDoc}
   */
  public final List<SVNLogEntry> getRevisionsFromRepository(final SVNRepository repository, final long fromRevision,
                                                            final long toRevision) throws SVNException {

    final List<SVNLogEntry> revisions = new ArrayList<SVNLogEntry>();
    repository.log(new String[]{"/"}, fromRevision, toRevision, true, false, new ISVNLogEntryHandler() {
      public void handleLogEntry(final SVNLogEntry logEntry) {
        revisions.add(logEntry);
      }
    });
    return revisions;
  }

  /**
   * {@inheritDoc}
   */
  public List<SVNLogEntry> getRevisions(final RepositoryName repositoryName, final SVNRepository repository,
                                        final long fromRevision, final long toRevision, final String path,
                                        final long limit, final boolean stopOnCopy) throws SVNException, SventonException {

    logger.debug("Fetching [" + limit + "] revisions in the interval [" + toRevision + "-" + fromRevision + "]");
    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    repository.log(new String[]{path}, fromRevision, toRevision, true, stopOnCopy, limit, new ISVNLogEntryHandler() {
      public void handleLogEntry(final SVNLogEntry logEntry) {
        logEntries.add(logEntry);
      }
    });
    return logEntries;
  }

  /**
   * {@inheritDoc}
   */
  public final void export(final SVNRepository repository, final List<SVNFileRevision> targets, final long pegRevision,
                           final ExportDirectory exportDirectory) throws SVNException {

    for (final SVNFileRevision fileRevision : targets) {
      logger.debug("Exporting file [" + fileRevision.getPath() + "] revision [" + fileRevision.getRevision() + "]");
      final File revisionRootDir = new File(exportDirectory.getDirectory(), String.valueOf(fileRevision.getRevision()));
      revisionRootDir.mkdirs();
      final File entryToExport = new File(revisionRootDir, fileRevision.getPath());
      SVNClientManager.newInstance(null, repository.getAuthenticationManager()).getUpdateClient().doExport(
          SVNURL.parseURIDecoded(repository.getLocation().toDecodedString() + fileRevision.getPath()), entryToExport,
          SVNRevision.create(pegRevision), SVNRevision.create(fileRevision.getRevision()), null, true, SVNDepth.INFINITY);
    }
  }

  /**
   * {@inheritDoc}
   */
  public final TextFile getTextFile(final SVNRepository repository, final String path, final long revision,
                                    final String charset) throws SVNException, IOException {
    logger.debug("Fetching file " + path + "@" + revision);
    final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    getFile(repository, path, revision, outStream);
    return new TextFile(outStream.toString(charset));
  }

  /**
   * {@inheritDoc}
   */
  public final void getFile(final SVNRepository repository, final String path, final long revision,
                            final OutputStream output) throws SVNException {
    repository.getFile(path, revision, null, output);
  }

  /**
   * {@inheritDoc}
   */
  public final SVNProperties getFileProperties(final SVNRepository repository, final String path, final long revision) throws SVNException {
    final SVNProperties props = new SVNProperties();
    repository.getFile(path, revision, props, null);
    return props;
  }

  /**
   * {@inheritDoc}
   */
  public final boolean isTextFile(final SVNRepository repository, final String path, final long revision) throws SVNException {
    return SVNProperty.isTextMimeType(getFileProperties(repository, path, revision).getStringValue(SVNProperty.MIME_TYPE));
  }

  /**
   * {@inheritDoc}
   */
  public final String getFileChecksum(final SVNRepository repository, final String path, final long revision) throws SVNException {
    return getFileProperties(repository, path, revision).getStringValue(SVNProperty.CHECKSUM);
  }

  /**
   * {@inheritDoc}
   */
  public final long getLatestRevision(final SVNRepository repository) throws SVNException {
    return repository.getLatestRevision();
  }

  /**
   * {@inheritDoc}
   */
  public final SVNNodeKind getNodeKind(final SVNRepository repository, final String path, final long revision)
      throws SVNException {
    return repository.checkPath(path, revision);
  }

  /**
   * {@inheritDoc}
   */
  public Map<String, SVNLock> getLocks(final SVNRepository repository, final String startPath) throws SVNException {
    final String path = startPath == null ? "/" : startPath;
    logger.debug("Getting lock info for path [" + path + "] and below");

    final Map<String, SVNLock> locks = new HashMap<String, SVNLock>();
    SVNLock[] locksArray;

    try {
      locksArray = repository.getLocks(path);
      for (final SVNLock lock : locksArray) {
        logger.debug("Lock found: " + lock);
        locks.put(lock.getPath(), lock);
      }
    } catch (SVNException svne) {
      logger.debug("Unable to get locks for path [" + path + "]. Directory may not exist in HEAD", svne);
    }
    return locks;
  }

  /**
   * {@inheritDoc}
   */
  public SVNProperties getPathProperties(final SVNRepository repository, final String path, final long revision)
      throws SVNException {
    final SVNProperties properties = new SVNProperties();
    repository.getDir(path, revision, properties, (ISVNDirEntryHandler) null);
    return properties;
  }

  /**
   * {@inheritDoc}
   */
  public final List<RepositoryEntry> list(final SVNRepository repository, final String path, final long revision,
                                          final SVNProperties properties) throws SVNException {
    //noinspection unchecked
    final Collection<SVNDirEntry> entries = repository.getDir(path, revision, properties, (Collection) null);
    return RepositoryEntry.createEntryCollection(entries, path);
  }

  /**
   * {@inheritDoc}
   */
  public final RepositoryEntry getEntryInfo(final SVNRepository repository, final String path, final long revision)
      throws SVNException {

    final SVNDirEntry dirEntry = repository.info(path, revision);
    if (dirEntry != null) {
      return new RepositoryEntry(dirEntry, FilenameUtils.getFullPath(path));
    } else {
      logger.warn("Entry [" + path + "] does not exist in revision [" + revision + "]");
      throw new SVNException(SVNErrorMessage.create(SVNErrorCode.ENTRY_NOT_FOUND));
    }
  }

  /**
   * {@inheritDoc}
   */
  public final List<SVNFileRevision> getFileRevisions(final SVNRepository repository, final String path, final long revision)
      throws SVNException {

    //noinspection unchecked
    final List<SVNFileRevision> svnFileRevisions =
        (List<SVNFileRevision>) repository.getFileRevisions(path, null, 0, revision);

    if (logger.isDebugEnabled()) {
      final List<Long> fileRevisionNumbers = new ArrayList<Long>();
      for (final SVNFileRevision fileRevision : svnFileRevisions) {
        fileRevisionNumbers.add(fileRevision.getRevision());
      }
      logger.debug("Found revisions: " + fileRevisionNumbers);
    }
    return svnFileRevisions;
  }

  /**
   * {@inheritDoc}
   */
  public final List<SideBySideDiffRow> diffSideBySide(final SVNRepository repository, final DiffCommand command,
                                                      final SVNRevision pegRevision, final String charset,
                                                      final RepositoryConfiguration configuration) throws SVNException, DiffException {

    assertNotBinary(repository, command, pegRevision);

    String diffResultString;
    SideBySideDiffCreator sideBySideDiffCreator;

    try {
      final TextFile leftFile;
      final TextFile rightFile;

      final SVNProperties leftFileProperties;
      final SVNProperties rightFileProperties;

      if (SVNRevision.UNDEFINED == pegRevision) {
        leftFile = getTextFile(repository, command.getFromPath(), command.getFromRevision().getNumber(), charset);
        rightFile = getTextFile(repository, command.getToPath(), command.getToRevision().getNumber(), charset);
        leftFileProperties = getFileProperties(repository, command.getFromPath(), command.getFromRevision().getNumber());
        rightFileProperties = getFileProperties(repository, command.getToPath(), command.getToRevision().getNumber());
      } else {
        leftFile = getTextFile(repository, command.getFromPath(), pegRevision.getNumber(), charset);
        rightFile = getTextFile(repository, command.getToPath(), pegRevision.getNumber(), charset);
        leftFileProperties = getFileProperties(repository, command.getFromPath(), pegRevision.getNumber());
        rightFileProperties = getFileProperties(repository, command.getToPath(), pegRevision.getNumber());
      }

      final ByteArrayOutputStream diffResult = new ByteArrayOutputStream();
      final InputStream leftStream = new ByteArrayInputStream(leftFile.getContent().getBytes());
      final InputStream rightStream = new ByteArrayInputStream(rightFile.getContent().getBytes());
      final DiffProducer diffProducer = new DiffProducer(leftStream, rightStream, charset);

      diffProducer.doNormalDiff(diffResult);
      diffResultString = diffResult.toString(charset);

      if ("".equals(diffResultString)) {
        throw new IdenticalFilesException(command.getFromPath() + ", " + command.getToPath());
      }

      final KeywordHandler fromFileKeywordHandler =
          new KeywordHandler(leftFileProperties, configuration.getRepositoryUrl() + command.getFromPath());
      final KeywordHandler toFileKeywordHandler =
          new KeywordHandler(rightFileProperties, configuration.getRepositoryUrl() + command.getToPath());

      sideBySideDiffCreator = new SideBySideDiffCreator(leftFile, fromFileKeywordHandler, charset, rightFile,
          toFileKeywordHandler, charset);

    } catch (IOException ioex) {
      throw new DiffException("Unable to produce unified diff", ioex);
    }

    return sideBySideDiffCreator.createFromDiffResult(diffResultString);
  }

  /**
   * {@inheritDoc}
   */
  public final String diffUnified(final SVNRepository repository, final DiffCommand command, final SVNRevision pegRevision,
                                  final String charset, final RepositoryConfiguration configuration) throws SVNException, DiffException {

    assertNotBinary(repository, command, pegRevision);

    String diffResultString;

    try {
      final TextFile leftFile;
      final TextFile rightFile;

      if (SVNRevision.UNDEFINED == pegRevision) {
        leftFile = getTextFile(repository, command.getFromPath(), command.getFromRevision().getNumber(), charset);
        rightFile = getTextFile(repository, command.getToPath(), command.getToRevision().getNumber(), charset);
      } else {
        leftFile = getTextFile(repository, command.getFromPath(), pegRevision.getNumber(), charset);
        rightFile = getTextFile(repository, command.getToPath(), pegRevision.getNumber(), charset);
      }

      final ByteArrayOutputStream diffResult = new ByteArrayOutputStream();
      final DiffProducer diffProducer = new DiffProducer(new ByteArrayInputStream(leftFile.getContent().getBytes()),
          new ByteArrayInputStream(rightFile.getContent().getBytes()), charset);

      diffProducer.doUnifiedDiff(diffResult);

      diffResultString = diffResult.toString(charset);
      if ("".equals(diffResultString)) {
        throw new IdenticalFilesException(command.getFromPath() + ", " + command.getToPath());
      }

    } catch (final IOException ioex) {
      throw new DiffException("Unable to produce unified diff", ioex);
    }

    return diffResultString;
  }

  /**
   * {@inheritDoc}
   */
  public final List<InlineDiffRow> diffInline(final SVNRepository repository, final DiffCommand command, final SVNRevision pegRevision,
                                              final String charset, final RepositoryConfiguration configuration)
      throws SVNException, DiffException {

    assertNotBinary(repository, command, pegRevision);

    String diffResultString;
    final List<InlineDiffRow> resultRows = new ArrayList<InlineDiffRow>();

    try {
      final TextFile leftFile;
      final TextFile rightFile;

      if (SVNRevision.UNDEFINED == pegRevision) {
        leftFile = getTextFile(repository, command.getFromPath(), command.getFromRevision().getNumber(), charset);
        rightFile = getTextFile(repository, command.getToPath(), command.getToRevision().getNumber(), charset);
      } else {
        leftFile = getTextFile(repository, command.getFromPath(), pegRevision.getNumber(), charset);
        rightFile = getTextFile(repository, command.getToPath(), pegRevision.getNumber(), charset);
      }

      final ByteArrayOutputStream diffResult = new ByteArrayOutputStream();
      final Map generatorProperties = new HashMap();
      final int maxLines = Math.max(leftFile.getRows().size(), rightFile.getRows().size());
      //noinspection unchecked
      generatorProperties.put(QDiffGeneratorFactory.GUTTER_PROPERTY, maxLines);
      final DiffProducer diffProducer = new DiffProducer(new ByteArrayInputStream(leftFile.getContent().getBytes()),
          new ByteArrayInputStream(rightFile.getContent().getBytes()), charset, generatorProperties);

      diffProducer.doUnifiedDiff(diffResult);

      diffResultString = diffResult.toString(charset);
      if ("".equals(diffResultString)) {
        throw new IdenticalFilesException(command.getFromPath() + ", " + command.getToPath());
      }

      int rowNumberLeft = 1;
      int rowNumberRight = 1;
      //noinspection unchecked
      for (final String row : (List<String>) IOUtils.readLines(new StringReader(diffResultString))) {
        if (!row.startsWith("@@")) {
          final char action = row.charAt(0);
          switch (action) {
            case ' ':
              resultRows.add(new InlineDiffRow(rowNumberLeft, rowNumberRight, DiffAction.UNCHANGED, row.substring(1).trim()));
              rowNumberLeft++;
              rowNumberRight++;
              break;
            case '+':
              resultRows.add(new InlineDiffRow(null, rowNumberRight, DiffAction.ADDED, row.substring(1).trim()));
              rowNumberRight++;
              break;
            case '-':
              resultRows.add(new InlineDiffRow(rowNumberLeft, null, DiffAction.DELETED, row.substring(1).trim()));
              rowNumberLeft++;
              break;
            default:
              throw new IllegalArgumentException("Unknown action: " + action);
          }
        }
      }
    } catch (final IOException ioex) {
      throw new DiffException("Unable to produce inline diff", ioex);
    }
    return resultRows;
  }

  /**
   * {@inheritDoc}
   */
  public final List<SVNDiffStatus> diffPaths(final SVNRepository repository, final DiffCommand command, final SVNRevision pegRevision,
                                             final RepositoryConfiguration configuration) throws SVNException {

    final SVNDiffClient diffClient = SVNClientManager.newInstance(
        null, repository.getAuthenticationManager()).getDiffClient();

    final List<SVNDiffStatus> result = new ArrayList<SVNDiffStatus>();

    final String repoRoot = repository.getLocation().toDecodedString();

    diffClient.doDiffStatus(
        SVNURL.parseURIDecoded(repoRoot + command.getFromPath()), command.getFromRevision(),
        SVNURL.parseURIDecoded(repoRoot + command.getToPath()), command.getToRevision(),
        SVNDepth.INFINITY, false, new ISVNDiffStatusHandler() {
      public void handleDiffStatus(final SVNDiffStatus diffStatus) throws SVNException {
        if (diffStatus.getModificationType() != SVNStatusType.STATUS_NONE || diffStatus.isPropertiesModified()) {
          result.add(diffStatus);
        }
      }
    });
    return result;
  }

  /**
   * {@inheritDoc}
   */
  public final AnnotatedTextFile blame(final SVNRepository repository, final String path, final long revision,
                                       final String charset, final Colorer colorer) throws SVNException {

    final long blameRevision;
    if (SVNRevision.UNDEFINED.getNumber() == revision) {
      blameRevision = repository.getLatestRevision();
    } else {
      blameRevision = revision;
    }

    logger.debug("Blaming file [" + path + "] revision [" + revision + "]");

    final SVNProperties properties = getFileProperties(repository, path, revision);

    final AnnotatedTextFile annotatedTextFile = new AnnotatedTextFile(
        path, charset, colorer, properties, repository.getLocation().toDecodedString());

    final SVNLogClient logClient = SVNClientManager.newInstance(
        null, repository.getAuthenticationManager()).getLogClient();

    final AnnotationHandler handler = new AnnotationHandler(annotatedTextFile);
    final SVNRevision startRev = SVNRevision.create(0);
    final SVNRevision endRev = SVNRevision.create(blameRevision);

    logClient.doAnnotate(SVNURL.parseURIDecoded(repository.getLocation().toDecodedString() + path), endRev, startRev,
        endRev, false, handler, charset);
    try {
      annotatedTextFile.colorize();
    } catch (IOException ioex) {
      logger.warn("Unable to colorize [" + path + "]", ioex);
    }
    return annotatedTextFile;
  }

  /**
   * {@inheritDoc}
   */
  public SVNNodeKind getNodeKindForDiff(final SVNRepository repository, final DiffCommand command,
                                        final SVNRevision pegRevision) throws SVNException, DiffException {

    final long fromRevision;
    final long toRevision;

    if (SVNRevision.UNDEFINED.equals(pegRevision)) {
      fromRevision = command.getFromRevision().getNumber();
      toRevision = command.getToRevision().getNumber();
    } else {
      fromRevision = pegRevision.getNumber();
      toRevision = pegRevision.getNumber();
    }

    final SVNNodeKind nodeKind1 = getNodeKind(repository, command.getFromPath(), fromRevision);
    final SVNNodeKind nodeKind2 = getNodeKind(repository, command.getToPath(), toRevision);

    assertFileOrDir(nodeKind1, command.getFromPath(), fromRevision);
    assertFileOrDir(nodeKind2, command.getToPath(), toRevision);
    assertSameKind(nodeKind1, nodeKind2);
    return nodeKind1;
  }

  private void assertSameKind(final SVNNodeKind nodeKind1, final SVNNodeKind nodeKind2) throws DiffException {
    if (nodeKind1 != nodeKind2) {
      throw new DiffException("Entries are different kinds! " + nodeKind1 + "!=" + nodeKind2);
    }
  }

  private void assertFileOrDir(final SVNNodeKind nodeKind, final String path, final long revision) throws DiffException {
    if (SVNNodeKind.DIR != nodeKind && SVNNodeKind.FILE != nodeKind) {
      throw new DiffException("Path [" + path + "] does not exist as revision [" + revision + "]");
    }
  }

  private void assertNotBinary(final SVNRepository repository, final DiffCommand command, final SVNRevision pegRevision)
      throws SVNException, IllegalFileFormatException {

    final boolean isLeftFileTextType;
    final boolean isRightFileTextType;
    if (SVNRevision.UNDEFINED.equals(pegRevision)) {
      isLeftFileTextType = isTextFile(repository, command.getFromPath(), command.getFromRevision().getNumber());
      isRightFileTextType = isTextFile(repository, command.getToPath(), command.getToRevision().getNumber());
    } else {
      isLeftFileTextType = isTextFile(repository, command.getFromPath(), pegRevision.getNumber());
      isRightFileTextType = isTextFile(repository, command.getToPath(), pegRevision.getNumber());
    }

    if (!isLeftFileTextType && !isRightFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary files: " + command.getFromPath() + ", " + command.getToPath());
    } else if (!isLeftFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary file: " + command.getFromPath());
    } else if (!isRightFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary file: " + command.getToPath());
    }
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
