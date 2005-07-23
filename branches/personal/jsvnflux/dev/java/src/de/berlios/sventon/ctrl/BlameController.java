package de.berlios.sventon.ctrl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * BlameController.
 * <P>
 * Controller to support the blame/praise/annotate functionality of Subversion.
 * @author patrikfr@users.berlios.de
 */
public class BlameController extends AbstractSVNTemplateController implements Controller {

  private static final SVNRevision FIRST_REVISION = SVNRevision.parse("1");

  @Override
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
      HttpServletRequest request, HttpServletResponse response) throws SVNException {
    
    logger.debug("Blaming path: " + svnCommand.getPath() + ", rev: " + FIRST_REVISION + " - " + revision);
    
    BlameHandler blameHandler = new BlameHandler();

//    repository.doAnnotate(svnCommand.getPath(), FIRST_REVISION, revision, blameHandler);
    
    blameHandler.colorizeContent((Colorer) getApplicationContext().getBean("colorer"),
        svnCommand.getFileExtension(), true);
    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();
    logger.debug("Adding blameHandler: " + blameHandler);
    model.put("handler", blameHandler);

    return new ModelAndView("blame", model);
  }
}
