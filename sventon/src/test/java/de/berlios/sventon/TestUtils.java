package de.berlios.sventon;

import de.berlios.sventon.repository.RepositoryEntry;
import org.tmatesoft.svn.core.*;

import java.util.*;

public final class TestUtils {

  private static final Map<String, SVNLogEntryPath> ONE_CHANED_PATH = new HashMap<String, SVNLogEntryPath>();
  private static final long DEFAULT_REVISION = 123;
  private static final String DEFAULT_MESSAGE = "TestMessage";
  private static final String DEFAULT_AUTHOR = "TestAuthor";

  static {
    ONE_CHANED_PATH.put("/file1.java", new SVNLogEntryPath("/file1.java", 'M', null, 1));
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

  public static final Map<String, SVNLock> getLocksStub(final String path) {
    final Map<String, SVNLock> lockMap = new HashMap<String, SVNLock>();
    lockMap.put(path, new SVNLock(path, "id", "TestOwner", "Comment", new Date(), null));
    return lockMap;
  }

  public static List<RepositoryEntry> getDirectoryList() {
    final List<RepositoryEntry> entries = new ArrayList<RepositoryEntry>();
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "trunk", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "tags", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "file1.java", SVNNodeKind.FILE, 64000, false, 1, new Date(), "jesper"), "/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "file2.html", SVNNodeKind.FILE, 32000, false, 2, new Date(), "jesper"), "/"));

    entries.add(new RepositoryEntry(new SVNDirEntry(null, "tagfile.txt", SVNNodeKind.FILE, 3200, false, 2, new Date(), "jesper"), "/tags/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "tagfile2.txt", SVNNodeKind.FILE, 1600, false, 3, new Date(), "jesper"), "/tags/"));

    entries.add(new RepositoryEntry(new SVNDirEntry(null, "code", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/trunk/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "src", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/trunk/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "DirFile3.java", SVNNodeKind.FILE, 1600, false, 3, new Date(), "jesper"), "/trunk/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "file1_in_trunk.java", SVNNodeKind.FILE, 6400, false, 1, new Date(), "jesper"), "/trunk/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "TestDirFile3.java", SVNNodeKind.FILE, 1600, false, 3, new Date(), "jesper"), "/trunk/"));

    entries.add(new RepositoryEntry(new SVNDirEntry(null, "File3.java", SVNNodeKind.FILE, 16000, false, 3, new Date(), "jesper"), "/trunk/code/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "DirFile2.html", SVNNodeKind.FILE, 3200, false, 2, new Date(), "jesper"), "/trunk/src/"));
    return entries;
  }

  public static List<RepositoryEntry> getFlattenedDirectoriesList() {
    final List<RepositoryEntry> list = new ArrayList<RepositoryEntry>();
    list.add(new RepositoryEntry(new SVNDirEntry(null, "dir1", SVNNodeKind.DIR, 0, false, 1, new Date(), DEFAULT_AUTHOR), "/"));
    list.add(new RepositoryEntry(new SVNDirEntry(null, "dir2", SVNNodeKind.DIR, 0, false, 2, new Date(), DEFAULT_AUTHOR), "/"));
    list.add(new RepositoryEntry(new SVNDirEntry(null, "dir3", SVNNodeKind.DIR, 0, false, 3, new Date(), DEFAULT_AUTHOR), "/trunk/"));
    return list;
  }

  public static List<RepositoryEntry> getFileEntriesDirectoryList() {
    final List<RepositoryEntry> list = new ArrayList<RepositoryEntry>();
    list.add(new RepositoryEntry(new SVNDirEntry(null, "test.abC", SVNNodeKind.FILE, 0, false, 0, null, null), "/"));
    list.add(new RepositoryEntry(new SVNDirEntry(null, "test.jpg", SVNNodeKind.FILE, 0, false, 0, null, null), "/"));
    list.add(new RepositoryEntry(new SVNDirEntry(null, "test.GIF", SVNNodeKind.FILE, 0, false, 0, null, null), "/"));
    return list;
  }
}
