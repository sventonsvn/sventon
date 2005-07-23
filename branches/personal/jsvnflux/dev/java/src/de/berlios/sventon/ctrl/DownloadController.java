package de.berlios.sventon.ctrl;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * Controller used when downloading single files.
 * @author jesper@users.berlios.de
 */
public class DownloadController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, long revision,
                                   HttpServletRequest request, HttpServletResponse response) throws SVNException {

    logger.debug("Downloading file: " + svnCommand.getPath());

    response.setContentType("application/octetstream");
    response.setHeader("Content-disposition", "attachment; filename=\"" + svnCommand.getTarget() + "\"");

    ServletOutputStream output = null;
    try {
      HashMap properties = new HashMap();
      output = response.getOutputStream();
      repository.getFile(svnCommand.getPath(), revision, properties, output);
      logger.debug(properties);
      output.flush();
      output.close();
    } catch (IOException ioex) {
      logger.error(ioex);
    }

    return null;
  }

}
