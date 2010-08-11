package org.sventon;

import org.junit.Ignore;
import org.sventon.appl.ConfigDirectory;
import org.sventon.model.RepositoryEntry;
import org.tmatesoft.svn.core.*;

import java.io.File;
import java.util.*;


@Ignore //Maven idiosyncrasy... http://jira.codehaus.org/browse/SUREFIRE-482
public final class TestUtils {

  public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
  public static final String CONFIG_FILE_NAME = "sventon_config.properties";

  private static final Map<String, SVNLogEntryPath> ONE_CHANED_PATH = new HashMap<String, SVNLogEntryPath>();
  private static final long DEFAULT_REVISION = 123;
  private static final String DEFAULT_MESSAGE = "TestMessage";
  private static final String DEFAULT_AUTHOR = "TestAuthor";

  static {
    ONE_CHANED_PATH.put("/file1.java", new SVNLogEntryPath("/file1.java", 'M', null, 1));
  }

  public static ConfigDirectory getTestConfigDirectory() {
    return new ConfigDirectory(new File(TEMP_DIR), "export_temp", "repositories");
  }

  public static SVNLogEntry getLogEntryStub() {
    return getLogEntryStub(new Date(), DEFAULT_AUTHOR, 123, DEFAULT_MESSAGE, ONE_CHANED_PATH);
  }

  public static SVNLogEntry getLogEntryStub(final Date date) {
    return getLogEntryStub(date, DEFAULT_AUTHOR, DEFAULT_REVISION, DEFAULT_MESSAGE, ONE_CHANED_PATH);
  }

  public static SVNLogEntry getLogEntryStub(final Map changedPaths) {
    return getLogEntryStub(new Date(), DEFAULT_AUTHOR, DEFAULT_REVISION, DEFAULT_MESSAGE, changedPaths);
  }

  public static SVNLogEntry getLogEntryStub(final long revision) {
    return getLogEntryStub(new Date(), DEFAULT_AUTHOR, revision, DEFAULT_MESSAGE, ONE_CHANED_PATH);
  }

  public static SVNLogEntry getLogEntryStub(final long revision, final String logMessage) {
    return getLogEntryStub(new Date(), DEFAULT_AUTHOR, revision, logMessage, ONE_CHANED_PATH);
  }

  public static SVNLogEntry getLogEntryStub(final Date date, final String author, final long revision, final String message,
                                            final Map<String, SVNLogEntryPath> changedPaths) {
    return new SVNLogEntry(changedPaths, revision, author, date, message);
  }

  public static Map<String, SVNLock> getLocksStub(final String path) {
    final Map<String, SVNLock> lockMap = new HashMap<String, SVNLock>();
    lockMap.put(path, new SVNLock(path, "id", "TestOwner", "Comment", new Date(), null));
    return lockMap;
  }

  public static List<RepositoryEntry> getDirectoryList() {
    final List<RepositoryEntry> entries = new ArrayList<RepositoryEntry>();
    entries.add(new RepositoryEntry(new SVNDirEntry(null, null, "trunk", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, null, "TAGS", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, null, "file1.java", SVNNodeKind.FILE, 64000, false, 1, new Date(), "jesper"), "/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, null, "file2.html", SVNNodeKind.FILE, 32000, false, 2, new Date(), "jesper"), "/"));

    entries.add(new RepositoryEntry(new SVNDirEntry(null, null, "tagfile.txt", SVNNodeKind.FILE, 3200, false, 2, new Date(), "jesper"), "/TAGS/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, null, "test.txt", SVNNodeKind.FILE, 1600, false, 3, new Date(), "jesper"), "/TAGS/"));

    entries.add(new RepositoryEntry(new SVNDirEntry(null, null, "code", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/trunk/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, null, "src", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/trunk/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, null, "DirFile3.java", SVNNodeKind.FILE, 1600, false, 3, new Date(), "jesper"), "/trunk/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, null, "file1_in_trunk.java", SVNNodeKind.FILE, 6400, false, 1, new Date(), "patrik"), "/trunk/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, null, "TestDirFile3.java", SVNNodeKind.FILE, 1600, false, 3, new Date(), "patrik"), "/trunk/"));

    entries.add(new RepositoryEntry(new SVNDirEntry(null, null, "File3.java", SVNNodeKind.FILE, 16000, false, 3, new Date(), "jesper"), "/trunk/code/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, null, "DirFile2.html", SVNNodeKind.FILE, 3200, false, 2, new Date(), "jesper"), "/trunk/src/"));
    return entries;
  }

  public static List<RepositoryEntry> getFlattenedDirectoriesList() {
    final List<RepositoryEntry> list = new ArrayList<RepositoryEntry>();
    list.add(new RepositoryEntry(new SVNDirEntry(null, null, "dir1", SVNNodeKind.DIR, 0, false, 1, new Date(), DEFAULT_AUTHOR), "/"));
    list.add(new RepositoryEntry(new SVNDirEntry(null, null, "dir2", SVNNodeKind.DIR, 0, false, 2, new Date(), DEFAULT_AUTHOR), "/"));
    list.add(new RepositoryEntry(new SVNDirEntry(null, null, "dir3", SVNNodeKind.DIR, 0, false, 3, new Date(), DEFAULT_AUTHOR), "/trunk/"));
    return list;
  }

  public static List<RepositoryEntry> getFileEntriesDirectoryList() {
    final List<RepositoryEntry> list = new ArrayList<RepositoryEntry>();
    list.add(new RepositoryEntry(new SVNDirEntry(null, null, "test.abC", SVNNodeKind.FILE, 0, false, 0, null, null), "/"));
    list.add(new RepositoryEntry(new SVNDirEntry(null, null, "test.jpg", SVNNodeKind.FILE, 0, false, 0, null, null), "/"));
    list.add(new RepositoryEntry(new SVNDirEntry(null, null, "test.GIF", SVNNodeKind.FILE, 0, false, 0, null, null), "/"));
    return list;
  }
}
