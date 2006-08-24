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
import de.berlios.sventon.repository.export.ExportEditor;
import de.berlios.sventon.repository.export.ExportReporterBaton;
import de.berlios.sventon.util.PathUtil;
import de.berlios.sventon.web.model.TextFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.io.*;
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
                     final File exportDir) throws SVNException {

    long exportRevision = revision;
    if (exportRevision == -1) {
      exportRevision = repository.getLatestRevision();
    }
    final ISVNReporterBaton reporterBaton = new ExportReporterBaton(exportRevision);

    try {
      for (final String target : targets) {
        final SVNNodeKind nodeKind = repository.checkPath(target, exportRevision);
        final File entryToExport = new File(exportDir, target);
        if (nodeKind == SVNNodeKind.FILE) {
          entryToExport.getParentFile().mkdirs();
          final OutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(entryToExport));
          final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
          logger.debug("Exporting file [" + target + "] revision [" + exportRevision + "]");
          final Map<String, String> properties = new HashMap<String, String>();
          getFile(repository, target, exportRevision, outStream, properties);
          if (SVNProperty.isTextMimeType(properties.get(SVNProperty.MIME_TYPE))) {
            final TextFile textFile = new TextFile(outStream.toString(), properties, repository.getLocation().toDecodedString(), target);
            fileOutputStream.write(textFile.getContent().getBytes());
          } else {
            fileOutputStream.write(outStream.toByteArray());
          }
          fileOutputStream.flush();
          fileOutputStream.close();
        } else if (nodeKind == SVNNodeKind.DIR) {
          logger.debug("Exporting directory [" + target + "] revision [" + exportRevision + "]");
          entryToExport.mkdirs();
          // The update method does not accept a full path, only a single file or dir leaf
          logger.debug("Original repository location: " + repository.getLocation());
          final SVNURL originalLocation = repository.getLocation();
          repository.setLocation(SVNURL.parseURIDecoded(originalLocation.toDecodedString()
              + PathUtil.getPathNoLeaf(target)), false);
          logger.debug("Temporarily changing repository location to: " + repository.getLocation());
          // Do the export
          repository.update(exportRevision, PathUtil.getTarget(target), true, reporterBaton, new ExportEditor(entryToExport.getParentFile()));
          logger.debug("Resetting repository location");
          repository.setLocation(originalLocation, false);
        } else {
          throw new IllegalArgumentException("Target [" + target + "] does not exist in revision [" + exportRevision + "]");
        }
      }
    } catch (final IOException ioex) {
      logger.warn(ioex);
      throw new RuntimeException(ioex);
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

