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
package de.berlios.sventon.repository.cache.entrycache;

import de.berlios.sventon.repository.*;
import de.berlios.sventon.web.ctrl.LogEntryActionType;
import de.berlios.sventon.util.PathUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.*;

/**
 * EntryCacheUpdater.
 *
 * @author jesper@users.berlios.de
 */
public class EntryCacheUpdater extends AbstractRevisionObserver {

  /**
   * The logging instance.
   */
  final private Log logger = LogFactory.getLog(getClass());

  /**
   * The EntryCache instance.
   */
  final private EntryCache entryCache;

  /**
   * The repository instance. Needed to do entry lookups during caching.
   */
  private SVNRepository repository;

  /**
   * Repository configuration instance.
   */
  private RepositoryConfiguration configuration;

  /**
   * Constructor.
   *
   * @param entryCache The EntryCache instance.
   */
  public EntryCacheUpdater(final EntryCache entryCache) {
    logger.info("Starting");
    this.entryCache = entryCache;
  }

  /**
   * For testing purposes only!
   *
   * @param repository Repository instance
   */
  protected void setRepository(final SVNRepository repository) {
    this.repository = repository;
  }

  /**
   * Sets the repository configuration.
   *
   * @param configuration The repository configuration
   */
  public void setRepositoryConfiguration(final RepositoryConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Updates the cache to HEAD revision.
   * A Subversion <i>log</i> command will be performed and
   * the cache will be updated accordingly.
   * <table>
   * <tr><th>Type</th><th>Description</th><th>Action</th></tr>
   * <tr><td>'A'</td><td>Added</td><td>Entry is added</td></tr>
   * <tr><td>'D'</td><td>Deleted</td><td>Entry is removed</td></tr>
   * <tr><td>'M'</td><td>Modified</td><td>Entry's details are updated</td></tr>
   * <tr><td>'R'</td><td>Replaced (means that the object is first deleted, then
   * another object with the same name is added, all within a single revision)
   * </td><td>Entry's details are updated</td></tr>
   * </table>
   *
   * @param revisions The new revisions.
   */
  public void update(final List<SVNLogEntry> revisions) {
    logger.info("Observer got [" + revisions.size() + "] updated revision(s)");

    try {
      if (repository == null) {
        repository = RepositoryFactory.INSTANCE.getRepository(configuration);
      }
    } catch (SVNException svne) {
      logger.warn("Could not establish repository connection", svne);
      return;
    }

    final int revisionCount = revisions.size();
    final long firstRevision = revisions.get(0).getRevision();
    final long lastRevision = revisions.get(revisionCount - 1).getRevision();

    if (revisionCount > 0 && firstRevision == 1) {
      logger.info("Starting initial population");
      try {
        addDirectories("/", lastRevision);
        entryCache.setCachedRevision(lastRevision);
      } catch (SVNException svnex) {
        logger.error("Unable to populate cache", svnex);
      }
    } else {
      // Initial population has already been performed - only apply changes for now.
      if (lastRevision < entryCache.getCachedRevision()) {
        throw new IllegalStateException("Revision [" + lastRevision + "] is older than last cached revision ["
            + entryCache.getCachedRevision() + "]. Has the repository URL changed? Delete all cache files to "
            + "trigger a complete cache rebuild");
      }

      // One logEntry is one commit (or revision)
      for (final SVNLogEntry logEntry : revisions) {
        try {
          long revision = logEntry.getRevision();
          logger.debug("Applying changes from revision [" + revision + "] to cache");

          final Map<String, SVNLogEntryPath> map = logEntry.getChangedPaths();
          final List<String> latestPathsList = new ArrayList<String>(map.keySet());
          // Sort the entries to apply changes in right order
          Collections.sort(latestPathsList);

          for (final String entryPath : latestPathsList) {
            final SVNLogEntryPath logEntryPath = map.get(entryPath);
            switch (LogEntryActionType.valueOf(String.valueOf(logEntryPath.getType()))) {
              case A :
                logger.debug("Adding entry to cache: " + logEntryPath.getPath());
                doEntryCacheAdd(logEntryPath, revision);
                break;
              case D :
                logger.debug("Removing deleted entry from cache: " + logEntryPath.getPath());
                doEntryCacheDelete(logEntryPath, revision);
                break;
              case R :
                logger.debug("Replacing entry in cache: " + logEntryPath.getPath());
                doEntryCacheReplace(logEntryPath, revision);
                break;
              case M :
                logger.debug("Updating modified entry in cache: " + logEntryPath.getPath());
                doEntryCacheModify(logEntryPath, revision);
                break;
              default :
                throw new RuntimeException("Unknown log entry type: " + logEntryPath.getType() + " in rev " + logEntry.getRevision());
            }
          }
        } catch (SVNException svnex) {
          logger.error("Unable to update entryCache", svnex);
        }
      }
    }
  }

  /**
   * Modifies an entry (file or directory) in the cache.
   *
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws org.tmatesoft.svn.core.SVNException
   *          if subversion error occur.
   */
  private void doEntryCacheModify(final SVNLogEntryPath logEntryPath, final long revision) throws SVNException {
    entryCache.removeByName(logEntryPath.getPath(), false);
    entryCache.add(new RepositoryEntry(repository.info(logEntryPath.getPath(), revision),
        PathUtil.getPathPart(logEntryPath.getPath()), null));
  }

  /**
   * Replaces an entry (file or directory) in the cache.
   *
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doEntryCacheReplace(final SVNLogEntryPath logEntryPath, final long revision) throws SVNException {
    doEntryCacheModify(logEntryPath, revision);
  }

  /**
   * Deletes an entry (file or directory) from the cache.
   *
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doEntryCacheDelete(final SVNLogEntryPath logEntryPath, final long revision) throws SVNException {
    // Have to find out if deleted entry was a file or directory
    final SVNDirEntry deletedEntry = repository.info(logEntryPath.getPath(), revision - 1);
    if (RepositoryEntry.Kind.valueOf(deletedEntry.getKind().toString()) == RepositoryEntry.Kind.dir) {
      // Directory node deleted
      logger.debug(logEntryPath.getPath() + " is a directory. Doing a recursive delete");
      entryCache.removeByName(logEntryPath.getPath(), true);
    } else {
      // Single entry delete
      entryCache.removeByName(logEntryPath.getPath(), false);
    }
  }

  /**
   * Adds an entry (file or directory) to the cache.
   *
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doEntryCacheAdd(final SVNLogEntryPath logEntryPath, final long revision) throws SVNException {
    // Have to find out if added entry was a file or directory
    final SVNDirEntry addedEntry = repository.info(logEntryPath.getPath(), revision);

    // If the entry is a directory and a copyPath exists, the entry is
    // a moved or copied directory (branch). In that case we have to recursively
    // add the entry. If entry is a directory but does not have a copyPath
    // the contents will be added one by one as single entries.
    if (RepositoryEntry.Kind.valueOf(addedEntry.getKind().toString()) == RepositoryEntry.Kind.dir
        && logEntryPath.getCopyPath() != null) {
      // Directory node added
      logger.debug(logEntryPath.getPath() + " is a directory. Doing a recursive add");
      entryCache.add(new RepositoryEntry(addedEntry, PathUtil.getPathPart(logEntryPath.getPath()), null));
      // Add directory contents
      addDirectories(logEntryPath.getPath() + "/", revision);
    } else {
      // Single entry added
      entryCache.add(new RepositoryEntry(addedEntry, PathUtil.getPathPart(logEntryPath.getPath()), null));
    }
  }

  /**
   * Adds all entries in given path.
   * This method will be recursively called by itself.
   *
   * @param path The path to add.
   * @throws org.tmatesoft.svn.core.SVNException
   *          if a Subversion error occurs.
   */
  @SuppressWarnings("unchecked")
  private void addDirectories(final String path, final long revision) throws SVNException {
    final List<SVNDirEntry> entriesList = Collections.checkedList(new ArrayList<SVNDirEntry>(), SVNDirEntry.class);

    entriesList.addAll(repository.getDir(path, revision, null, (Collection) null));
    for (SVNDirEntry entry : entriesList) {
      final RepositoryEntry newEntry = new RepositoryEntry(entry, path, null);
      if (!entryCache.add(newEntry)) {
        logger.warn("Unable to add already existing entry to cache: " + newEntry.toString());
      }
      if (entry.getKind() == SVNNodeKind.DIR) {
        addDirectories(path + entry.getName() + "/", revision);
      }
    }
  }
}
