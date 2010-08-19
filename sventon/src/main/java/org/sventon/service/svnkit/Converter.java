package org.sventon.service.svnkit;

import org.sventon.model.*;
import org.sventon.model.Properties;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNFileRevision;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 */
public class Converter {

  public static LogEntry toLogEntry(SVNLogEntry svnLogEntry) {
    return new LogEntry(svnLogEntry.getRevision(),
        convertRevisionProperties(svnLogEntry.getRevisionProperties()),
        convert(svnLogEntry.getChangedPaths()));
  }

  private static Set<ChangedPath> convert(Map<String, SVNLogEntryPath> changedPaths) {
    final Set<ChangedPath> convertedPaths = new TreeSet<ChangedPath>();
    for (SVNLogEntryPath svnLogEntryPath : changedPaths.values()) {
      convertedPaths.add(new ChangedPath(svnLogEntryPath.getPath(), svnLogEntryPath.getCopyPath(),
          svnLogEntryPath.getCopyRevision(), ChangeType.parse(svnLogEntryPath.getType())));
    }
    return convertedPaths;
  }

  private static Map<RevisionProperty, String> convertRevisionProperties(SVNProperties revisionProperties) {
    final Map<RevisionProperty, String> properties = new HashMap<RevisionProperty, String>();

    for (Object o : revisionProperties.nameSet()) {
      final String revisionPropertyName = (String) o;
      final String revisionPropertyValue = revisionProperties.getStringValue(revisionPropertyName);
      final RevisionProperty revisionProperty = RevisionProperty.byName(revisionPropertyName);
      properties.put(revisionProperty, revisionPropertyValue);
    }
    return properties;
  }

  // TODO: Introduce commons collections with transformer etc?

  public static List<FileRevision> convertFileRevisions(List<SVNFileRevision> revisions) {
    final List<FileRevision> pathRevisions = new ArrayList<FileRevision>(revisions.size());
    for (SVNFileRevision fileRevision : revisions) {
      final FileRevision revision = new FileRevision(fileRevision.getPath(), Revision.create(fileRevision.getRevision()));

      final SVNProperties svnProperties = fileRevision.getRevisionProperties();
      for (Object o : svnProperties.nameSet()) {
        final String revisionPropertyName = (String) o;
        final String revisionPropertyValue = svnProperties.getStringValue(revisionPropertyName);
        final RevisionProperty revisionProperty = RevisionProperty.byName(revisionPropertyName);
        revision.addProperty(revisionProperty, revisionPropertyValue);
      }
      pathRevisions.add(revision);
    }
    return pathRevisions;
  }

  public static Properties convertProperties(final SVNProperties svnProperties){
    final Properties props = new Properties();
    for (Object o : svnProperties.asMap().keySet()) {
      final String key = (String) o;
      final String value = SVNPropertyValue.getPropertyAsString(svnProperties.getSVNPropertyValue(key));
      props.put(new Property(key), new PropertyValue(value));
    }

    return props;
  }


  public static DirEntry createDirEntry(SVNDirEntry dirEntry, String fullPath) {
    final DirEntry entry = new DirEntry(fullPath, dirEntry.getName(), dirEntry.getAuthor(), dirEntry.getDate(),
        DirEntry.Kind.valueOf(dirEntry.getKind().toString().toUpperCase()), dirEntry.getRevision(), dirEntry.getSize());

    return entry;
  }

  public static List<DirEntry> convertDirEntries(final Collection<SVNDirEntry> svnEntries, final String fullPath){
    final List<DirEntry> entries = new ArrayList<DirEntry>();

    for (SVNDirEntry svnEntry : svnEntries) {
      entries.add(createDirEntry(svnEntry, fullPath));
    }

    return entries;
  }
}


