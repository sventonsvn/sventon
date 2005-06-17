package de.berlios.sventon.ctrl;

import static de.berlios.sventon.svnsupport.SVNDirEntryComparator.NAME;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNDirEntry;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import de.berlios.sventon.svnsupport.SVNDirEntryComparator;
import de.berlios.sventon.svnsupport.RevisionIndex;

/**
 * Controller used when searching for file or directory entries in the repository.
 * @author jesper@users.berlios.de
 */
public class SearchController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, long revision,
      HttpServletRequest request, HttpServletResponse response) throws SVNException {
    
    List<SVNDirEntry> entries = Collections.checkedList(new ArrayList<SVNDirEntry>(), SVNDirEntry.class);
    
    logger.debug("Searching index for: " + request.getParameter("sventonSearchString"));

    RevisionIndex index = (RevisionIndex) getApplicationContext().getBean("revisionIndex");
    index.setRepository(repository);
    entries.addAll(index.find(request.getParameter("sventonSearchString")));
    Collections.sort(entries, new SVNDirEntryComparator(NAME, true));

    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("svndir", entries);

    return new ModelAndView("repobrowser", model);
  }
}
