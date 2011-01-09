/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon;

import org.junit.Ignore;
import org.sventon.appl.ConfigDirectory;
import org.sventon.model.*;

import java.io.File;
import java.util.*;


@Ignore
//Maven idiosyncrasy... http://jira.codehaus.org/browse/SUREFIRE-482
public final class TestUtils {

  public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
  public static final String CONFIG_FILE_NAME = "sventon_config.properties";

  private static final SortedSet<ChangedPath> ONE_CHANGED_PATH = new TreeSet<ChangedPath>();
  private static final String DEFAULT_MESSAGE = "TestMessage";
  private static final String DEFAULT_AUTHOR = "TestAuthor";
  private static final String AUTHOR_JESPER = "jesper";
  private static final String AUTHOR_PATRIK = "patrik";

  static {
    ONE_CHANGED_PATH.add(new ChangedPath("/trunk/file1.java", null, 1, ChangeType.MODIFIED));
  }

  public static ConfigDirectory getTestConfigDirectory() {
    return new ConfigDirectory(new File(TEMP_DIR), "export_temp", "repositories");
  }

  public static LogEntry getLogEntryStub() {
    return getLogEntryStub(new Date(), DEFAULT_AUTHOR, 123, DEFAULT_MESSAGE, ONE_CHANGED_PATH);
  }

  public static LogEntry getLogEntryStub(final long revision) {
    return getLogEntryStub(new Date(), DEFAULT_AUTHOR, revision, DEFAULT_MESSAGE, ONE_CHANGED_PATH);
  }

  public static LogEntry getLogEntryStub(final Date date, final String author, final long revision, final String message,
                                         final SortedSet<ChangedPath> changedPaths) {
    return createLogEntry(revision, author, date, message, changedPaths);
  }

  public static List<DirEntry> getDirectoryList() {
    final List<DirEntry> entries = new ArrayList<DirEntry>();
    entries.add(new DirEntry("/", "trunk", AUTHOR_JESPER, new Date(), DirEntry.Kind.DIR, 1, 0));
    entries.add(new DirEntry("/", "TAGS", AUTHOR_JESPER, new Date(), DirEntry.Kind.DIR, 1, 0));
    entries.add(new DirEntry("/", "file1.java", AUTHOR_JESPER, new Date(), DirEntry.Kind.FILE, 1, 64000));
    entries.add(new DirEntry("/", "file2.html", AUTHOR_JESPER, new Date(), DirEntry.Kind.FILE, 2, 32000));

    entries.add(new DirEntry("/TAGS/", "tagfile.txt", AUTHOR_JESPER, new Date(), DirEntry.Kind.FILE, 2, 3200));
    entries.add(new DirEntry("/TAGS/", "test.txt", AUTHOR_JESPER, new Date(), DirEntry.Kind.FILE, 3, 1600));

    entries.add(new DirEntry("/trunk/", "code", AUTHOR_JESPER, new Date(), DirEntry.Kind.DIR, 1, 0));
    entries.add(new DirEntry("/trunk/", "src", AUTHOR_JESPER, new Date(), DirEntry.Kind.DIR, 1, 0));
    entries.add(new DirEntry("/trunk/", "DirFile3.java", AUTHOR_JESPER, new Date(), DirEntry.Kind.FILE, 3, 1600));
    entries.add(new DirEntry("/trunk/", "file1_in_trunk.java", AUTHOR_PATRIK, new Date(), DirEntry.Kind.FILE, 1, 6400));
    entries.add(new DirEntry("/trunk/", "TestDirFile3.java", AUTHOR_PATRIK, new Date(), DirEntry.Kind.FILE, 3, 1600));

    entries.add(new DirEntry("/trunk/code/", "File3.java", AUTHOR_JESPER, new Date(), DirEntry.Kind.FILE, 3, 16000));
    entries.add(new DirEntry("/trunk/src/", "DirFile2.html", AUTHOR_JESPER, new Date(), DirEntry.Kind.FILE, 2, 3200));

    return entries;
  }

  public static List<DirEntry> getFlattenedDirectoriesList() {
    final List<DirEntry> list = new ArrayList<DirEntry>();

    list.add(new DirEntry("/", "dir1", DEFAULT_AUTHOR, new Date(), DirEntry.Kind.DIR, 1, 0));
    list.add(new DirEntry("/", "dir2", DEFAULT_AUTHOR, new Date(), DirEntry.Kind.DIR, 2, 0));
    list.add(new DirEntry("/trunk/", "dir3", DEFAULT_AUTHOR, new Date(), DirEntry.Kind.DIR, 3, 0));
    return list;
  }

  public static List<DirEntry> getFileEntriesDirectoryList() {
    final List<DirEntry> list = new ArrayList<DirEntry>();
    list.add(new DirEntry("/", "test.abC", "", null, DirEntry.Kind.FILE, 0, 0));
    list.add(new DirEntry("/", "test.jpg", "", null, DirEntry.Kind.FILE, 0, 0));
    list.add(new DirEntry("/", "test.GIF", "", null, DirEntry.Kind.FILE, 0, 0));
    return list;
  }

  public static LogEntry createLogEntry(long revision, String author, Date date, String message, SortedSet<ChangedPath> changedPaths) {
    Map<RevisionProperty, String> props = new HashMap<RevisionProperty, String>();
    props.put(RevisionProperty.AUTHOR, author);
    props.put(RevisionProperty.LOG, message);
    props.put(RevisionProperty.DATE, DateUtil.formatISO8601(date));
    return new LogEntry(revision, props, changedPaths);
  }

  public static LogEntry createLogEntry(long revision, String author, Date date, String message) {
    return createLogEntry(revision, author, date, message, null);
  }
}
