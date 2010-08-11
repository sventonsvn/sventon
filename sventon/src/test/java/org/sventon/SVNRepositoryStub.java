package org.sventon;

import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.*;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class SVNRepositoryStub extends SVNRepository {

  private static SVNURL LOCALHOST;

  static {
    try {
      LOCALHOST = SVNURL.parseURIDecoded("http://localhost/");
    } catch (SVNException e) {
      throw new RuntimeException(e);
    }
  }

  public SVNRepositoryStub() {
    super(LOCALHOST, null);
  }

  public SVNRepositoryStub(SVNURL location, ISVNSession options) {
    super(location, options);
  }

  @Override
  public void testConnection() throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public long getLatestRevision() throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public long getDatedRevision(Date date) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public SVNProperties getRevisionProperties(long revision, SVNProperties properties) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public void setRevisionPropertyValue(long revision, String propertyName, SVNPropertyValue propertyValue) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public SVNPropertyValue getRevisionPropertyValue(long revision, String propertyName) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public SVNNodeKind checkPath(String path, long revision) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public long getFile(String path, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public Collection getDir(String s, long l, SVNProperties svnProperties, int i, Collection collection) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public long getDir(String path, long revision, SVNProperties properties, ISVNDirEntryHandler handler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  protected int getFileRevisionsImpl(String arg0, long arg1, long arg2, boolean arg3, ISVNFileRevisionHandler arg4) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public int getFileRevisions(String path, long startRevision, long endRevision, ISVNFileRevisionHandler handler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public long log(String[] targetPaths, long startRevision, long endRevision, boolean changedPath, boolean strictNode, long limit, ISVNLogEntryHandler handler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public int getLocations(String path, long pegRevision, long[] revisions, ISVNLocationEntryHandler handler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public SVNDirEntry getDir(String path, long revision, boolean includeCommitMessages, Collection entries) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  /**
   * @deprecated
   */
  @Override
  public void diff(SVNURL url, long targetRevision, long revision, String target, boolean ignoreAncestry, boolean recursive, ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  /**
   * @deprecated
   */
  @Override
  public void diff(SVNURL url, long revision, String target, boolean ignoreAncestry, boolean recursive, ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public void update(long revision, String target, boolean recursive, ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public void status(long revision, String target, boolean recursive, ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  @Deprecated
  public void update(SVNURL url, long revision, String target, boolean recursive, ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public SVNDirEntry info(String path, long revision) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public ISVNEditor getCommitEditor(String logMessage, Map locks, boolean keepLocks, final ISVNWorkspaceMediator mediator) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public SVNLock getLock(String path) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public SVNLock[] getLocks(String path) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public void lock(Map pathsToRevisions, String comment, boolean force, ISVNLockHandler handler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public void unlock(Map pathToTokens, boolean force, ISVNLockHandler handler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public void closeSession() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public SVNURL getLocation() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public void setLocation(SVNURL url, boolean forceReconnect) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  /**
   * @deprecated
   */
  @Override
  public String getRepositoryUUID() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public String getRepositoryUUID(boolean forceConnection) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  /**
   * @deprecated
   */
  @Override
  public SVNURL getRepositoryRoot() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public SVNURL getRepositoryRoot(boolean forceConnection) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public void setAuthenticationManager(ISVNAuthenticationManager authManager) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public ISVNAuthenticationManager getAuthenticationManager() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  protected void setRepositoryCredentials(String uuid, SVNURL rootURL) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public long log(String[] targetPaths, long startRevision, long endRevision, boolean changedPath, boolean strictNode, ISVNLogEntryHandler handler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public Collection getFileRevisions(String path, Collection revisions, long sRevision, long eRevision) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public Collection getDir(String path, long revision, SVNProperties properties, Collection dirEntries) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public Collection log(String[] targetPaths, Collection entries, long startRevision, long endRevision, boolean changedPath, boolean strictNode) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public Collection getLocations(String path, Collection entries, long pegRevision, long[] revisions) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public Map getLocations(String path, Map entries, long pegRevision, long[] revisions) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  @Deprecated
  public void diff(SVNURL url, long targetRevision, long revision, String target, boolean ignoreAncestry, boolean recursive, boolean getContents, ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public void checkout(long revision, String target, boolean recursive, ISVNEditor editor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public void replay(long lowRevision, long revision, boolean sendDeltas, ISVNEditor editor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public ISVNEditor getCommitEditor(String logMessage, final ISVNWorkspaceMediator mediator) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public ISVNSession getOptions() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  protected long getDeletedRevisionImpl(String s, long l, long l1) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  protected void lock() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  protected void unlock() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public String getRepositoryPath(String relativePath) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public String getFullPath(String relativeOrRepositoryPath) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public long getDir(String s, long l, SVNProperties svnProperties, int i, ISVNDirEntryHandler isvnDirEntryHandler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public void diff(SVNURL svnurl, long l, long l1, String s, boolean b, SVNDepth svnDepth, boolean b1, ISVNReporterBaton isvnReporterBaton, ISVNEditor isvnEditor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public void update(SVNURL svnurl, long l, String s, SVNDepth svnDepth, ISVNReporterBaton isvnReporterBaton, ISVNEditor isvnEditor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public void update(long l, String s, SVNDepth svnDepth, boolean b, ISVNReporterBaton isvnReporterBaton, ISVNEditor isvnEditor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public void status(long l, String s, SVNDepth svnDepth, ISVNReporterBaton isvnReporterBaton, ISVNEditor isvnEditor) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  protected ISVNEditor getCommitEditorInternal(Map map, boolean b, SVNProperties svnProperties, ISVNWorkspaceMediator isvnWorkspaceMediator) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public boolean hasCapability(SVNCapability svnCapability) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  protected long getLocationSegmentsImpl(String s, long l, long l1, long l2, ISVNLocationSegmentHandler isvnLocationSegmentHandler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  protected int getLocationsImpl(String s, long l, long[] longs, ISVNLocationEntryHandler isvnLocationEntryHandler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  protected long logImpl(String[] strings, long l, long l1, boolean b, boolean b1, long l2, boolean b2, String[] strings1, ISVNLogEntryHandler isvnLogEntryHandler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  protected Map getMergeInfoImpl(String[] strings, long l, SVNMergeInfoInheritance svnMergeInfoInheritance, boolean b) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  protected void replayRangeImpl(long l, long l1, long l2, boolean b, ISVNReplayHandler isvnReplayHandler) throws SVNException {
    throw new UnsupportedOperationException("Not implemented!");
  }
}
