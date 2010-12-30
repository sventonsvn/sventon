/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.service.svnkit;

import org.sventon.model.*;
import org.sventon.model.Properties;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNFileRevision;

import java.util.*;

/**
 * Converter for SVNKit specific data structures.
 */
public class SVNKitConverter {

  public static LogEntry toLogEntry(SVNLogEntry svnLogEntry) {
    return new LogEntry(svnLogEntry.getRevision(),
        convertRevisionProperties(svnLogEntry.getRevisionProperties()),
        convert(svnLogEntry.getChangedPaths()));
  }

  private static SortedSet<ChangedPath> convert(Map<String, SVNLogEntryPath> changedPaths) {
    final SortedSet<ChangedPath> convertedPaths = new TreeSet<ChangedPath>();
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

  public static Properties convertProperties(final SVNProperties svnProperties) {
    final Properties props = new Properties();
    for (Object o : svnProperties.asMap().keySet()) {
      final String key = (String) o;
      final String value = SVNPropertyValue.getPropertyAsString(svnProperties.getSVNPropertyValue(key));
      props.put(new Property(key), new PropertyValue(value));
    }
    return props;
  }

  public static DirEntry createDirEntry(SVNDirEntry dirEntry, String fullPath) {
    final DirEntry.Kind kind = DirEntry.Kind.valueOf(dirEntry.getKind().toString().toUpperCase());
    return new DirEntry(fullPath, dirEntry.getName(), dirEntry.getAuthor(), dirEntry.getDate(),
        kind, dirEntry.getRevision(), dirEntry.getSize());
  }

  public static List<DirEntry> convertDirEntries(final Collection<SVNDirEntry> svnEntries, final String fullPath) {
    final List<DirEntry> entries = new ArrayList<DirEntry>();
    for (SVNDirEntry svnEntry : svnEntries) {
      entries.add(createDirEntry(svnEntry, fullPath));
    }
    return entries;
  }
}


