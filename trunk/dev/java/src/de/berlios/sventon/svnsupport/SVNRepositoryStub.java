/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.svnsupport;

import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.*;

import java.io.OutputStream;
import java.util.*;

public class SVNRepositoryStub extends SVNRepository {

  private long latestRevision = 0;

  private SVNDirEntry infoEntry;

  private HashMap<String, Collection> repositoryEntries = null;

  private List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();


  public SVNRepositoryStub(SVNURL location, ISVNSession options) {
    super(location, options);
    repositoryEntries = new HashMap<String, Collection>();
  }

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

  /**
   * Traverses revisions history. In other words, collects per revision
   * information that includes the revision number, author, datestamp,
   * log message and maybe a list of changed paths (optional). For each
   * revision this information is represented by an <b>SVNLogEntry</b>
   * object. Such objects are passed to the provided <code>handler</code>.
   * <p/>
   * <p/>
   * This method invokes <code>handler</code> on each log entry from
   * <code>startRevision</code> to <code>endRevision</code>.
   * <code>startRevision</code> may be greater or less than
   * <code>endRevision</code>; this just controls whether the log messages are
   * processed in descending or ascending revision number order.
   * <p/>
   * <p/>
   * If <code>startRevision</code> or <code>endRevision</code> is invalid, it
   * defaults to the youngest.
   * <p/>
   * <p/>
   * If <code>targetPaths</code> has one or more elements, then
   * only those revisions are processed in which at least one of <code>targetPaths</code> was
   * changed (i.e., if a file text or properties changed; if dir properties
   * changed or an entry was added or deleted). Each path is relative
   * to the repository location that this object is set to.
   * <p/>
   * <p/>
   * If <code>changedPath</code> is <span class="javakeyword">true</span>, then each
   * <b>SVNLogEntry</b> passed to the handler will contain info about all
   * paths changed in that revision it represents. To get them call
   * {@link org.tmatesoft.svn.core.SVNLogEntry#getChangedPaths()} that returns a map,
   * which keys are the changed paths and the values are <b>SVNLogEntryPath</b> objects.
   * If <code>changedPath</code> is <span class="javakeyword">false</span>, changed paths
   * info will not be provided.
   * <p/>
   * <p/>
   * If <code>strictNode</code> is <span class="javakeyword">true</span>, copy history will
   * not be traversed (if any exists) when harvesting the revision logs for each path.
   * <p/>
   * <p/>
   * If <code>limit</code> is > 0 then only the first <code>limit</code> log entries
   * will be handled. Otherwise this number is ignored.
   * <p/>
   * <p/>
   * <b>NOTE:</b> you may not invoke methods of this <b>SVNRepository</b>
   * object from within the provided <code>handler</code>.
   *
   * @param targetPaths   paths that mean only those revisions at which they were
   *                      changed
   * @param startRevision a revision to start from
   * @param endRevision   a revision to end at
   * @param changedPath   if <span class="javakeyword">true</span> then
   *                      revision information will also include all changed paths per
   *                      revision, otherwise not
   * @param strictNode    if <span class="javakeyword">true</span> then copy history (if any) is not
   *                      to be traversed
   * @param limit         the maximum number of log entries to process
   * @param handler       a caller's handler that will be dispatched log entry objects
   * @return the number of revisions traversed
   * @throws org.tmatesoft.svn.core.SVNException
   *          if a failure occured while connecting to a repository
   *          or the user's authentication failed (see
   *          {@link org.tmatesoft.svn.core.SVNAuthenticationException})
   * @see #log(String[], java.util.Collection, long, long, boolean, boolean)
   * @see #log(String[], long, long, boolean, boolean, long, org.tmatesoft.svn.core.ISVNLogEntryHandler)
   * @see org.tmatesoft.svn.core.ISVNLogEntryHandler
   * @see org.tmatesoft.svn.core.SVNLogEntry
   * @see org.tmatesoft.svn.core.SVNLogEntryPath
   */
  public long log(String[] targetPaths, long startRevision, long endRevision, boolean changedPath, boolean strictNode, long limit, ISVNLogEntryHandler handler) throws SVNException {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
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
    return infoEntry;
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

  /**
   * Traverses revisions history and returns a collection of log entries. In
   * other words, collects per revision information that includes the revision number,
   * author, datestamp, log message and maybe a list of changed paths (optional). For each
   * revision this information is represented by an <b>SVNLogEntry</b>.
   * object.
   * <p/>
   * <p/>
   * <code>startRevision</code> may be greater or less than
   * <code>endRevision</code>; this just controls whether the log messages are
   * processed in descending or ascending revision number order.
   * <p/>
   * <p/>
   * If <code>startRevision</code> or <code>endRevision</code> is invalid, it
   * defaults to the youngest.
   * <p/>
   * <p/>
   * If <code>targetPaths</code> has one or more elements, then
   * only those revisions are processed in which at least one of <code>targetPaths</code> was
   * changed (i.e., if a file text or properties changed; if dir properties
   * changed or an entry was added or deleted). Each path is relative
   * to the repository location that this object is set to.
   * <p/>
   * <p/>
   * If <code>changedPath</code> is <span class="javakeyword">true</span>, then each
   * <b>SVNLogEntry</b> object is supplied with info about all
   * paths changed in that revision it represents. To get them call
   * {@link org.tmatesoft.svn.core.SVNLogEntry#getChangedPaths()} that returns a map,
   * which keys are the changed paths and the mappings are <b>SVNLogEntryPath</b> objects.
   * If <code>changedPath</code> is <span class="javakeyword">false</span>, changed paths
   * info will not be provided.
   * <p/>
   * <p/>
   * If <code>strictNode</code> is <span class="javakeyword">true</span>, copy history will
   * not be traversed (if any exists) when harvesting the revision logs for each path.
   *
   * @param targetPaths   paths that mean only those revisions at which they were
   *                      changed
   * @param entries       if not <span class="javakeyword">null</span> then this collection
   *                      will receive log entries
   * @param startRevision a revision to start from
   * @param endRevision   a revision to end at
   * @param changedPath   if <span class="javakeyword">true</span> then
   *                      revision information will also include all changed paths per
   *                      revision, otherwise not
   * @param strictNode    if <span class="javakeyword">true</span> then copy history (if any) is not
   *                      to be traversed
   * @return a collection with log entries
   * @throws org.tmatesoft.svn.core.SVNException
   *          if a failure occured while connecting to a repository
   *          or the user's authentication failed (see
   *          {@link org.tmatesoft.svn.core.SVNAuthenticationException})
   * @see #log(String[], long, long, boolean, boolean, org.tmatesoft.svn.core.ISVNLogEntryHandler)
   * @see #log(String[], long, long, boolean, boolean, long, org.tmatesoft.svn.core.ISVNLogEntryHandler)
   * @see org.tmatesoft.svn.core.ISVNLogEntryHandler
   * @see org.tmatesoft.svn.core.SVNLogEntry
   * @see org.tmatesoft.svn.core.SVNLogEntryPath
   */
  public Collection log(String[] targetPaths, Collection entries, long startRevision, long endRevision, boolean changedPath, boolean strictNode) throws SVNException {
    return logEntries;
  }

  public void setLogResult(final List<SVNLogEntry> entries) {
    logEntries = entries;
  }

  public static SVNRepositoryStub getInstance() throws Exception {
    // Set up the repository stub
    SVNRepositoryStub repository = new SVNRepositoryStub(SVNURL.parseURIDecoded("http://localhost"), null);

    repository.setLatestRevision(123);

    List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    Map<String, SVNLogEntryPath> changedPaths = new HashMap<String, SVNLogEntryPath>();
    changedPaths.put("/file1.java", new SVNLogEntryPath("/file1.java", 'M', "", 1));
    changedPaths.put("/file2.html", new SVNLogEntryPath("/file2.html", 'D', "", 1));
    changedPaths.put("/file3.abc", new SVNLogEntryPath("/file3.abc", 'A', "", 1));
    changedPaths.put("/file4.def", new SVNLogEntryPath("/file4.def", 'R', "", 1));
    logEntries.add(new SVNLogEntry(changedPaths, 1, "jesper", new Date(), "Commit message."));
    repository.setLogResult(logEntries);

    List<SVNDirEntry> entries1 = new ArrayList<SVNDirEntry>();
    entries1.add(new SVNDirEntry("file1.java", SVNNodeKind.FILE, 64000, false, 1, new Date(), "jesper"));
    entries1.add(new SVNDirEntry("file2.html", SVNNodeKind.FILE, 32000, false, 2, new Date(), "jesper"));
    entries1.add(new SVNDirEntry("dir1", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"));
    entries1.add(new SVNDirEntry("File3.java", SVNNodeKind.FILE, 16000, false, 3, new Date(), "jesper"));
    List<SVNDirEntry> entries2 = new ArrayList<SVNDirEntry>();
    entries2.add(new SVNDirEntry("dir2", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"));
    entries2.add(new SVNDirEntry("file1.java", SVNNodeKind.FILE, 6400, false, 1, new Date(), "jesper"));
    entries2.add(new SVNDirEntry("DirFile2.html", SVNNodeKind.FILE, 3200, false, 2, new Date(), "jesper"));
    entries2.add(new SVNDirEntry("DirFile3.java", SVNNodeKind.FILE, 1600, false, 3, new Date(), "jesper"));

    repository.addDir("/", entries1);
    repository.addDir("/dir1/", entries2);
    repository.addDir("/dir1/dir2/", new ArrayList());
    repository.infoEntry = new SVNDirEntry("file999.java", SVNNodeKind.FILE, 12345, false, 1, new Date(), "jesper");

    return repository;
  }
}
