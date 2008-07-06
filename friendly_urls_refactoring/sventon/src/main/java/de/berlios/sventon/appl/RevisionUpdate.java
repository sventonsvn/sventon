/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.appl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.List;

/**
 * A RevisionUpdate object will be created when a repository change has been detected.
 * The object will be communicated to all {@link RevisionObserver}s and contains the
 * new revisions including repository information.
 *
 * @author jesper@users.berlios.de
 */
public final class RevisionUpdate {

  private final RepositoryName repositoryName;
  private final List<SVNLogEntry> logEntries;
  private final boolean flushAfterUpdate;
  private final boolean clearCacheBeforeUpdate;

  /**
   * Constructor.
   *
   * @param repositoryName         Repository name
   * @param logEntries             The new log entries
   * @param flushAfterUpdate       If <tt>true</tt>, caches will be flushed after update.
   * @param clearCacheBeforeUpdate Clear cache before update, to make sure we don't get duplicates.
   */
  public RevisionUpdate(final RepositoryName repositoryName, final List<SVNLogEntry> logEntries, final boolean flushAfterUpdate,
                        final boolean clearCacheBeforeUpdate) {
    this.repositoryName = repositoryName;
    this.logEntries = logEntries;
    this.flushAfterUpdate = flushAfterUpdate;
    this.clearCacheBeforeUpdate = clearCacheBeforeUpdate;
  }

  /**
   * Gets the updates log entries, one per revision.
   *
   * @return The log entries.
   */
  public List<SVNLogEntry> getRevisions() {
    return logEntries;
  }

  /**
   * Gets the repository name.
   *
   * @return The repository name.
   */
  public RepositoryName getRepositoryName() {
    return repositoryName;
  }

  /**
   * Gets the flush-after-update status.
   *
   * @return If <tt>true</tt>, caches must be flushed after update.
   */
  public boolean isFlushAfterUpdate() {
    return flushAfterUpdate;
  }

  /**
   * Gets the clearCacheBeforeUpdate status.
   *
   * @return If <tt>true</tt>, caches must be cleared before update.
   */
  public boolean isClearCacheBeforeUpdate() {
    return clearCacheBeforeUpdate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
}
