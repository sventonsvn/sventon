package org.sventon.service.javahl;

import org.sventon.model.ChangeType;
import org.sventon.model.ChangedPath;
import org.sventon.model.DirEntry;
import org.sventon.model.RevisionProperty;
import org.tigris.subversion.javahl.ChangePath;
import org.tigris.subversion.javahl.NodeKind;
import org.tigris.subversion.javahl.Revision;
import org.tigris.subversion.javahl.RevisionRange;

import java.util.*;

public class JavaHLConverter {

  static Revision convertRevision(long revision) {
    if (revision == org.sventon.model.Revision.HEAD.getNumber()){
      return Revision.HEAD;
    }

    return Revision.getInstance(revision);
  }

  static Set<ChangedPath> convertChangedPaths(ChangePath[] changePaths) {
    final Set<ChangedPath> changedPaths = new TreeSet<ChangedPath>();
    if (changePaths != null) {
      for (ChangePath cp : changePaths) {
        changedPaths.add(new ChangedPath(cp.getPath(), cp.getCopySrcPath(), cp.getCopySrcRevision(), ChangeType.parse(cp.getAction())));
      }
    }

    return changedPaths;
  }

  static Map<RevisionProperty, String> convertRevisionPropertyMap(Map map) {
    final HashMap<RevisionProperty, String> propertyMap = new HashMap<RevisionProperty, String>();

    if (map != null) {
      for (Object o : map.keySet()) {
        String property = (String) o;
        propertyMap.put(RevisionProperty.byName(property), (String) map.get(property));

      }
    }

    return propertyMap;
  }

  static RevisionRange[] getRevisionRange(long fromRevision, long toRevision) {
    return new RevisionRange[]{new RevisionRange(convertRevision(fromRevision), convertRevision(toRevision))};
  }

  public static DirEntry.Kind convertNodeKind(int nodeKind) {
    return DirEntry.Kind.valueOf(NodeKind.getNodeKindName(nodeKind).trim().toUpperCase());
  }
}