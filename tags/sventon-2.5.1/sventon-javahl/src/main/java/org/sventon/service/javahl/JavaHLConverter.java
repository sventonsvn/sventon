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
package org.sventon.service.javahl;

import org.sventon.model.ChangeType;
import org.sventon.model.ChangedPath;
import org.sventon.model.DirEntry;
import org.sventon.model.RevisionProperty;
import org.tigris.subversion.javahl.ChangePath;
import org.tigris.subversion.javahl.NodeKind;
import org.tigris.subversion.javahl.Revision;
import org.tigris.subversion.javahl.RevisionRange;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Converter for JavaHL specific data structures.
 */
public class JavaHLConverter {

  static Revision convertRevision(final long revision) {
    if (revision == org.sventon.model.Revision.HEAD.getNumber()) {
      return Revision.HEAD;
    }

    return Revision.getInstance(revision);
  }

  static SortedSet<ChangedPath> convertChangedPaths(final ChangePath[] changePaths) {
    final SortedSet<ChangedPath> changedPaths = new TreeSet<ChangedPath>();
    if (changePaths != null) {
      for (ChangePath cp : changePaths) {
        changedPaths.add(new ChangedPath(cp.getPath(), cp.getCopySrcPath(), cp.getCopySrcRevision(), ChangeType.parse(cp.getAction())));
      }
    }
    return changedPaths;
  }

  static Map<RevisionProperty, String> convertRevisionPropertyMap(final Map map) {
    final HashMap<RevisionProperty, String> propertyMap = new HashMap<RevisionProperty, String>();

    if (map != null) {
      for (Object o : map.keySet()) {
        String property = (String) o;
        propertyMap.put(RevisionProperty.byName(property), (String) map.get(property));

      }
    }
    return propertyMap;
  }

  static RevisionRange[] getRevisionRange(final long fromRevision, final long toRevision) {
    return new RevisionRange[]{new RevisionRange(convertRevision(fromRevision), convertRevision(toRevision))};
  }

  static DirEntry.Kind convertNodeKind(int nodeKind) {
    return DirEntry.Kind.valueOf(NodeKind.getNodeKindName(nodeKind).trim().toUpperCase());
  }

}