package de.berlios.sventon.ctrl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.sun.crypto.provider.BlowfishParameters;

public class BlameController extends AbstractSVNTemplateController implements Controller {

  private static final int FIRST_REVISION = 1;

  @Override
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, long revision,
      HttpServletRequest request, HttpServletResponse response) throws SVNException {
    
    logger.debug("Blaming path: " + svnCommand.getPath() + ", rev: " + FIRST_REVISION + " - " + revision);
    
    BlameHandler blameHandler = new BlameHandler();
    repository.annotate(svnCommand.getPath(), FIRST_REVISION, revision, blameHandler);
    
    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();
    logger.debug("Adding blameHandler: " + blameHandler);
    model.put("handler", blameHandler);

    return new ModelAndView("blame", model);
  }
}
