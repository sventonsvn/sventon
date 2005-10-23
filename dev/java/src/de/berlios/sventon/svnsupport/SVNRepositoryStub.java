package de.berlios.sventon.svnsupport;

import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.*;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SVNRepositoryStub extends SVNRepository {

  public SVNRepositoryStub(SVNURL location, ISVNSession options) {
    super(location, options);
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

  /**
   * Fetches the contents of a directory into the provided
   * collection object and returns the directory entry itself. Information
   * of each directory entry is represented by an <b>SVNDirEntry</b> object.
   *
   * @param path                  a directory path relative to the repository location
   *                              to which this object is set
   * @param revision              a revision number
   * @param includeCommitMessages if <span class="javakeyword">true</span> then
   *                              dir entries (<b>SVNDirEntry</b> objects) will be supplied
   *                              with commit log messages, otherwise not
   * @param entries               a collection that receives fetched dir entries
   * @return the parent directory entry which contents
   *         are fetched into <code>entries</code>
   * @throws org.tmatesoft.svn.core.SVNException
   *          in the following cases:
   *          <ul>
   *          <li><code>path</code> not found in the specified <code>revision</code>
   *          <li><code>path</code> is not a directory
   *          <li>a failure occured while connecting to a repository
   *          <li>the user authentication failed
   *          (see {@link org.tmatesoft.svn.core.SVNAuthenticationException})
   *          </ul>
   * @see #getDir(String, long, java.util.Map, org.tmatesoft.svn.core.ISVNDirEntryHandler)
   * @see #getDir(String, long, java.util.Map, java.util.Collection)
   * @see org.tmatesoft.svn.core.SVNDirEntry
   */
  public SVNDirEntry getDir(String path, long revision, boolean includeCommitMessages, Collection entries) throws SVNException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public Collection getDir(String s, long l) throws SVNException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void diff(SVNURL svnurl, long l, long l1, String s, boolean b, boolean b1, ISVNReporterBaton isvnReporterBaton, ISVNEditor isvnEditor) throws SVNException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * Calculates the differences between two items.
   * <p/>
   * <p/>
   * <code>target</code> is the name (one-level path component) of an entry that will restrict
   * the scope of the diff operation to this entry. In other words <code>target</code> is a child entry of the
   * directory represented by the repository location to which this object is set. For
   * example, if we have something like <code>"/dirA/dirB"</code> in a repository, then
   * this object's repository location may be set to <code>"svn://host:port/path/to/repos/dirA"</code>,
   * and <code>target</code> may be <code>"dirB"</code>.
   * <p/>
   * <p/>
   * If <code>target</code> is <span class="javakeyword">null</span> or empty (<code>""</code>)
   * then the scope of the diff operation is the repository location to which
   * this object is set.
   * <p/>
   * <p/>
   * The <code>reporter</code> is used to describe the state of the target item(s) (i.e.
   * items' revision numbers). All the paths described by the <code>reporter</code>
   * should be relative to the repository location to which this object is set.
   * <p/>
   * <p/>
   * After that the <code>editor</code> is used to carry out all the work on
   * evaluating differences against <code>url</code>. This <code>editor</code> contains
   * knowledge of where the change will begin (when {@link org.tmatesoft.svn.core.io.ISVNEditor#openRoot(long) ISVNEditor.openRoot()}
   * is called).
   * <p/>
   * <p/>
   * If <code>ignoreAncestry</code> is <span class="javakeyword">false</span> then
   * the ancestry of the paths being diffed is taken into consideration - they
   * are treated as related. In this case, for example, if calculating differences between
   * two files with identical contents but different ancestry,
   * the entire contents of the target file is considered as having been removed and
   * added again.
   * <p/>
   * <p/>
   * If <code>ignoreAncestry</code> is <span class="javakeyword">true</span>
   * then the two paths are merely compared ignoring the ancestry.
   * <p/>
   * <p/>
   * <b>NOTE:</b> you may not invoke methods of this <b>SVNRepository</b>
   * object from within the provided <code>reporter</code> and <code>editor</code>.
   *
   * @param url            a repository location of the entry against which
   *                       differences are calculated
   * @param revision       a revision number of the entry located at the
   *                       specified <code>url</code>; defaults to the
   *                       latest revision (HEAD) if this arg is invalid
   * @param target         a target entry name (optional)
   * @param ignoreAncestry if <span class="javakeyword">true</span> then
   *                       the ancestry of the two entries to be diffed is
   *                       ignored, otherwise not
   * @param recursive      if <span class="javakeyword">true</span> and the diff scope
   *                       is a directory, descends recursively, otherwise not
   * @param reporter       a caller's reporter
   * @param editor         a caller's editor
   * @throws org.tmatesoft.svn.core.SVNException
   *          in the following cases:
   *          <ul>
   *          <li><code>url</code> not found neither in the specified
   *          <code>revision</code> nor in the HEAD revision
   *          <li>a failure occured while connecting to a repository
   *          <li>the user authentication failed
   *          (see {@link org.tmatesoft.svn.core.SVNAuthenticationException})
   *          </ul>
   * @see org.tmatesoft.svn.core.io.ISVNReporterBaton
   * @see org.tmatesoft.svn.core.io.ISVNReporter
   * @see org.tmatesoft.svn.core.io.ISVNEditor
   * @deprecated use {@link #diff(org.tmatesoft.svn.core.SVNURL, long, long, String, boolean, boolean, org.tmatesoft.svn.core.io.ISVNReporterBaton, org.tmatesoft.svn.core.io.ISVNEditor)} instead
   */
  public void diff(SVNURL url, long revision, String target, boolean ignoreAncestry, boolean recursive, ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
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
