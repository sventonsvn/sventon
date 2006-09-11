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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.io.OutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    return (SVNLogEntry) repository.log(
        new String[]{path}, null, revision, revision, true, false).iterator().next();
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

    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    repository.log(new String[]{path}, fromRevision, toRevision, true, false, limit, new ISVNLogEntryHandler() {
      public void handleLogEntry(final SVNLogEntry logEntry) {
        logEntries.add(logEntry);
      }
    });
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
  }

  /**
   * {@inheritDoc}
   */
  public void getFile(final SVNRepository repository, final String path, final long revision,
                      final OutputStream output) throws SVNException {
    repository.getFile(path, revision, null, output);
  }

  /**
   * {@inheritDoc}
   */
  public void getFile(final SVNRepository repository, final String path, final long revision,
                      final OutputStream output, final Map properties) throws SVNException {
    repository.getFile(path, revision, properties, output);
  }

  /**
   * {@inheritDoc}
   */
  public void getFileProperties(final SVNRepository repository, final String path, final long revision,
                                final Map properties) throws SVNException {
    repository.getFile(path, revision, properties, null);
  }

  /**
   * {@inheritDoc}
   */
  public boolean isTextFile(final SVNRepository repository, final String path, final long revision) throws SVNException {
    final Map properties = new HashMap();
    repository.getFile(path, revision, properties, null);
    return SVNProperty.isTextMimeType((String) properties.get(SVNProperty.MIME_TYPE));
  }

  /**
   * {@inheritDoc}
   */
  public String getFileChecksum(final SVNRepository repository, final String path, final long revision) throws SVNException {
    final Map properties = new HashMap();
    repository.getFile(path, revision, properties, null);
    return (String) properties.get(SVNProperty.CHECKSUM);
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
