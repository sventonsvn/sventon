package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.colorer.Colorer;
import org.sventon.model.AnnotatedTextFile;
import org.sventon.model.RepositoryName;
import org.sventon.model.Revision;
import org.sventon.model.UserRepositoryContext;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.BaseCommand;

import java.io.IOException;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

public class BlameControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final RepositoryService mockService = EasyMock.createMock(RepositoryService.class);

    final Colorer colorer = new Colorer() {
      public String getColorizedContent(String content, String fileExtension, String encoding) throws IOException {
        return content;
      }
    };

    final AnnotatedTextFile annotatedFile = new AnnotatedTextFile();
    final UserRepositoryContext context = new UserRepositoryContext();
    context.setCharset("UTF-8");

    final BaseCommand command = new BaseCommand();
    command.setPath("trunk/test");
    command.setName(new RepositoryName("test"));
    command.setRevision(Revision.create(12));

    final BlameController ctrl = new BlameController(colorer);
    ctrl.setRepositoryService(mockService);

    expect(mockService.blame(null, command.getPath(), command.getRevisionNumber(), context.getCharset(), colorer)).andStubReturn(annotatedFile);
    replay(mockService);

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, context, null, null, null);
    final Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(1, model.size());
    assertEquals(annotatedFile, model.get("annotatedFile"));
  }
}