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
 * Controller used when searching for file or directory entries in the repository.
 * @author jesper@users.berlios.de
 */
public class SearchController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
      HttpServletRequest request, HttpServletResponse response) throws SVNException {
    
    List<RepositoryEntry> entries = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);
    logger.debug("Searching index for: " + request.getParameter("sventonSearchString"));
    RevisionIndex index = (RevisionIndex) getApplicationContext().getBean("revisionIndex");
    // TODO: Should be set from app-context-xml
    index.setRepository(repository);
    entries.addAll(index.find(request.getParameter("sventonSearchString")));
    logger.debug(entries.size() + " entries found.");

    //TODO: Fix sorting for IndexEntries.
    //Collections.sort(entries, new RepositoryEntryComparator(NAME, true));

    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("svndir", entries);
    model.put("search", true);  // Indicates that path should be shown in browser view.
    return new ModelAndView("repobrowser", model);
  }
}
