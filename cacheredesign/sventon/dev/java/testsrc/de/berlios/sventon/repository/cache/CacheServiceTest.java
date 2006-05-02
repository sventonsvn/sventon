package de.berlios.sventon.repository.cache;

import de.berlios.sventon.repository.RepositoryConfiguration;
import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.SVNRepositoryStub;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CacheServiceTest extends TestCase {

  public void testFindEntry() throws Exception {
    final RepositoryConfiguration config = new RepositoryConfiguration();
    config.setCacheUsed(false); // Prevent physical connection during update check
    config.setRepositoryRoot("http://localhost");
    final EntryCacheImpl entryCache = new EntryCacheImpl();
    entryCache.add(getEntryTemplateList());

    final CacheServiceImpl cacheService = new CacheServiceImpl();
    cacheService.setRepositoryConfiguration(config);
    cacheService.setEntryCache(entryCache);
    assertEquals(4, cacheService.findEntry("java").size());
  }

  public void testFindEntryInPath() throws Exception {
    final RepositoryConfiguration config = new RepositoryConfiguration();
    config.setCacheUsed(false); // Prevent physical connection during update check
    config.setRepositoryRoot("http://localhost");
    final EntryCacheImpl entryCache = new EntryCacheImpl();
    entryCache.add(getEntryTemplateList());

    final CacheServiceImpl cacheService = new CacheServiceImpl();
    cacheService.setRepositoryConfiguration(config);
    cacheService.setEntryCache(entryCache);
    assertEquals(1, cacheService.findEntry("html", "/trunk/src/").size());
  }

  public void testFindEntryWithLimit() throws Exception {
    final RepositoryConfiguration config = new RepositoryConfiguration();
    config.setCacheUsed(false); // Prevent physical connection during update check
    config.setRepositoryRoot("http://localhost");
    final EntryCacheImpl entryCache = new EntryCacheImpl();
    entryCache.add(getEntryTemplateList());

    final CacheServiceImpl cacheService = new CacheServiceImpl();
    cacheService.setRepositoryConfiguration(config);
    cacheService.setEntryCache(entryCache);
    assertEquals(2, cacheService.findEntry("java", "/", 2).size());

    assertEquals(4, cacheService.findEntry("java", "/", null).size());
    assertEquals(4, cacheService.findEntry("java", "/", 8).size());
    assertEquals(4, cacheService.findEntry("java", "/", 0).size());
    assertEquals(1, cacheService.findEntry("java", "/", 1).size());
  }

  public void testFindDirectories() throws Exception {
    final RepositoryConfiguration config = new RepositoryConfiguration();
    config.setCacheUsed(false); // Prevent physical connection during update check
    config.setRepositoryRoot("http://localhost");
    final EntryCacheImpl entryCache = new EntryCacheImpl();
    entryCache.add(getEntryTemplateList());

    final CacheServiceImpl cacheService = new CacheServiceImpl();
    cacheService.setRepositoryConfiguration(config);
    cacheService.setEntryCache(entryCache);
    assertEquals(3, cacheService.findDirectories("/").size());

    assertEquals(1, cacheService.findDirectories("/trunk/").size());
  }

  public void testPopulate() throws Exception {
    final RepositoryConfiguration config = new RepositoryConfiguration();
    config.setCacheUsed(true);
    config.setRepositoryRoot("http://localhost");

    final CacheServiceImpl cacheService = new CacheServiceImpl();
    cacheService.setRepositoryConfiguration(config);
    cacheService.setRepository(SVNRepositoryStub.getInstance());
    cacheService.setEntryCache(new EntryCacheImpl());

    assertEquals(8, cacheService.findEntry(".*").size());
  }

  public void testUpdate() throws Exception {
    final RepositoryConfiguration config = new RepositoryConfiguration();
    config.setCacheUsed(true);
    config.setRepositoryRoot("http://localhost");

    final EntryCache entryCache = new EntryCacheImpl();
    entryCache.setCachedRevision(100);
    entryCache.setRepositoryURL("http://localhost");
    entryCache.add(getEntryTemplateList());

    final CacheServiceImpl cacheService = new CacheServiceImpl();
    cacheService.setRepositoryConfiguration(config);
    cacheService.setRepository(SVNRepositoryStub.getInstance());
    cacheService.setEntryCache(entryCache);

    assertEquals(10, cacheService.findEntry(".*").size());
  }

  private List<RepositoryEntry> getEntryTemplateList() {
    final List<RepositoryEntry> entries = new ArrayList<RepositoryEntry>();
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "trunk", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "tags", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "file1.java", SVNNodeKind.FILE, 64000, false, 1, new Date(), "jesper"), "/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "file2.html", SVNNodeKind.FILE, 32000, false, 2, new Date(), "jesper"), "/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "File3.java", SVNNodeKind.FILE, 16000, false, 3, new Date(), "jesper"), "/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "src", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/trunk/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "DirFile2.html", SVNNodeKind.FILE, 3200, false, 2, new Date(), "jesper"), "/trunk/src/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "file1_in_trunk.java", SVNNodeKind.FILE, 6400, false, 1, new Date(), "jesper"), "/trunk/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "DirFile3.java", SVNNodeKind.FILE, 1600, false, 3, new Date(), "jesper"), "/trunk/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "tagfile.txt", SVNNodeKind.FILE, 3200, false, 2, new Date(), "jesper"), "/tags/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "tagfile2.txt", SVNNodeKind.FILE, 1600, false, 3, new Date(), "jesper"), "/tags/", null));
    return entries;
  }
}