package de.berlios.sventon.ctrl;

import de.berlios.sventon.index.RevisionIndexer;
import de.berlios.sventon.command.SVNBaseCommand;

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
 * Controller used when searching for file or directory entries in the repository.
 * @author jesper@users.berlios.de
 */
public class SearchController extends AbstractSVNTemplateController implements Controller {

  private RevisionIndexer revisionIndexer;

  /**
   * Sets the revision indexer instance.
   *
   * @param revisionIndexer The instance.
   */
  public void setRevisionIndexer(final RevisionIndexer revisionIndexer) {
    this.revisionIndexer = revisionIndexer;
  }

  /**
   * Gets the revision indexer instance.
   *
   * @return The instance.
   */
  public RevisionIndexer getRevisionIndexer() {
    return revisionIndexer;
  }

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
      HttpServletRequest request, HttpServletResponse response, BindException exception) throws SVNException {
    
    List<RepositoryEntry> entries = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);
    logger.debug("Searching index for: " + request.getParameter("sventonSearchString"));
    entries.addAll(getRevisionIndexer().find(request.getParameter("sventonSearchString")));

    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("svndir", entries);
    model.put("isSearch", true);  // Indicates that path should be shown in browser view.
    return new ModelAndView("repobrowser", model);
  }
}
