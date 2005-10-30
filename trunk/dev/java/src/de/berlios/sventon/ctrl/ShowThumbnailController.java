package de.berlios.sventon.ctrl;

import de.berlios.sventon.command.SVNBaseCommand;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

/**
 * Controller for showing selected repository entries as thumbnails.
 *
 * @author jesper@users.berlios.de
 */
public class ShowThumbnailController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
                                   HttpServletRequest request, HttpServletResponse response) throws SVNException {
    final String[] entryParameters = request.getParameterValues("entry");

    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();

    logger.debug("Showing thumbnail images");
    for(String entry : entryParameters) {
      logger.debug("entry: " + entry);
    }

    model.put("thumbnailentries", Arrays.asList(entryParameters));
    return new ModelAndView("showthumbs", model);

  }
}
