package de.berlios.sventon.index;

import de.berlios.sventon.ctrl.RepositoryEntry;

import java.io.Serializable;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * The revision index keeps all repository entries cached for fast repository searching.
 * The RevisionIndex will be serialized to disk on exit and loaded on startup by
 * RevisionIndexer.
 *
 * @author jesper@users.berlios.de
 */
public class RevisionIndex implements Serializable {

  /**
   * The index.
   */
  private List<RepositoryEntry> index;

  /**
   * Current indexed revision.
   */
  private long indexRevision = 0;

  /**
   * Indexed URL.
   */
  private String url = "";

  /**
   * Constructor.
   */
  public RevisionIndex() {
    index = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);
  }

  /**
   * Gets the current index revision.
   *
   * @return The revision.
   */
  public long getIndexRevision() {
    return this.indexRevision;
  }

  /**
   * Sets the index revision.
   *
   * @param revision The revision
   */
  public void setIndexRevision(final long revision) {
    this.indexRevision = revision;
  }

  /**
   * Clears the index.
   */
  public void clearIndex() {
    index.clear();
  }

  /**
   * Gets the index entries.
   *
   * @return The index entries.
   */
  public List<RepositoryEntry> getEntries() {
    return index;
  }

  /**
   * Adds an entry to the index.
   *
   * @param entry Entry to add
   */
  public void add(final RepositoryEntry entry) {
    index.add(entry);
  }

  /**
   * Sets the indexed url.
   *
   * @param url The indexed url
   */
  public void setUrl(final String url) {
    this.url = url;
  }

  /**
   * Gets the index url.
   *
   * @return The index url
   */
  public String getUrl() {
    return this.url;
  }

}
