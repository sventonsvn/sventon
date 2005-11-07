package de.berlios.sventon.ctrl;

import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.util.ImageUtil;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
                                   HttpServletRequest request, HttpServletResponse response, BindException exception) throws SVNException {
    final String[] entryParameters = request.getParameterValues("entry");

    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();
    List<String> entries = new ArrayList<String>();

    logger.debug("Showing thumbnail images");
    // Check what entries are image files - and add them to the list of thumbnails.
    for(String entry : entryParameters) {
      logger.debug("entry: " + entry);
      if (ImageUtil.isImageFilename(entry)) {
        entries.add(entry);
      }
    }
    logger.debug(entries.size() + " entries out of " + entryParameters.length
        + " are image files");

    model.put("thumbnailentries", entries);
    return new ModelAndView("showthumbs", model);

  }
}
