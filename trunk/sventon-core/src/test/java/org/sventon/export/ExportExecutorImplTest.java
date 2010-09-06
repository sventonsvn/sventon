package org.sventon.export;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.sventon.SVNConnection;
import org.sventon.TestUtils;
import org.sventon.appl.ConfigDirectory;
import org.sventon.model.PathRevision;
import org.sventon.service.RepositoryService;
import org.sventon.util.WebUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ExportExecutorImplTest {

  private static final String UUID_STRING = "c5eaa2ba-2655-444b-aa64-c15ecff3e6da";

  @Test
  public void testPrepareResponse() throws Exception {
    final ConfigDirectory configDirectoryMock = createMock(ConfigDirectory.class);
    final ExportExecutorImpl exportExecutor = new ExportExecutorImpl(configDirectoryMock);
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();

    assertEquals(0, response.getHeaderNames().size());
    assertNull(response.getContentType());
    exportExecutor.prepareResponse(request, response, new File("testfile"));
    assertEquals(1, response.getHeaderNames().size());
    assertEquals("attachment; filename=\"testfile\"", response.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER));
    assertEquals(WebUtils.APPLICATION_OCTET_STREAM, response.getContentType());
  }

  @Test
  public void testExportTask() throws Exception {
    final List<PathRevision> entries = new ArrayList<PathRevision>();

    final ConfigDirectory configDirectoryMock = createMock(ConfigDirectory.class);
    final ExportDirectory exportDirectoryMock = createMock(ExportDirectory.class);
    final RepositoryService repositoryServiceMock = createMock(RepositoryService.class);
    final SVNConnection connection = createMock(SVNConnection.class);

    expect(configDirectoryMock.getExportDirectory()).andStubReturn(new File(TestUtils.TEMP_DIR));
    expect(exportDirectoryMock.getDirectory()).andReturn(new File("."));
    expect(exportDirectoryMock.getUUID()).andStubReturn(UUID.fromString(UUID_STRING));
    expect(exportDirectoryMock.mkdirs()).andStubReturn(true);
    repositoryServiceMock.export(connection, entries, 123, exportDirectoryMock);
    exportDirectoryMock.delete();
    expect(exportDirectoryMock.compress()).andStubReturn(new File(TestUtils.TEMP_DIR));

    replay(configDirectoryMock);
    replay(exportDirectoryMock);
    replay(connection);

    final ExportExecutorImpl exportExecutor = new ExportExecutorImpl(configDirectoryMock);

    exportExecutor.setRepositoryService(repositoryServiceMock);

    final ExportExecutorImpl.ExportTask exportTask = exportExecutor.new ExportTask(exportDirectoryMock,
        connection, entries, 123);

    exportTask.call();

    verify(configDirectoryMock);
    verify(exportDirectoryMock);
    verify(connection);
  }
}
