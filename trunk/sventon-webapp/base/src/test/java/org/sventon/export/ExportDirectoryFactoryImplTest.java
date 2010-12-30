/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
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
