/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.SVNConnection;
import org.sventon.export.ExportExecutor;
import org.sventon.model.PathRevision;
import org.sventon.model.RepositoryName;
import org.sventon.model.Revision;
import org.sventon.web.UserRepositoryContext;
import org.sventon.web.command.MultipleEntriesCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.*;

public class ExportControllerTest {
  private static final String UUID_STRING = "c5eaa2ba-2655-444b-aa64-c15ecff3e6da";

  @Test
  public void testExport() throws Exception {
    final File tempFile = File.createTempFile("sventon-", "-test");
    tempFile.deleteOnExit();

    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final UserRepositoryContext context = new UserRepositoryContext();
    final MultipleEntriesCommand command = new MultipleEntriesCommand();
    final PathRevision[] entriesToExport = new PathRevision[]{
        new PathRevision("/trunk/file1", Revision.create(100)),
        new PathRevision("/tags/test/file2", Revision.create(101))
    };
    command.setName(new RepositoryName("test"));
    command.setEntries(entriesToExport);

    assertFalse(context.getIsWaitingForExport());
    assertNull(context.getExportUuid());

    final ExportController ctrl = new ExportController(new ExportExecutor() {
      public UUID submit(MultipleEntriesCommand command, SVNConnection connection, long pegRevision) {
        return UUID.fromString(UUID_STRING);
      }

      public void downloadByUUID(UUID uuid, HttpServletRequest request, HttpServletResponse response) throws IOException {
      }

      public void delete(UUID uuid) {
      }

      public int getProgress(UUID uuid) {
        return 1;
      }
    });

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 123, context, request, response, null);
    assertNotNull(modelAndView);

    assertTrue(context.getIsWaitingForExport());
    assertEquals(UUID_STRING, context.getExportUuid().toString());
  }
}
