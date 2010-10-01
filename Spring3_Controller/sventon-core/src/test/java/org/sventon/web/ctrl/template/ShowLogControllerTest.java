package org.sventon.web.ctrl.template;

import org.junit.Before;
import org.junit.Test;
import org.sventon.model.Revision;

import static org.junit.Assert.assertEquals;

public class ShowLogControllerTest {

  private ShowLogController controller;

  @Before
  public void setUp() throws Exception {
    controller = new ShowLogController();
  }

  @Test
  public void testCalculateFromRevision() throws Exception {
    assertEquals(2, controller.calculateFromRevision(2, Revision.HEAD));
    assertEquals(1, controller.calculateFromRevision(2, Revision.create(1)));
  }
}
