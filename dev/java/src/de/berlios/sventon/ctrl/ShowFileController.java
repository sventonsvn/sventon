package de.berlios.sventon.ctrl;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

public class ShowFileController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, long revision,
      HttpServletRequest request, HttpServletResponse response) throws SVNException {

    Map<String, Object> model = new HashMap<String, Object>();

    ByteArrayOutputStream outStream = new ByteArrayOutputStream();

    logger.debug("Assembling file contents");
    repository.getFile(svnCommand.getPath(), revision, new HashMap(), outStream);

    logger.debug("Create model");
    model.put("fileContents", outStream.toString());

    return new ModelAndView("showfile", model);
  }

}
