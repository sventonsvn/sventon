/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.service;

import de.berlios.sventon.appl.Application;
import de.berlios.sventon.appl.InstanceConfiguration;
import de.berlios.sventon.content.KeywordHandler;
import de.berlios.sventon.diff.*;
import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.cache.CacheException;
import de.berlios.sventon.repository.cache.CacheGateway;
import de.berlios.sventon.repository.cache.objectcache.ObjectCache;
import de.berlios.sventon.repository.cache.objectcache.ObjectCacheKey;
import de.berlios.sventon.repository.export.ExportDirectory;
import de.berlios.sventon.util.ImageScaler;
import de.berlios.sventon.util.PathUtil;
import de.berlios.sventon.web.command.DiffCommand;
import de.berlios.sventon.web.model.ImageMetadata;
import de.berlios.sventon.web.model.RawTextFile;
import de.berlios.sventon.web.model.SideBySideDiffRow;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

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
   * The cache instance.
   */
  private CacheGateway cacheGateway;

  /**
   * The application.
   */
  private Application application;

  /**
   * Sets the application.
   *
   * @param application Application
   */
  public void setApplication(final Application application) {
    this.application = application;
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
   * {@inheritDoc}
   */
  public SVNLogEntry getRevision(final String instanceName, final SVNRepository repository, final long revision)
      throws SVNException, CacheException {
    final long start = System.currentTimeMillis();
    final SVNLogEntry logEntry;
    if (application.getInstance(instanceName).getConfiguration().isCacheUsed()) {
      logger.debug("Fetching cached revision: " + revision);
      logEntry = cacheGateway.getRevision(instanceName, revision);
    } else {
      logEntry = (SVNLogEntry) repository.log(
          new String[]{"/"}, null, revision, revision, true, false).iterator().next();
    }
    logger.debug("PERF: getRevision(): " + (System.currentTimeMillis() - start));
    return logEntry;
  }

  /**
   * {@inheritDoc}
   */
  public List<SVNLogEntry> getRevisionsFromRepository(final SVNRepository repository, final long fromRevision, final long toRevision)
      throws SVNException {
    final long start = System.currentTimeMillis();
    final List<SVNLogEntry> revisions = new ArrayList<SVNLogEntry>();
    repository.log(new String[]{"/"}, fromRevision, toRevision, true, false, -1, new ISVNLogEntryHandler() {
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
  public List<SVNLogEntry> getRevisions(final String instanceName, final SVNRepository repository,
                                        final long fromRevision, final long toRevision, final String path,
                                        final long limit) throws SVNException, CacheException {

    final long start = System.currentTimeMillis();
    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    if (application.getInstance(instanceName).getConfiguration().isCacheUsed()) {
      // To be able to return cached revisions, we first have to get the revision numbers
      // Doing a logs-call, skipping the details, to get them.
      final List<Long> revisions = new ArrayList<Long>();
      repository.log(new String[]{path}, fromRevision, toRevision, false, false, limit, new ISVNLogEntryHandler() {
        public void handleLogEntry(final SVNLogEntry logEntry) {
          revisions.add(logEntry.getRevision());
        }
      });
      logger.debug("Fetching cached revisions [" + toRevision + "-" + fromRevision + "]");
      logEntries.addAll(cacheGateway.getRevisions(instanceName, revisions));
    } else {
      repository.log(new String[]{path}, fromRevision, toRevision, true, false, limit, new ISVNLogEntryHandler() {
        public void handleLogEntry(final SVNLogEntry logEntry) {
          logEntries.add(logEntry);
        }
      });
    }
    logger.debug("PERF: getRevisions(): " + (System.currentTimeMillis() - start));
    return logEntries;
  }

  /**
   * {@inheritDoc}
   */
  public void export(final SVNRepository repository, final List<String> targets, final long revision,
                     final ExportDirectory exportDirectory) throws SVNException {

    final long start = System.currentTimeMillis();
    long exportRevision = revision;
    if (exportRevision == -1) {
      exportRevision = repository.getLatestRevision();
    }
    for (final String target : targets) {
      logger.debug("Exporting file [" + target + "] revision [" + exportRevision + "]");
      final File entryToExport = new File(exportDirectory.getFile(), target);
      SVNClientManager.newInstance(null, repository.getAuthenticationManager()).getUpdateClient().doExport(
          SVNURL.parseURIDecoded(repository.getLocation().toDecodedString() + target), entryToExport,
          SVNRevision.create(exportRevision), SVNRevision.create(exportRevision), null, true, true);
    }
    logger.debug("PERF: export(): " + (System.currentTimeMillis() - start));
  }

  /**
   * {@inheritDoc}
   */
  public RawTextFile getTextFile(final SVNRepository repository, final String path, final long revision, final String charset)
      throws SVNException, UnsupportedEncodingException {

    final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    getFile(repository, path, revision, outStream);
    return new RawTextFile(outStream.toString(charset), true);
  }

  /**
   * {@inheritDoc}
   */
  public void getFile(final SVNRepository repository, final String path, final long revision,
                      final OutputStream output) throws SVNException {
    getFile(repository, path, revision, output, null);
  }

  /**
   * {@inheritDoc}
   */
  public void getFile(final SVNRepository repository, final String path, final long revision,
                      final OutputStream output, final Map properties) throws SVNException {
    final long start = System.currentTimeMillis();
    repository.getFile(path, revision, properties, output);
    logger.debug("PERF: getFile(): " + (System.currentTimeMillis() - start));
  }

  /**
   * {@inheritDoc}
   */
  public Map getFileProperties(final SVNRepository repository, final String path, final long revision) throws SVNException {
    final Map props = new HashMap();
    final long start = System.currentTimeMillis();
    repository.getFile(path, revision, props, null);
    logger.debug("PERF: getFileProperties(): " + (System.currentTimeMillis() - start));
    return props;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isTextFile(final SVNRepository repository, final String path, final long revision) throws SVNException {
    return SVNProperty.isTextMimeType((String) getFileProperties(repository, path, revision).get(SVNProperty.MIME_TYPE));
  }

  /**
   * {@inheritDoc}
   */
  public String getFileChecksum(final SVNRepository repository, final String path, final long revision) throws SVNException {
    return (String) getFileProperties(repository, path, revision).get(SVNProperty.CHECKSUM);
  }

  /**
   * {@inheritDoc}
   */
  public long getLatestRevision(final SVNRepository repository) throws SVNException {
    final long start = System.currentTimeMillis();
    final long revision = repository.getLatestRevision();
    logger.debug("PERF: getLatestRevision(): " + (System.currentTimeMillis() - start));
    return revision;
  }

  /**
   * {@inheritDoc}
   */
  public List<SVNLogEntry> getLatestRevisions(final String instanceName, final SVNRepository repository,
                                              final long revisionCount) throws SVNException, CacheException {
    return getLatestRevisions(instanceName, "/", repository, revisionCount);
  }

  /**
   * {@inheritDoc}
   */
  public List<SVNLogEntry> getLatestRevisions(final String instanceName, final String path, final SVNRepository repository,
                                              final long revisionCount) throws SVNException, CacheException {
    final long headRevision = repository.getLatestRevision();
    long toRevision = headRevision - revisionCount;
    if (toRevision < 1) {
      toRevision = 1;
    }
    return getRevisions(instanceName, repository, headRevision, toRevision, path, revisionCount);
  }

  /**
   * {@inheritDoc}
   */
  public SVNNodeKind getNodeKind(final SVNRepository repository, final String path, final long revision) throws SVNException {
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
  public List<RepositoryEntry> list(final SVNRepository repository, final String path, final long revision,
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
  public RepositoryEntry getEntryInfo(final SVNRepository repository, final String path, final long revision)
      throws SVNException {

    final long start = System.currentTimeMillis();
    final RepositoryEntry repositoryEntry =
        new RepositoryEntry(repository.info(path, revision), PathUtil.getPathPart(path));
    logger.debug("PERF: getEntryInfo(): " + (System.currentTimeMillis() - start));
    return repositoryEntry;
  }

  /**
   * {@inheritDoc}
   */
  public List<SVNFileRevision> getFileRevisions(final SVNRepository repository, final String path, final long revision)
      throws SVNException {

    final long start = System.currentTimeMillis();
    //noinspection unchecked
    final List<SVNFileRevision> svnFileRevisions =
        (List<SVNFileRevision>) repository.getFileRevisions(path, null, 0, revision);

    logger.debug("PERF: getFileRevisions(): " + (System.currentTimeMillis() - start));
    return svnFileRevisions;
  }

  /**
   * {@inheritDoc}
   */
  public ImageMetadata getThumbnailImage(final SVNRepository repository, final ObjectCache objectCache,
                                         final String path, final long revision,
                                         final URL fullSizeImageUrl, final String imageFormatName,
                                         final int maxThumbnailSize, final OutputStream out) throws SVNException {

    // Check if the thumbnail exists on the cache
    final String checksum = getFileChecksum(repository, path, revision);
    final ObjectCacheKey cacheKey = new ObjectCacheKey(path, checksum);
    logger.debug("Using cachekey: " + cacheKey);
    final byte[] thumbnailData = (byte[]) objectCache.get(cacheKey);

    try {
      if (thumbnailData != null) {
        // Writing cached thumbnail image to output stream
        out.write(thumbnailData);
      } else {
        // Thumbnail was not in the cache - create it.
        logger.debug("Getting full size image from url: " + fullSizeImageUrl);
        final ImageScaler imageScaler = new ImageScaler(ImageIO.read(fullSizeImageUrl));
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imageScaler.getThumbnail(maxThumbnailSize), imageFormatName, baos);

        // Putting created thumbnail image into the cache.
        logger.debug("Caching thumbnail. Using cachekey: " + cacheKey);
        objectCache.put(cacheKey, baos.toByteArray());
        out.write(baos.toByteArray());
      }
    } catch (final IOException ioex) {
      logger.warn("Unable to get thumbnail for: " + path, ioex);
    }
    return null;  //TODO: Return instance of ImageMetadata
  }

  /**
   * {@inheritDoc}
   */
  public List<SideBySideDiffRow> diffSideBySide(final SVNRepository repository, final DiffCommand diffCommand, final String charset,
                                                final InstanceConfiguration configuration) throws SVNException, DiffException {

    assertNotBinary(repository, diffCommand);
    assertFileEntries(repository, diffCommand);

    String diffResultString;
    SideBySideDiffCreator sideBySideDiffCreator;

    try {
      final RawTextFile leftFile = getTextFile(repository, diffCommand.getFromPath(), diffCommand.getFromRevision().getNumber(), charset);
      final RawTextFile rightFile = getTextFile(repository, diffCommand.getToPath(), diffCommand.getToRevision().getNumber(), charset);

      final Map leftFileProperties = getFileProperties(repository, diffCommand.getFromPath(), diffCommand.getFromRevision().getNumber());
      final Map rightFileProperties = getFileProperties(repository, diffCommand.getToPath(), diffCommand.getToRevision().getNumber());

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
          new KeywordHandler(leftFileProperties, configuration.getUrl() + diffCommand.getFromPath());
      final KeywordHandler toFileKeywordHandler =
          new KeywordHandler(rightFileProperties, configuration.getUrl() + diffCommand.getToPath());

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
  public String diffUnified(final SVNRepository repository, final DiffCommand diffCommand, final String charset,
                            final InstanceConfiguration configuration)
      throws SVNException, DiffException {

    assertNotBinary(repository, diffCommand);
    assertFileEntries(repository, diffCommand);

    String diffResultString;

    try {
      final RawTextFile leftFile = getTextFile(repository, diffCommand.getFromPath(), diffCommand.getFromRevision().getNumber(), charset);
      final RawTextFile rightFile = getTextFile(repository, diffCommand.getToPath(), diffCommand.getToRevision().getNumber(), charset);

      final ByteArrayOutputStream diffResult = new ByteArrayOutputStream();
      final DiffProducer diffProducer = new DiffProducer(new ByteArrayInputStream(leftFile.getContent().getBytes()),
          new ByteArrayInputStream(rightFile.getContent().getBytes()), charset);

      diffProducer.doUniDiff(diffResult);

      diffResultString = diffResult.toString(charset);
      if ("".equals(diffResultString)) {
        throw new IdenticalFilesException(diffCommand.getFromPath() + ", " + diffCommand.getToPath());
      }

    } catch (final IOException ioex) {
      throw new DiffException("Unable to produce unified diff", ioex);
    }

    return diffResultString;
  }

  private void assertNotBinary(final SVNRepository repository, final DiffCommand diffCommand) throws SVNException,
      IllegalFileFormatException {

    final boolean isLeftFileTextType = isTextFile(repository, diffCommand.getFromPath(), diffCommand.getFromRevision().getNumber());
    final boolean isRightFileTextType = isTextFile(repository, diffCommand.getToPath(), diffCommand.getToRevision().getNumber());

    if (!isLeftFileTextType && !isRightFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary files: " + diffCommand.getFromPath() + ", " + diffCommand.getToPath());
    } else if (!isLeftFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary file: " + diffCommand.getFromPath());
    } else if (!isRightFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary file: " + diffCommand.getToPath());
    }
  }

  private void assertFileEntries(final SVNRepository repository, final DiffCommand diffCommand) throws SVNException, DiffException {
    final SVNNodeKind entry1 = getNodeKind(repository, diffCommand.getFromPath(), diffCommand.getFromRevision().getNumber());
    final SVNNodeKind entry2 = getNodeKind(repository, diffCommand.getToPath(), diffCommand.getToRevision().getNumber());

    final String message = "Only files can be diffed. [";
    if (SVNNodeKind.FILE != entry1) {
      throw new DiffException(message + diffCommand.getFromPath() + "] is a directory");
    }
    if (SVNNodeKind.FILE != entry2) {
      throw new DiffException(message + diffCommand.getToPath() + "] is a directory");
    }
  }

}
