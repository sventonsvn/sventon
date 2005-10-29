package de.berlios.sventon.ctrl;

import de.berlios.sventon.blame.BlameHandler;
import de.berlios.sventon.colorer.Colorer;
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

/**
 * BlameController.
 * <P>
 * Controller to support the blame/praise/annotate functionality of Subversion.
 * @author patrikfr@users.berlios.de
 */
public class BlameController extends AbstractSVNTemplateController implements Controller {

  private static final SVNRevision FIRST_REVISION = SVNRevision.parse("1");

  private Colorer colorer;

  /**
   * Sets the <tt>Colorer</tt> instance.
   *
   * @param colorer The instance.
   */
  public void setColorer(Colorer colorer) {
    this.colorer = colorer;
  }

  /**
   * Gets <tt>Colorer</tt> instance.
   *
   * @return The instance.
   */
  public Colorer getColorer() {
    return colorer;
  }

  @Override
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
                                   HttpServletRequest request, HttpServletResponse response) throws SVNException {

    logger.debug("Blaming path: " + svnCommand.getCompletePath() + ", rev: " + FIRST_REVISION + " - " + revision);

    BlameHandler blameHandler = new BlameHandler();

//    repository.doAnnotate(svnCommand.getPath(), FIRST_REVISION, revision, blameHandler);

    blameHandler.colorizeContent(getColorer(), svnCommand.getTarget());
    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();
    logger.debug("Adding blameHandler: " + blameHandler);
    model.put("handler", blameHandler);
    model.put("properties", new HashMap());   //TODO: Replace with valid entry properties
    return new ModelAndView("blame", model);
  }
}
