package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.sventon.export.ExportDirectory;
import org.sventon.export.ExportDirectoryFactory;
import org.sventon.model.RepositoryName;
import org.sventon.model.UserRepositoryContext;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.MultipleEntriesCommand;
import org.sventon.util.WebUtils;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.io.SVNFileRevision;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ExportControllerTest extends TestCase {

  public void testExport() throws Exception {
    final File tempFile = File.createTempFile("sventon-", "-test");
    tempFile.deleteOnExit();
    final ExportDirectory exportDirectoryMock = EasyMock.createMock(ExportDirectory.class);
    final RepositoryService repositoryServiceMock = EasyMock.createMock(RepositoryService.class);

    final ExportController ctrl = new ExportController();
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final UserRepositoryContext context = new UserRepositoryContext();
    final MultipleEntriesCommand command = new MultipleEntriesCommand();
    final SVNFileRevision[] entriesToExport = new SVNFileRevision[]{
        new SVNFileRevision("/trunk/file1", 100, new SVNProperties(), new SVNProperties()),
        new SVNFileRevision("/tags/test/file2", 101, new SVNProperties(), new SVNProperties())
    };
    command.setEntries(entriesToExport);

    ctrl.setRepositoryService(repositoryServiceMock);
    ctrl.setExportDirectoryFactory(new ExportDirectoryFactory() {
      public ExportDirectory create(RepositoryName repositoryName) {
        return exportDirectoryMock;
      }
    });

    repositoryServiceMock.export(null, Arrays.asList(entriesToExport), -1, exportDirectoryMock);
    expect(exportDirectoryMock.compress()).andStubReturn(tempFile);
    exportDirectoryMock.delete();

    replay(exportDirectoryMock);
    replay(repositoryServiceMock);
    ctrl.svnHandle(null, command, 123, context, request, response, null);
    verify(exportDirectoryMock);
    verify(repositoryServiceMock);

    assertEquals("application/octet-stream", response.getContentType());
    assertEquals("", response.getContentAsString());
    assertTrue(((String)response.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).matches(
        "attachment; filename=\"sventon-\\d++-test\""
    ));
  }
}
