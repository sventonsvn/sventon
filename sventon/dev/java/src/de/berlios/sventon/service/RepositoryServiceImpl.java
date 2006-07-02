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

import de.berlios.sventon.repository.RepositoryConfiguration;
import de.berlios.sventon.repository.cache.CacheException;
import de.berlios.sventon.repository.cache.Cache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNReporter;
import org.tmatesoft.svn.core.io.diff.SVNDeltaProcessor;
import org.tmatesoft.svn.core.io.diff.SVNDiffWindow;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

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
   * The repository configuration. Used to check whether caching is enabled or not.
   */
  private RepositoryConfiguration configuration;

  /**
   * The cache instance.
   */
  private Cache cache;

  /**
   * {@inheritDoc}
   */
  public SVNLogEntry getRevision(final SVNRepository repository, final long revision)
      throws SVNException {

    SVNLogEntry entry = null;
    if (configuration.isCacheUsed()) {
      try {
        logger.debug("Getting cached revision: " + revision);
        entry = getCachedRevision(revision);
      } catch (CacheException ce) {
        logger.warn("Unable to get cache revision: " + revision, ce);
        logger.info("Fallback - make a deep log fetch instead");
      }
    }
    return entry != null ? entry : getRevision(repository, revision, "/");
  }

  /**
   * {@inheritDoc}
   */
  public SVNLogEntry getRevision(final SVNRepository repository, final long revision, final String path)
      throws SVNException {

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
      public void handleLogEntry(final SVNLogEntry logEntry) throws SVNException {
        logEntries.add(logEntry);
      }
    });
    return logEntries;
  }

  private SVNLogEntry getCachedRevision(final long revision) throws CacheException {
    return cache.getRevision(revision);
  }

  private List<SVNLogEntry> getCachedRevisions(final long fromRevision, final long toRevision) throws CacheException {
    //TODO: revisit!
    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    if (fromRevision < toRevision) {
      for (long i = fromRevision; i <= toRevision; i++) {
        logEntries.add(cache.getRevision(i));
      }
    } else {
      for (long i = fromRevision; i >= toRevision; i--) {
        logEntries.add(cache.getRevision(i));
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
    final ISVNEditor exportEditor = new ExportEditor(exportDir);
    try {
      for (String target : targets) {
        final SVNNodeKind nodeKind = repository.checkPath(target, exportRevision);
        if (nodeKind == SVNNodeKind.FILE) {
          final File fileToExport = new File(exportDir, target);
          fileToExport.getParentFile().mkdirs();
          final OutputStream output = new BufferedOutputStream(new FileOutputStream(fileToExport));
          logger.debug("Exporting file [" + target + "] revision [" + exportRevision + "]");
          repository.getFile(target, exportRevision, null, output);
          output.flush();
          output.close();
        } else if (nodeKind == SVNNodeKind.DIR) {
          logger.debug("Exporting dir [" + target + "] revision [" + exportRevision + "]");
          repository.update(exportRevision, target, true, reporterBaton, exportEditor);
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
   * Set repository configuration.
   *
   * @param configuration Configuration
   */
  public void setRepositoryConfiguration(final RepositoryConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Sets the cache instance.
   *
   * @param cache Cache instance
   */
  public void setCache(final Cache cache) {
    this.cache = cache;
  }


  /**
   * ReporterBaton implementation that always reports 'empty wc' state.
   */
  private static class ExportReporterBaton implements ISVNReporterBaton {

    private long exportRevision;

    public ExportReporterBaton(long revision) {
      exportRevision = revision;
    }

    public void report(final ISVNReporter reporter) throws SVNException {
      /*
      * Here empty working copy is reported.
      *
      * ISVNReporter includes methods that allows to report mixed-rev working copy
      * and even let server know that some files or directories are locally missing or
      * locked.
      */
      reporter.setPath("", null, exportRevision, true);

      /*
      * Don't forget to finish the report!
      */
      reporter.finishReport();
    }
  }

  /**
   * ISVNEditor implementation that will add directories and files into the target directory
   * accordingly to update instructions sent by the server.
   */
  private static class ExportEditor implements ISVNEditor {

    /**
     * Logger for this class and subclasses.
     */
    protected final Log logger = LogFactory.getLog(getClass());

    private File myRootDirectory;
    private SVNDeltaProcessor myDeltaProcessor;

    /*
    * root - the local directory where the node tree is to be exported into.
    */
    public ExportEditor(final File root) {
      myRootDirectory = root;
      /*
      * Utility class that will help us to transform 'deltas' sent by the
      * server to the new files contents.
      */
      myDeltaProcessor = new SVNDeltaProcessor();
    }

    /*
    * Server reports revision to which application of the further
    * instructions will update working copy to.
    */
    public void targetRevision(long revision) throws SVNException {
    }

    /*
    * Called before sending other instructions.
    */
    public void openRoot(long revision) throws SVNException {
    }

    /*
    * Called when a new directory has to be added.
    *
    * For each 'addDir' call server will call 'closeDir' method after
    * all children of the added directory are added.
    *
    * This implementation creates corresponding directory below root directory.
    */
    public void addDir(final String path, final String copyFromPath, final long copyFromRevision) throws SVNException {
      final File newDir = new File(myRootDirectory, path);
      if (!newDir.exists()) {
        if (!newDir.mkdirs()) {
          final SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.IO_ERROR, "error: failed to add the directory ''{0}''.", newDir);
          throw new SVNException(err);
        }
      }
    }

    /*
    * Called when there is an existing directory that has to be 'opened' either
    * to modify this directory properties or to process other files and directories
    * inside this directory.
    *
    * In case of export this method will never be called because we reported
    * that our 'working copy' is empty and so server knows that there are
    * no 'existing' directories.
    */
    public void openDir(String path, long revision) throws SVNException {
    }

    /*
    * Instructs to change opened or added directory property.
    *
    * This method is called to update properties set by the user as well
    * as those created automatically, like "svn:committed-rev".
    * See SVNProperty class for default property names.
    *
    * When property has to be deleted value will be 'null'.
    */
    public void changeDirProperty(String name, String value) throws SVNException {
    }

    /*
    * Called when a new file has to be created.
    *
    * For each 'addFile' call server will call 'closeFile' method after
    * sending file properties and contents.
    *
    * This implementation creates empty file below root directory, file contents
    * will be updated later, and for empty files may not be sent at all.
    */
    public void addFile(final String path, final String copyFromPath, final long copyFromRevision) throws SVNException {
      final File file = new File(myRootDirectory, path);
      if (file.exists()) {
        SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.IO_ERROR, "error: exported file ''{0}'' already exists!", file);
        throw new SVNException(err);
      }
      try {
        file.createNewFile();
      } catch (IOException e) {
        final SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.IO_ERROR, "error: cannot create new  file ''{0}''", file);
        throw new SVNException(err);
      }
    }

    /*
    * Called when there is an existing files that has to be 'opened' either
    * to modify file contents or properties.
    *
    * In case of export this method will never be called because we reported
    * that our 'working copy' is empty and so server knows that there are
    * no 'existing' files.
    */
    public void openFile(final String path, final long revision) throws SVNException {
    }

    /*
    * Instructs to add, modify or delete file property.
    * In this example we skip this instruction, but 'real' export operation
    * may inspect 'svn:eol-style' or 'svn:mime-type' property values to
    * transfor file contents propertly after receiving.
    */
    public void changeFileProperty(final String path, final String name, final String value) throws SVNException {
    }

    /*
    * Called before sending 'delta' for a file. Delta may include instructions
    * on how to create a file or how to modify existing file. In this example
    * delta will always contain instructions on how to create a new file and so
    * we set up deltaProcessor with 'null' base file and target file to which we would
    * like to store the result of delta application.
    */
    public void applyTextDelta(final String path, final String baseChecksum) throws SVNException {
      myDeltaProcessor.applyTextDelta(null, new File(myRootDirectory, path), false);
    }

    /*
    * Server sends deltas in form of 'diff windows'. Depending on the file size
    * there may be several diff windows. Utility class SVNDeltaProcessor process
    * these windows for us.
    */
    public OutputStream textDeltaChunk(final String path, final SVNDiffWindow diffWindow) throws SVNException {
      return myDeltaProcessor.textDeltaChunk(diffWindow);
    }

    /*
    * Called when all diff windows (delta) is transferred.
    */
    public void textDeltaEnd(final String path) throws SVNException {
      myDeltaProcessor.textDeltaEnd();
    }

    /*
    * Called when file update is completed.
    * This call always matches addFile or openFile call.
    */
    public void closeFile(final String path, final String textChecksum) throws SVNException {
      logger.debug("Exported: " + path);
    }

    /*
    * Called when all child files and directories are processed.
    * This call always matches addDir, openDir or openRoot call.
    */
    public void closeDir() throws SVNException {
    }

    /*
    * Insturcts to delete an entry in the 'working copy'. Of course will not be
    * called during export operation.
    */
    public void deleteEntry(final String path, final long revision) throws SVNException {
    }

    /*
    * Called when directory at 'path' should be somehow processed,
    * but authenticated user (or anonymous user) doesn't have enough
    * access rights to get information on this directory (properties, children).
    */
    public void absentDir(final String path) throws SVNException {
    }

    /*
    * Called when file at 'path' should be somehow processed,
    * but authenticated user (or anonymous user) doesn't have enough
    * access rights to get information on this file (contents, properties).
    */
    public void absentFile(final String path) throws SVNException {
    }

    /*
    * Called when update is completed.
    */
    public SVNCommitInfo closeEdit() throws SVNException {
      return null;
    }

    /*
    * Called when update is completed with an error or server
    * requests client to abort update operation.
    */
    public void abortEdit() throws SVNException {
    }
  }
}
