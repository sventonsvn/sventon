package de.berlios.sventon.ctrl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import de.berlios.sventon.command.SVNBaseCommand;

/**
 * GoToController.
 * <p>
 * Controller to inspect the given path/revision combo and forward to appropriate
 * controller.
 * <p>
 * This controller performs pretty much the same thing as the post handler in 
 * {@link de.berlios.sventon.ctrl.AbstractSVNTemplateController}, but can be 
 * called as a GET request. This gives a somewhat ugly redundancy that probably 
 * should be rmoved.
 * 
 * @author patrikfr@users.berlios.de
 */
public class GoToController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, 
      SVNBaseCommand svnCommand, SVNRevision revision, HttpServletRequest request, 
      HttpServletResponse response, BindException exception) throws SVNException {

    String redirectUrl = null;

    logger.debug("Checking node kind for command: " + svnCommand);
    SVNNodeKind kind = repository.checkPath(svnCommand.getCompletePath(), revision.getNumber());

    logger.debug("Node kind: " + kind);

    if (kind == SVNNodeKind.DIR) {
      redirectUrl = "repobrowser.svn";
    } else if (kind == SVNNodeKind.FILE) {
      redirectUrl = "showfile.svn";
    } else {
      //Invalid path/rev combo. Forward to error page.
      exception.rejectValue("path", "goto.command.invalidpath", "Invalid path");
      return prepareExceptionModelAndView(exception, svnCommand);
    }
    logger.debug("Submitted command: " + svnCommand);
    logger.debug("Redirecting to: " + redirectUrl);

    Map<String, Object> m = new HashMap<String, Object>();
    m.put("path", svnCommand.getPath());
    m.put("revision", svnCommand.getRevision());
    return new ModelAndView(new RedirectView(redirectUrl), m);
  }

}
