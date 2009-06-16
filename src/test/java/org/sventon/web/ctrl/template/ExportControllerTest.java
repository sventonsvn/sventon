package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.export.ExportExecutor;
import org.sventon.model.RepositoryName;
import org.sventon.model.UserRepositoryContext;
import org.sventon.web.command.MultipleEntriesCommand;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;

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
    final SVNFileRevision[] entriesToExport = new SVNFileRevision[]{
        new SVNFileRevision("/trunk/file1", 100, new SVNProperties(), new SVNProperties()),
        new SVNFileRevision("/tags/test/file2", 101, new SVNProperties(), new SVNProperties())
    };
    command.setName(new RepositoryName("test"));
    command.setEntries(entriesToExport);

    assertFalse(context.getIsWaitingForExport());
    assertNull(context.getExportUuid());

    final ExportController ctrl = new ExportController(new ExportExecutor() {
      public UUID submit(MultipleEntriesCommand command, SVNRepository repository, long pegRevision) {
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
