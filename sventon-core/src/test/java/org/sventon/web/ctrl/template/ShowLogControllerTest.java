package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import org.sventon.model.Revision;

public class ShowLogControllerTest extends TestCase {

  private ShowLogController controller;

  @Override
  protected void setUp() throws Exception {
    controller = new ShowLogController();
  }

  public void testCalculateFromRevision() throws Exception {
    assertEquals(2, controller.calculateFromRevision(2, Revision.HEAD));
    assertEquals(1, controller.calculateFromRevision(2, Revision.create(1)));
  }
}
