package org.sventon.export;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.sventon.appl.ConfigDirectory;

import java.io.File;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

public class ExportDirectoryFactoryImplTest {

  @Test
  public void testSetArchiveFileCharset() throws Exception {
    final ConfigDirectory configDirectoryMock = EasyMock.createMock(ConfigDirectory.class);
    expect(configDirectoryMock.getExportDirectory()).andStubReturn(new File("."));
    replay(configDirectoryMock);
    final ExportExecutorImpl ctrl = new ExportExecutorImpl(configDirectoryMock);
    assertEquals(ExportExecutorImpl.FALLBACK_CHARSET, ctrl.getArchiveFileCharset().toString());
    ctrl.setArchiveFileCharset(null);
    assertEquals(ExportExecutorImpl.FALLBACK_CHARSET, ctrl.getArchiveFileCharset().toString());
    ctrl.setArchiveFileCharset("UTF-8");
    assertEquals("UTF-8", ctrl.getArchiveFileCharset().toString());
    verify(configDirectoryMock);
  }

}
