package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.appl.RepositoryName;
import de.berlios.sventon.colorer.Colorer;
import de.berlios.sventon.model.AnnotatedTextFile;
import de.berlios.sventon.service.RepositoryService;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserRepositoryContext;
import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.IOException;
import java.util.Map;

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

    final SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("trunk/test");
    command.setName(new RepositoryName("test"));
    command.setRevision(SVNRevision.create(12));

    final BlameController ctrl = new BlameController();
    ctrl.setRepositoryService(mockService);
    ctrl.setColorer(colorer);

    expect(mockService.blame(null, command.getPath(), command.getRevisionNumber(), context.getCharset(), colorer)).andStubReturn(annotatedFile);
    replay(mockService);

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, context, null, null, null);
    final Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(1, model.size());
    assertEquals(annotatedFile, model.get("annotatedFile"));
    assertEquals("blame", modelAndView.getViewName());
  }
}