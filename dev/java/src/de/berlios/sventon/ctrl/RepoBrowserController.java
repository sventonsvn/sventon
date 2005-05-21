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

public class RepoBrowserController extends AbstractSVNTemplateController implements Controller {

  @Override
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, long revision,
      HttpServletRequest request, HttpServletResponse response) throws SVNException {
    
    List<SVNDirEntry> dir = Collections.checkedList(new ArrayList<SVNDirEntry>(), SVNDirEntry.class);
    
    logger.debug("Getting directory contents for: " + svnCommand.getPath());
    HashMap properties = new HashMap();
    dir.addAll(repository.getDir(svnCommand.getPath(), revision, properties, new ArrayList()));
    logger.debug(properties);
    Collections.sort(dir, new SVNDirEntryComparator(NAME, true));

    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("svndir", dir);

    return new ModelAndView("repobrowser", model);
  }
}
