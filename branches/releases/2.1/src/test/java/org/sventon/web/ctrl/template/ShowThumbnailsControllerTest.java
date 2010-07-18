package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.SVNRepositoryStub;
import org.sventon.util.SVNFileRevisionEditor;
import org.sventon.web.command.MultipleEntriesCommand;
import org.tmatesoft.svn.core.io.SVNFileRevision;

import java.util.List;
import java.util.Map;

public class ShowThumbnailsControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final ConfigurableMimeFileTypeMap mftm = new ConfigurableMimeFileTypeMap();
    mftm.afterPropertiesSet();

    final MultipleEntriesCommand command = new MultipleEntriesCommand();
    final ShowThumbnailsController ctrl = new ShowThumbnailsController(mftm);

    final String[] pathEntries = new String[]{
        "file1.gif@123",
        "file2.jpg@123",
        "file.abc@123"};

    final SVNFileRevisionEditor svnFileRevisionEditor = new SVNFileRevisionEditor();
    command.setEntries(svnFileRevisionEditor.convert(pathEntries));

    final MockHttpServletRequest req = new MockHttpServletRequest();
    req.addParameter(GetFileController.DISPLAY_REQUEST_PARAMETER, GetFileController.CONTENT_DISPOSITION_INLINE);

    final ModelAndView modelAndView = ctrl.svnHandle(new SVNRepositoryStub(),
        command, 100, null, req, null, null);

    final Map model = modelAndView.getModel();
    final List entries = (List) model.get("thumbnailentries");

    assertEquals(2, entries.size());

    final SVNFileRevision entry0 = (SVNFileRevision) entries.get(0);
    assertEquals("file1.gif", entry0.getPath());
    assertEquals(123, entry0.getRevision());
  }

}