package de.berlios.sventon.ctrl;

import de.berlios.sventon.svnsupport.IndexEntry;
import de.berlios.sventon.svnsupport.RevisionIndex;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

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
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, long revision,
      HttpServletRequest request, HttpServletResponse response) throws SVNException {
    
    List<IndexEntry> entries = Collections.checkedList(new ArrayList<IndexEntry>(), IndexEntry.class);

    // Make sure the path starts with a slash as that
    // is the path structure of the revision index.
    String fromPath = request.getParameter("path");
    if (!fromPath.startsWith("/")) {
      fromPath = "/" + fromPath;
    }

    logger.debug("Flattening directories below: " + fromPath);

    RevisionIndex index = (RevisionIndex) getApplicationContext().getBean("revisionIndex");
    index.setRepository(repository);
    entries.addAll(index.getDirectories(fromPath));
    logger.debug(entries.size() + " entries found.");

    //TODO: Fix sorting for IndexEntries.
    //Collections.sort(entries, new SVNDirEntryComparator(NAME, true));

    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("svndir", entries);

    return new ModelAndView("searchresult", model);
  }
}
