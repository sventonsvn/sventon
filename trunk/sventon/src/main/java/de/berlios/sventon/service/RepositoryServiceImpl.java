/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.service;

import de.berlios.sventon.SventonException;
import de.berlios.sventon.appl.RepositoryConfiguration;
import de.berlios.sventon.appl.RepositoryName;
import de.berlios.sventon.colorer.Colorer;
import de.berlios.sventon.content.KeywordHandler;
import de.berlios.sventon.diff.*;
import de.berlios.sventon.model.*;
import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.cache.objectcache.ObjectCache;
import de.berlios.sventon.repository.cache.objectcache.ObjectCacheKey;
import de.berlios.sventon.repository.export.ExportDirectory;
import de.berlios.sventon.util.ImageScaler;
import de.berlios.sventon.util.PathUtil;
import de.berlios.sventon.web.command.DiffCommand;
import de.regnis.q.sequence.line.diff.QDiffGeneratorFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.*;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Service class for accessing the subversion repository.
 *
 * @author jesper@users.berlios.de
 */
public class RepositoryServiceImpl implements RepositoryService {

  /**
   * Logger for this class and subclasses.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * {@inheritDoc}
   */
  public SVNLogEntry getRevision(final RepositoryName repositoryName, final SVNRepository repository, final long revision)
      throws SVNException, SventonException {

    final long start = System.currentTimeMillis();
    final SVNLogEntry logEntry = (SVNLogEntry) repository.log(new String[]{"/"}, null, revision, revision,
        true, false).iterator().next();
    logger.debug("PERF: getRevision(): " + (System.currentTimeMillis() - start));
    return logEntry;
  }

  /**
   * {@inheritDoc}
   */
  public final List<SVNLogEntry> getRevisionsFromRepository(final SVNRepository repository, final long fromRevision,
                                                            final long toRevision) throws SVNException {

    final long start = System.currentTimeMillis();
    final List<SVNLogEntry> revisions = new ArrayList<SVNLogEntry>();
    repository.log(new String[]{"/"}, fromRevision, toRevision, true, false, new ISVNLogEntryHandler() {
      public void handleLogEntry(final SVNLogEntry logEntry) {
        revisions.add(logEntry);
      }
    });
    logger.debug("PERF: getRevisionsFromRepository(): " + (System.currentTimeMillis() - start));
    return revisions;
  }

  /**
   * {@inheritDoc}
   */
  public List<SVNLogEntry> getRevisions(final RepositoryName repositoryName, final SVNRepository repository,
                                        final long fromRevision, final long toRevision, final String path,
                                        final long limit) throws SVNException, SventonException {

    final long start = System.currentTimeMillis();
    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    repository.log(new String[]{path}, fromRevision, toRevision, true, false, limit, new ISVNLogEntryHandler() {
      public void handleLogEntry(final SVNLogEntry logEntry) {
        logEntries.add(logEntry);
      }
    });
    logger.debug("PERF: getRevisions(): " + (System.currentTimeMillis() - start));
    return logEntries;
  }

  /**
   * {@inheritDoc}
   */
  public final void export(final SVNRepository repository, final List<SVNFileRevision> targets, final long pegRevision,
                           final ExportDirectory exportDirectory) throws SVNException {

    final long start = System.currentTimeMillis();
    for (final SVNFileRevision fileRevision : targets) {
      logger.debug("Exporting file [" + fileRevision.getPath() + "] revision [" + fileRevision.getRevision() + "]");
      final File revisionRootDir = new File(exportDirectory.getFile(), String.valueOf(fileRevision.getRevision()));
      revisionRootDir.mkdirs();
      final File entryToExport = new File(revisionRootDir, fileRevision.getPath());
      SVNClientManager.newInstance(null, repository.getAuthenticationManager()).getUpdateClient().doExport(
          SVNURL.parseURIDecoded(repository.getLocation().toDecodedString() + fileRevision.getPath()), entryToExport,
          SVNRevision.create(pegRevision), SVNRevision.create(fileRevision.getRevision()), null, true, true);
    }
    logger.debug("PERF: export(): " + (System.currentTimeMillis() - start));
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
    getFile(repository, path, revision, output, null);
  }

  /**
   * {@inheritDoc}
   */
  public final void getFile(final SVNRepository repository, final String path, final long revision,
                            final OutputStream output, final Map properties) throws SVNException {
    final long start = System.currentTimeMillis();
    repository.getFile(path, revision, properties, output);
    logger.debug("PERF: getFile(): " + (System.currentTimeMillis() - start));
  }

  /**
   * {@inheritDoc}
   */
  public final Map getFileProperties(final SVNRepository repository, final String path, final long revision) throws SVNException {
    final Map props = new HashMap();
    final long start = System.currentTimeMillis();
    repository.getFile(path, revision, props, null);
    logger.debug("PERF: getFileProperties(): " + (System.currentTimeMillis() - start));
    return props;
  }

  /**
   * {@inheritDoc}
   */
  public final boolean isTextFile(final SVNRepository repository, final String path, final long revision) throws SVNException {
    return SVNProperty.isTextMimeType((String) getFileProperties(repository, path, revision).get(SVNProperty.MIME_TYPE));
  }

  /**
   * {@inheritDoc}
   */
  public final String getFileChecksum(final SVNRepository repository, final String path, final long revision) throws SVNException {
    return (String) getFileProperties(repository, path, revision).get(SVNProperty.CHECKSUM);
  }

  /**
   * {@inheritDoc}
   */
  public final long getLatestRevision(final SVNRepository repository) throws SVNException {
    final long start = System.currentTimeMillis();
    final long revision = repository.getLatestRevision();
    logger.debug("PERF: getLatestRevision(): " + (System.currentTimeMillis() - start));
    return revision;
  }

  /**
   * {@inheritDoc}
   */
  public final SVNNodeKind getNodeKind(final SVNRepository repository, final String path, final long revision)
      throws SVNException {
    final long start = System.currentTimeMillis();
    final SVNNodeKind svnNodeKind = repository.checkPath(path, revision);
    logger.debug("PERF: getNodeKind(): " + (System.currentTimeMillis() - start));
    return svnNodeKind;
  }

  /**
   * {@inheritDoc}
   */
  public Map<String, SVNLock> getLocks(final SVNRepository repository, final String startPath) throws SVNException {
    final String path = startPath == null ? "/" : startPath;
    logger.debug("Getting lock info for path [" + path + "] and below");

    final Map<String, SVNLock> locks = new HashMap<String, SVNLock>();
    SVNLock[] locksArray;

    final long start = System.currentTimeMillis();
    try {
      locksArray = repository.getLocks(path);
      for (final SVNLock lock : locksArray) {
        logger.debug("Lock found: " + lock);
        locks.put(lock.getPath(), lock);
      }
    } catch (SVNException svne) {
      logger.debug("Unable to get locks for path [" + path + "]. Directory may not exist in HEAD");
    }
    logger.debug("PERF: getLocks(): " + (System.currentTimeMillis() - start));
    return locks;
  }

  /**
   * {@inheritDoc}
   */
  public final List<RepositoryEntry> list(final SVNRepository repository, final String path, final long revision,
                                          final Map properties) throws SVNException {
    final long start = System.currentTimeMillis();
    //noinspection unchecked
    final Collection<SVNDirEntry> entries = repository.getDir(path, revision, properties, (Collection) null);
    final List<RepositoryEntry> entryCollection = RepositoryEntry.createEntryCollection(entries, path);
    logger.debug("PERF: list(): " + (System.currentTimeMillis() - start));
    return entryCollection;
  }

  /**
   * {@inheritDoc}
   */
  public final RepositoryEntry getEntryInfo(final SVNRepository repository, final String path, final long revision)
      throws SVNException {

    final long start = System.currentTimeMillis();
    final SVNDirEntry dirEntry = repository.info(path, revision);
    if (dirEntry != null) {
      final RepositoryEntry repositoryEntry = new RepositoryEntry(dirEntry, PathUtil.getPathPart(path));
      logger.debug("PERF: getEntryInfo(): " + (System.currentTimeMillis() - start));
      return repositoryEntry;
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

    final long start = System.currentTimeMillis();
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

    logger.debug("PERF: getFileRevisions(): " + (System.currentTimeMillis() - start));
    return svnFileRevisions;
  }

  /**
   * {@inheritDoc}
   */
  public final List<SideBySideDiffRow> diffSideBySide(final SVNRepository repository, final DiffCommand diffCommand,
                                                      final SVNRevision pegRevision, final String charset,
                                                      final RepositoryConfiguration configuration) throws SVNException, DiffException {

    assertNotBinary(repository, diffCommand, pegRevision);

    String diffResultString;
    SideBySideDiffCreator sideBySideDiffCreator;

    try {
      final TextFile leftFile;
      final TextFile rightFile;

      final Map leftFileProperties;
      final Map rightFileProperties;

      if (SVNRevision.UNDEFINED == pegRevision) {
        leftFile = getTextFile(repository, diffCommand.getFromPath(), diffCommand.getFromRevision().getNumber(), charset);
        rightFile = getTextFile(repository, diffCommand.getToPath(), diffCommand.getToRevision().getNumber(), charset);
        leftFileProperties = getFileProperties(repository, diffCommand.getFromPath(), diffCommand.getFromRevision().getNumber());
        rightFileProperties = getFileProperties(repository, diffCommand.getToPath(), diffCommand.getToRevision().getNumber());
      } else {
        leftFile = getTextFile(repository, diffCommand.getFromPath(), pegRevision.getNumber(), charset);
        rightFile = getTextFile(repository, diffCommand.getToPath(), pegRevision.getNumber(), charset);
        leftFileProperties = getFileProperties(repository, diffCommand.getFromPath(), pegRevision.getNumber());
        rightFileProperties = getFileProperties(repository, diffCommand.getToPath(), pegRevision.getNumber());
      }

      final ByteArrayOutputStream diffResult = new ByteArrayOutputStream();
      final InputStream leftStream = new ByteArrayInputStream(leftFile.getContent().getBytes());
      final InputStream rightStream = new ByteArrayInputStream(rightFile.getContent().getBytes());
      final DiffProducer diffProducer = new DiffProducer(leftStream, rightStream, charset);

      diffProducer.doNormalDiff(diffResult);
      diffResultString = diffResult.toString(charset);

      if ("".equals(diffResultString)) {
        throw new IdenticalFilesException(diffCommand.getFromPath() + ", " + diffCommand.getToPath());
      }

      final KeywordHandler fromFileKeywordHandler =
          new KeywordHandler(leftFileProperties, configuration.getRepositoryUrl() + diffCommand.getFromPath());
      final KeywordHandler toFileKeywordHandler =
          new KeywordHandler(rightFileProperties, configuration.getRepositoryUrl() + diffCommand.getToPath());

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
  public final String diffUnified(final SVNRepository repository, final DiffCommand diffCommand, final SVNRevision pegRevision,
                                  final String charset, final RepositoryConfiguration configuration) throws SVNException, DiffException {

    assertNotBinary(repository, diffCommand, pegRevision);

    String diffResultString;

    try {
      final TextFile leftFile;
      final TextFile rightFile;

      if (SVNRevision.UNDEFINED == pegRevision) {
        leftFile = getTextFile(repository, diffCommand.getFromPath(), diffCommand.getFromRevision().getNumber(), charset);
        rightFile = getTextFile(repository, diffCommand.getToPath(), diffCommand.getToRevision().getNumber(), charset);
      } else {
        leftFile = getTextFile(repository, diffCommand.getFromPath(), pegRevision.getNumber(), charset);
        rightFile = getTextFile(repository, diffCommand.getToPath(), pegRevision.getNumber(), charset);
      }

      final ByteArrayOutputStream diffResult = new ByteArrayOutputStream();
      final DiffProducer diffProducer = new DiffProducer(new ByteArrayInputStream(leftFile.getContent().getBytes()),
          new ByteArrayInputStream(rightFile.getContent().getBytes()), charset);

      diffProducer.doUnifiedDiff(diffResult);

      diffResultString = diffResult.toString(charset);
      if ("".equals(diffResultString)) {
        throw new IdenticalFilesException(diffCommand.getFromPath() + ", " + diffCommand.getToPath());
      }

    } catch (final IOException ioex) {
      throw new DiffException("Unable to produce unified diff", ioex);
    }

    return diffResultString;
  }

  /**
   * {@inheritDoc}
   */
  public final List<InlineDiffRow> diffInline(final SVNRepository repository, final DiffCommand diffCommand, final SVNRevision pegRevision,
                                              final String charset, final RepositoryConfiguration configuration)
      throws SVNException, DiffException {

    assertNotBinary(repository, diffCommand, pegRevision);

    String diffResultString;
    final List<InlineDiffRow> resultRows = new ArrayList<InlineDiffRow>();

    try {
      final TextFile leftFile;
      final TextFile rightFile;

      if (SVNRevision.UNDEFINED == pegRevision) {
        leftFile = getTextFile(repository, diffCommand.getFromPath(), diffCommand.getFromRevision().getNumber(), charset);
        rightFile = getTextFile(repository, diffCommand.getToPath(), diffCommand.getToRevision().getNumber(), charset);
      } else {
        leftFile = getTextFile(repository, diffCommand.getFromPath(), pegRevision.getNumber(), charset);
        rightFile = getTextFile(repository, diffCommand.getToPath(), pegRevision.getNumber(), charset);
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
        throw new IdenticalFilesException(diffCommand.getFromPath() + ", " + diffCommand.getToPath());
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
  public final List<SVNDiffStatus> diffPaths(final SVNRepository repository, final DiffCommand diffCommand, final SVNRevision pegRevision,
                                             final RepositoryConfiguration configuration) throws SVNException, DiffException {

    final long start = System.currentTimeMillis();
    final SVNDiffClient diffClient = SVNClientManager.newInstance(
        null, repository.getAuthenticationManager()).getDiffClient();

    final List<SVNDiffStatus> result = new ArrayList<SVNDiffStatus>();

    final String repoRoot = repository.getLocation().toDecodedString();
    diffClient.doDiffStatus(
        SVNURL.parseURIDecoded(repoRoot + diffCommand.getFromPath()), diffCommand.getFromRevision(),
        SVNURL.parseURIDecoded(repoRoot + diffCommand.getToPath()), diffCommand.getToRevision(),
        true, false, new ISVNDiffStatusHandler() {
      public void handleDiffStatus(final SVNDiffStatus diffStatus) throws SVNException {
        if (diffStatus.getModificationType() == SVNStatusType.STATUS_NONE && !diffStatus.isPropertiesModified()) {
          return;
        } else {
          result.add(diffStatus);
        }
      }
    });
    logger.debug("PERF: diffPaths(): " + (System.currentTimeMillis() - start));
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

    final long start = System.currentTimeMillis();

    logger.debug("Blaming file [" + path + "] revision [" + revision + "]");

    final Map properties = getFileProperties(repository, path, revision);
    final SVNLogClient logClient = SVNClientManager.newInstance(
        null, repository.getAuthenticationManager()).getLogClient();
    final AnnotatedTextFile annotatedTextFile = new AnnotatedTextFile(
        path, charset, colorer, properties, repository.getLocation().toDecodedString());
    final ISVNAnnotateHandler handler = new ISVNAnnotateHandler() {
      public void handleLine(final Date date, final long revision, final String author, final String line) {
        annotatedTextFile.addRow(date, revision, author, line);
      }
    };

    final SVNRevision startRev = SVNRevision.create(0);
    final SVNRevision endRev = SVNRevision.create(blameRevision);

    logClient.doAnnotate(SVNURL.parseURIDecoded(repository.getLocation().toDecodedString() + path), endRev, startRev,
        endRev, false, handler, charset);
    try {
      annotatedTextFile.colorize();
    } catch (IOException ioex) {
      logger.warn("Unable to colorize [" + path + "]", ioex);
    }

    logger.debug("PERF: blame(): " + (System.currentTimeMillis() - start));
    return annotatedTextFile;
  }

  private void assertNotBinary(final SVNRepository repository, final DiffCommand diffCommand, final SVNRevision pegRevision)
      throws SVNException, IllegalFileFormatException {

    final boolean isLeftFileTextType;
    final boolean isRightFileTextType;
    if (SVNRevision.UNDEFINED.equals(pegRevision)) {
      isLeftFileTextType = isTextFile(repository, diffCommand.getFromPath(), diffCommand.getFromRevision().getNumber());
      isRightFileTextType = isTextFile(repository, diffCommand.getToPath(), diffCommand.getToRevision().getNumber());
    } else {
      isLeftFileTextType = isTextFile(repository, diffCommand.getFromPath(), pegRevision.getNumber());
      isRightFileTextType = isTextFile(repository, diffCommand.getToPath(), pegRevision.getNumber());
    }

    if (!isLeftFileTextType && !isRightFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary files: " + diffCommand.getFromPath() + ", " + diffCommand.getToPath());
    } else if (!isLeftFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary file: " + diffCommand.getFromPath());
    } else if (!isRightFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary file: " + diffCommand.getToPath());
    }
  }

}
