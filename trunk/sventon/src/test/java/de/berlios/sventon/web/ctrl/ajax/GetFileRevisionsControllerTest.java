package de.berlios.sventon.web.ctrl.ajax;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetFileRevisionsControllerTest extends TestCase {

  public void testFindNextPreviousRevision() throws Exception {
    final GetFileRevisionsController ctrl = new GetFileRevisionsController();
    List<Long> revisions = new ArrayList<Long>();

    long targetRevision = 1;
    assertNull(ctrl.findPreviousRevision(revisions, targetRevision));
    assertNull(ctrl.findNextRevision(revisions, targetRevision));

    revisions.add(1L);
    assertNull(ctrl.findPreviousRevision(revisions, targetRevision));
    assertNull(ctrl.findNextRevision(revisions, targetRevision));

    targetRevision++;
    assertNull(ctrl.findPreviousRevision(revisions, targetRevision));
    assertNull(ctrl.findNextRevision(revisions, targetRevision));

    revisions.add(2L);
    assertEquals(new Long(1), ctrl.findPreviousRevision(revisions, targetRevision));
    assertNull(ctrl.findNextRevision(revisions, targetRevision));

    revisions.add(3L);

    assertEquals(new Long(1), ctrl.findPreviousRevision(revisions, targetRevision));
    assertEquals(new Long(3), ctrl.findNextRevision(revisions, targetRevision));

    revisions.clear();

    revisions.add(100L);
    revisions.add(120L);

    assertEquals(new Long(100), ctrl.findPreviousRevision(revisions, 120));
    assertNull(ctrl.findNextRevision(revisions, 120));

    revisions.add(130L);
    assertEquals(new Long(130), ctrl.findNextRevision(revisions, 120));

    revisions.clear();
    revisions = Arrays.asList(54L, 56L, 57L);

    assertNull(ctrl.findPreviousRevision(revisions, 54));
    assertEquals(new Long(56), ctrl.findNextRevision(revisions, 54));
  }

}