package de.berlios.sventon.repository.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation that persists cache by serializing the objects to disk
 *
 * @author jesper@users.berlios.de
 */
public class DiskCachePersister implements CachePersister {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  public CommitMessageCache loadCommitMessageCache() {
    //TODO: Read from disk
    logger.debug("Loading commitMessageCache");
    return null;
  }

  public EntryCache loadEntryCache() {
    //TODO: Read from disk
    logger.debug("Loading entryCache");
    return null;
  }

  public void save(final CommitMessageCache commitMessageCache) {
    //TODO: Save
    logger.debug("Saving commitMessageCache");
  }

  public void save(final EntryCache entryCache) {
    //TODO: Save
    logger.debug("Saving entryCache");
  }
}
