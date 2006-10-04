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

import de.berlios.sventon.repository.cache.CacheException;
import de.berlios.sventon.repository.cache.CacheGateway;
import de.berlios.sventon.repository.export.ExportDirectory;
import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.web.model.RawTextFile;
import de.berlios.sventon.util.PathUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNFileRevision;

import java.io.OutputStream;
import java.io.File;
import java.io.ByteArrayOutputStream;
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
   * {@inheritDoc}
   */
  public SVNLogEntry getRevision(final SVNRepository repository, final long revision) throws SVNException {
    return getRevision(repository, revision, "/");
  }

  /**
   * {@inheritDoc}
   */
  public SVNLogEntry getRevision(final SVNRepository repository, final long revision, final String path) throws SVNException {
    final long start = System.currentTimeMillis();
    final SVNLogEntry svnLogEntry = (SVNLogEntry) repository.log(
        new String[]{path}, null, revision, revision, true, false).iterator().next();
    logger.debug("PERF: getRevision(): " + (System.currentTimeMillis() - start));
    return svnLogEntry;
  }

  /**
   * {@inheritDoc}
   */
  public List<SVNLogEntry> getRevisions(final SVNRepository repository, final long fromRevision, final long toRevision)
      throws SVNException {
    return getRevisions(repository, fromRevision, toRevision, "/", -1);
  }

  /**
   * {@inheritDoc}
   */
  public List<SVNLogEntry> getRevisions(final SVNRepository repository, final long fromRevision, final long toRevision,
                                        final long limit) throws SVNException {
    return getRevisions(repository, fromRevision, toRevision, "/", limit);
  }

  /**
   * {@inheritDoc}
   */
  public List<SVNLogEntry> getRevisions(final SVNRepository repository, final long fromRevision, final long toRevision,
                                        final String path, final long limit) throws SVNException {
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

  private SVNLogEntry getCachedRevision(final String instanceName, final long revision) throws CacheException {
    return cacheGateway.getRevision(instanceName, revision);
  }

  private List<SVNLogEntry> getCachedRevisions(final String instanceName, final long fromRevision, final long toRevision)
      throws CacheException {

    //TODO: revisit!
    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    if (fromRevision < toRevision) {
      for (long revision = fromRevision; revision <= toRevision; revision++) {
        logEntries.add(cacheGateway.getRevision(instanceName, revision));
      }
    } else {
      for (long revision = fromRevision; revision >= toRevision; revision--) {
        logEntries.add(cacheGateway.getRevision(instanceName, revision));
      }
    }
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
  public RawTextFile getTextFile(final SVNRepository repository, final String path, final long revision)
      throws SVNException {

    final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    getFile(repository, path, revision, outStream);
    return new RawTextFile(outStream.toString(), true);
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
  public void getFileProperties(final SVNRepository repository, final String path, final long revision,
                                final Map properties) throws SVNException {
    final long start = System.currentTimeMillis();
    repository.getFile(path, revision, properties, null);
    logger.debug("PERF: getFileProperties(): " + (System.currentTimeMillis() - start));
  }

  /**
   * {@inheritDoc}
   */
  public boolean isTextFile(final SVNRepository repository, final String path, final long revision) throws SVNException {
    final Map properties = new HashMap();
    getFileProperties(repository, path, revision, properties);
    return SVNProperty.isTextMimeType((String) properties.get(SVNProperty.MIME_TYPE));
  }

  /**
   * {@inheritDoc}
   */
  public String getFileChecksum(final SVNRepository repository, final String path, final long revision) throws SVNException {
    final Map properties = new HashMap();
    getFileProperties(repository, path, revision, properties);
    return (String) properties.get(SVNProperty.CHECKSUM);
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
  public RepositoryEntry getEntry(final SVNRepository repository, final String path, final long revision)
      throws SVNException {

    final long start = System.currentTimeMillis();
    final RepositoryEntry repositoryEntry =
        new RepositoryEntry(repository.info(path, revision), PathUtil.getPathPart(path));
    logger.debug("PERF: getEntry(): " + (System.currentTimeMillis() - start));
    return repositoryEntry;
  }

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
   * Sets the cache gateway instance.
   *
   * @param cacheGateway Cache gateway instance
   */
  public void setCacheGateway(final CacheGateway cacheGateway) {
    this.cacheGateway = cacheGateway;
  }

}
