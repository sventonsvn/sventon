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
