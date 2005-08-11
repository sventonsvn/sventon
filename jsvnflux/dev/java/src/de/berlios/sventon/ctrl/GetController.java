package de.berlios.sventon.ctrl;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * Controller used when downloading single files.
 * @author jesper@users.berlios.de
 */
public class GetController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
                                   HttpServletRequest request, HttpServletResponse response) throws SVNException {

    logger.debug("Getting file: " + svnCommand.getPath());

    String displayType = request.getParameter("disp");
    String contentType = "application/octetstream";

    if ("inline".equals(displayType)) {
      logger.debug("Getting file as 'inline' content.");
      // TODO: Content type handling could be moved out of here. May be useful in more places.
      if ("gif".equalsIgnoreCase(svnCommand.getFileExtension())) {
        contentType = "image/gif";
      } else if ("png".equalsIgnoreCase(svnCommand.getFileExtension())) {
        contentType = "image/png";
      } else if ("jpg".equalsIgnoreCase(svnCommand.getFileExtension())) {
        contentType = "image/jpg";
      } else if ("jpe".equalsIgnoreCase(svnCommand.getFileExtension())) {
        contentType = "image/jpg";
      } else if ("jpeg".equalsIgnoreCase(svnCommand.getFileExtension())) {
        contentType = "image/jpg";
      }
      response.setHeader("Content-disposition", "inline; filename=\"" + svnCommand.getTarget() + "\"");
    } else {
      logger.debug("Getting file as 'attachment' content.");
      response.setHeader("Content-disposition", "attachment; filename=\"" + svnCommand.getTarget() + "\"");
    }
    response.setContentType(contentType);

    ServletOutputStream output = null;
    try {
      HashMap properties = new HashMap();
      output = response.getOutputStream();
      repository.getFile(svnCommand.getPath(), revision.getNumber(), properties, output);
      logger.debug(properties);
      output.flush();
      output.close();
    } catch (IOException ioex) {
      logger.error(ioex);
    }

    return null;
  }

}
