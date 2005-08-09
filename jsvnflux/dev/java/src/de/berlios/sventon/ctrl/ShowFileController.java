package de.berlios.sventon.ctrl;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * ShowFileController.
 * @author patrikfr@users.berlios.de
 */
public class ShowFileController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
                                   HttpServletRequest request, HttpServletResponse response) throws SVNException {

    Map<String, Object> model = new HashMap<String, Object>();
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    logger.debug("Assembling file contents for: " + svnCommand.getPath());

    HashMap properties = new HashMap();
    repository.getFile(svnCommand.getPath(), revision.getNumber(), properties, outStream);
    logger.debug(properties);

    if ("application/octet-stream".equals(properties.get("svn:mime-type"))) {
      logger.debug("Binary file detected");
    } else {
      String fileContents = ((Colorer) getApplicationContext().getBean("colorer")).getColorizedContent(
          outStream.toString(), svnCommand.getTarget());
      logger.debug("Create model");
      model.put("fileContents", fileContents);
    }

    return new ModelAndView("showfile", model);
  }

}
