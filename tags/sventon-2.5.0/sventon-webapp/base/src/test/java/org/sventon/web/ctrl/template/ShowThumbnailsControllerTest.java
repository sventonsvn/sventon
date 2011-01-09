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

import org.junit.Test;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.model.PathRevision;
import org.sventon.web.command.MultipleEntriesCommand;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ShowThumbnailsControllerTest {

  @Test
  public void testSvnHandle() throws Exception {
    final ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();
    fileTypeMap.afterPropertiesSet();

    final MultipleEntriesCommand command = new MultipleEntriesCommand();
    final ShowThumbnailsController ctrl = new ShowThumbnailsController(fileTypeMap);

    final String[] pathEntries = new String[]{
        "file1.gif@123",
        "file2.jpg@123",
        "file.abc@123"};

    command.setEntries(PathRevision.parse(pathEntries));

    final MockHttpServletRequest req = new MockHttpServletRequest();
    req.addParameter(GetFileController.DISPLAY_REQUEST_PARAMETER, GetFileController.CONTENT_DISPOSITION_INLINE);

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, req, null, null);

    final Map model = modelAndView.getModel();
    final List entries = (List) model.get("thumbnailentries");

    assertEquals(2, entries.size());

    final PathRevision entry0 = (PathRevision) entries.get(0);
    assertEquals("file1.gif", entry0.getPath());
    assertEquals(123, entry0.getRevision().getNumber());
  }

}