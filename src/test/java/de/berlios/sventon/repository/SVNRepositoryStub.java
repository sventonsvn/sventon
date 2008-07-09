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
package de.berlios.sventon.repository;

import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.*;

import java.io.OutputStream;
import java.util.*;

public class SVNRepositoryStub extends SVNRepository {

  public SVNRepositoryStub(SVNURL location, ISVNSession options) {
    super(location, options);
  }

  public void testConnection() throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public long getLatestRevision() throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public long getDatedRevision(Date date) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public Map getRevisionProperties(long revision, Map properties) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public void setRevisionPropertyValue(long revision, String propertyName, String propertyValue) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public String getRevisionPropertyValue(long revision, String propertyName) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public SVNNodeKind checkPath(String path, long revision) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public long getFile(String path, long revision, Map properties, OutputStream contents) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public long getDir(String path, long revision, Map properties, ISVNDirEntryHandler handler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public int getFileRevisions(String path, long startRevision, long endRevision, ISVNFileRevisionHandler handler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public long log(String[] targetPaths, long startRevision, long endRevision, boolean changedPath, boolean strictNode, long limit, ISVNLogEntryHandler handler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public int getLocations(String path, long pegRevision, long[] revisions, ISVNLocationEntryHandler handler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public SVNDirEntry getDir(String path, long revision, boolean includeCommitMessages, Collection entries) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  /**
   * @deprecated
   */
  public void diff(SVNURL url, long targetRevision, long revision, String target, boolean ignoreAncestry, boolean recursive, ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  /**
   * @deprecated
   */
  public void diff(SVNURL url, long revision, String target, boolean ignoreAncestry, boolean recursive, ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public void update(long revision, String target, boolean recursive, ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public void status(long revision, String target, boolean recursive, ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public void update(SVNURL url, long revision, String target, boolean recursive, ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public SVNDirEntry info(String path, long revision) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public ISVNEditor getCommitEditor(String logMessage, Map locks, boolean keepLocks, final ISVNWorkspaceMediator mediator) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public SVNLock getLock(String path) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public SVNLock[] getLocks(String path) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public void lock(Map pathsToRevisions, String comment, boolean force, ISVNLockHandler handler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public void unlock(Map pathToTokens, boolean force, ISVNLockHandler handler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public void closeSession() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public SVNURL getLocation() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public void setLocation(SVNURL url, boolean forceReconnect) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  /**
   * @deprecated
   */
  public String getRepositoryUUID() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public String getRepositoryUUID(boolean forceConnection) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  /**
   * @deprecated
   */
  public SVNURL getRepositoryRoot() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public SVNURL getRepositoryRoot(boolean forceConnection) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public void setAuthenticationManager(ISVNAuthenticationManager authManager) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public ISVNAuthenticationManager getAuthenticationManager() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  protected void setRepositoryCredentials(String uuid, SVNURL rootURL) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public long log(String[] targetPaths, long startRevision, long endRevision, boolean changedPath, boolean strictNode, ISVNLogEntryHandler handler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public Collection getFileRevisions(String path, Collection revisions, long sRevision, long eRevision) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public Collection getDir(String path, long revision, Map properties, Collection dirEntries) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public Collection log(String[] targetPaths, Collection entries, long startRevision, long endRevision, boolean changedPath, boolean strictNode) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public Collection getLocations(String path, Collection entries, long pegRevision, long[] revisions) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public Map getLocations(String path, Map entries, long pegRevision, long[] revisions) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public void diff(SVNURL url, long targetRevision, long revision, String target, boolean ignoreAncestry, boolean recursive, boolean getContents, ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public void checkout(long revision, String target, boolean recursive, ISVNEditor editor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public void replay(long lowRevision, long revision, boolean sendDeltas, ISVNEditor editor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public ISVNEditor getCommitEditor(String logMessage, final ISVNWorkspaceMediator mediator) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public ISVNSession getOptions() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  protected void lock() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  protected void unlock() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public String getRepositoryPath(String relativePath) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public String getFullPath(String relativeOrRepositoryPath) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }
}
