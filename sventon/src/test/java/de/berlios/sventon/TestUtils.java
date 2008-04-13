package de.berlios.sventon;

import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

}
