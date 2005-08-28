package de.berlios.sventon.svnsupport;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNFileRevisionHandler;
import org.tmatesoft.svn.core.io.ISVNLocationEntryHandler;
import org.tmatesoft.svn.core.io.ISVNLockHandler;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.ISVNWorkspaceMediator;
import org.tmatesoft.svn.core.io.SVNRepository;

public class SVNRepositoryStub extends SVNRepository {

  public SVNRepositoryStub(SVNURL location, boolean myIsSessionMode) {
    super(location, myIsSessionMode);
    repositoryEntries = new HashMap<String, Collection>();
  }

  private long latestRevision = 0;
 
  private HashMap<String, Collection> repositoryEntries = null;

  //
  // public SVNRepositoryStub(SVNRepositoryLocation svnRepositoryLocation) {
  // super(svnRepositoryLocation);
  // repositoryEntries = new HashMap<String, Collection>();
  // }

  public void testConnection() throws SVNException {
  }

  public void setLatestRevision(final long revision) {
    this.latestRevision = revision;
  }

  public long getLatestRevision() throws SVNException {
    return latestRevision;
  }

  public long getDatedRevision(Date date) throws SVNException {
    return 0;
  }

  public Map getRevisionProperties(long l, Map map) throws SVNException {
    return null;
  }

  public void setRevisionPropertyValue(long l, String s, String s1) throws SVNException {
  }

  public String getRevisionPropertyValue(long l, String s) throws SVNException {
    return null;
  }

  public SVNNodeKind checkPath(String s, long l) throws SVNException {
    return null;
  }

  public long getFile(String s, long l, Map map, OutputStream outputStream) throws SVNException {
    return 0;
  }

  public void addDir(final String path, Collection dirEntries) {
    repositoryEntries.put(path, dirEntries);
  }

  public Collection getDir(String path, long revision, Map properties, Collection dirEntries) throws SVNException {
    return repositoryEntries.get(path);
  }

  public long getDir(String s, long l, Map map, ISVNDirEntryHandler isvnDirEntryHandler) throws SVNException {
    return 0;
  }

  public int getFileRevisions(String s, long l, long l1, ISVNFileRevisionHandler isvnFileRevisionHandler)
      throws SVNException {
    return 0;
  }

  public int getLocations(String s, long l, long[] longs, ISVNLocationEntryHandler isvnLocationEntryHandler)
      throws SVNException {
    return 0;
  }

  public void update(long l, String s, boolean b, ISVNReporterBaton isvnReporterBaton, ISVNEditor isvnEditor)
      throws SVNException {

  }

  public void status(long l, String s, boolean b, ISVNReporterBaton isvnReporterBaton, ISVNEditor isvnEditor)
      throws SVNException {

  }

  public SVNDirEntry info(String s, long l) throws SVNException {
    return null;
  }

  public ISVNEditor getCommitEditor(String s, Map map, boolean b, ISVNWorkspaceMediator isvnWorkspaceMediator)
      throws SVNException {
    return null;
  }

  public SVNLock getLock(String s) throws SVNException {
    return null;
  }

  public SVNLock[] getLocks(String s) throws SVNException {
    return new SVNLock[0];
  }

  public SVNLock setLock(String s, String s1, boolean b, long l) throws SVNException {
    return null;
  }

  public void removeLock(String s, String s1, boolean b) throws SVNException {
  }

  public long log(String[] arg0, long arg1, long arg2, boolean arg3, boolean arg4, long arg5, ISVNLogEntryHandler arg6)
      throws SVNException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void diff(SVNURL url, long revision, String target, boolean ignoreAncestry, boolean recursive,
      ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
    // TODO Auto-generated method stub

  }

  @Override
  public void diff(SVNURL url, long targetRevision, long revision, String target, boolean ignoreAncestry,
      boolean recursive, ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
    // TODO Auto-generated method stub

  }

  @Override
  public void update(SVNURL url, long revision, String target, boolean recursive, ISVNReporterBaton reporter,
      ISVNEditor editor) throws SVNException {
    // TODO Auto-generated method stub

  }

  @Override
  public void lock(Map arg0, String arg1, boolean arg2, ISVNLockHandler arg3) throws SVNException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void unlock(Map arg0, boolean arg1, ISVNLockHandler arg2) throws SVNException {
    // TODO Auto-generated method stub
    
  }

@Override
public Collection getDir(String arg0, long arg1) throws SVNException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public void closeSession() throws SVNException {
	// TODO Auto-generated method stub
	
}
}
