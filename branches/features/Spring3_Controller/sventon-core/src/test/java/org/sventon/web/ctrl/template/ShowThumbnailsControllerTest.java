package org.sventon.web.ctrl.template;

import org.junit.Test;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.model.PathRevision;
import org.sventon.web.command.MultipleEntriesCommand;
import org.sventon.web.command.editor.PathRevisionEditor;

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

    final PathRevisionEditor pathRevisionEditor = new PathRevisionEditor();
    command.setEntries(pathRevisionEditor.convert(pathEntries));

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