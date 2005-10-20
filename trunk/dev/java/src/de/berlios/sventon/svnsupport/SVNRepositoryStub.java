package de.berlios.sventon.svnsupport;

import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.*;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SVNRepositoryStub extends SVNRepository {

  public SVNRepositoryStub(SVNURL location, boolean myIsSessionMode) {
    super(location, false);
    repositoryEntries = new HashMap<String, Collection>();
  }

  private long latestRevision = 0;

  private HashMap<String, Collection> repositoryEntries = null;

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

  public Collection getDir(String s, long l) throws SVNException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void diff(SVNURL svnurl, long l, String s, boolean b, boolean b1, ISVNReporterBaton isvnReporterBaton, ISVNEditor isvnEditor) throws SVNException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void diff(SVNURL svnurl, long l, long l1, String s, boolean b, boolean b1, ISVNReporterBaton isvnReporterBaton, ISVNEditor isvnEditor) throws SVNException {
    //To change body of implemented methods use File | Settings | File Templates.
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

  public void update(SVNURL svnurl, long l, String s, boolean b, ISVNReporterBaton isvnReporterBaton, ISVNEditor isvnEditor) throws SVNException {
    //To change body of implemented methods use File | Settings | File Templates.
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

  public void lock(Map map, String s, boolean b, ISVNLockHandler isvnLockHandler) throws SVNException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void unlock(Map map, boolean b, ISVNLockHandler isvnLockHandler) throws SVNException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void closeSession() throws SVNException {
    //To change body of implemented methods use File | Settings | File Templates.
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

}
