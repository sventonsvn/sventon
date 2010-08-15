package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.SVNConnection;
import org.sventon.export.ExportExecutor;
import org.sventon.model.PathRevision;
import org.sventon.model.RepositoryName;
import org.sventon.model.Revision;
import org.sventon.model.UserRepositoryContext;
import org.sventon.web.command.MultipleEntriesCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ExportControllerTest extends TestCase {
  private static final String UUID_STRING = "c5eaa2ba-2655-444b-aa64-c15ecff3e6da";

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

      public boolean isExported(UUID uuid) {
        return false;
      }
    });

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 123, context, request, response, null);
    assertNotNull(modelAndView);

    assertTrue(context.getIsWaitingForExport());
    assertEquals(UUID_STRING, context.getExportUuid().toString());
  }
}
