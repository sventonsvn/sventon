package org.sventon.service.svnkit;

import org.sventon.model.*;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.io.SVNFileRevision;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 */
public class Converter {

  public static LogEntry toLogEntry(SVNLogEntry svnLogEntry) {
    return new LogEntry(svnLogEntry.getRevision(),
        convertProperties(svnLogEntry.getRevisionProperties()),
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

  private static Map<RevisionProperty, String> convertProperties(SVNProperties revisionProperties) {
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


}


