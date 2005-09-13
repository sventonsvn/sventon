package de.berlios.sventon.ctrl;

import de.berlios.sventon.index.RevisionIndex;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Controller used when flatting directory structure.
 * @author jesper@users.berlios.de
 */
public class FlattenController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
      HttpServletRequest request, HttpServletResponse response) throws SVNException {
    
    List<RepositoryEntry> entries = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);

    // Make sure the path starts with a slash as that
    // is the path structure of the revision index.
    String fromPath = svnCommand.getCompletePath();
    if (!fromPath.startsWith("/")) {
      logger.debug("Appending initial slash.");
      fromPath = "/" + fromPath;
    }

    logger.debug("Flattening directories below: " + fromPath);
    RevisionIndex index = (RevisionIndex) getApplicationContext().getBean("revisionIndex");
    // TODO: Should be set from app-context-xml
    index.setRepository(repository);
    entries.addAll(index.getDirectories(fromPath));
    logger.debug(entries.size() + " entries found.");

    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("svndir", entries);
    model.put("search", true);  // Indicates that path should be shown in browser view.
    return new ModelAndView("repobrowser", model);
  }
}
