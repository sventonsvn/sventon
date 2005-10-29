package de.berlios.sventon.ctrl;

import de.berlios.sventon.svnsupport.RepositoryEntryComparator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static de.berlios.sventon.svnsupport.RepositoryEntryComparator.NAME;
import de.berlios.sventon.command.SVNBaseCommand;

/**
 * RepoBrowserController.
 * 
 * @author patrikfr@users.berlios.de
 */
public class RepoBrowserController extends AbstractSVNTemplateController implements Controller {

  @SuppressWarnings("unchecked")
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
                                   HttpServletRequest request, HttpServletResponse response) throws SVNException {

    List<RepositoryEntry> dir = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);

    // Update trailing / for path
    if (!svnCommand.getCompletePath().endsWith("/")) {
      svnCommand.setPath(svnCommand.getPath() + "/");
    }

    String completePath = svnCommand.getCompletePath();
    logger.debug("Getting directory contents for: " + completePath);
    HashMap properties = new HashMap();
    Collection entries = repository.getDir(completePath, revision.getNumber(), properties, (Collection) null);
    for (Object entry : entries) {
      dir.add(new RepositoryEntry((SVNDirEntry) entry, completePath, svnCommand.getMountPoint(false)));
    }

    Collections.sort(dir, new RepositoryEntryComparator(NAME, true));

    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("svndir", dir);
    logger.debug(properties);
    model.put("properties", properties);

    return new ModelAndView("repobrowser", model);
  }
}
