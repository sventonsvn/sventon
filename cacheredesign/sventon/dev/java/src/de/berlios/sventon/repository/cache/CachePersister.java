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
package de.berlios.sventon.repository.cache;

/**
 * Cache persister.
 *
 * @author jesper@users.berlios.de
 */
public interface CachePersister {

  CommitMessageCache loadCommitMessageCache();
  EntryCache loadEntryCache();

  void save(final CommitMessageCache commitMessageCache);
  void save(final EntryCache entryCache);

}
