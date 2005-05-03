package de.berlios.sventon.ctrl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNDirEntry;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

public class RepoBrowserController extends AbstractSVNTemplateController implements Controller {

  @Override
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, long revision,
      HttpServletRequest request, HttpServletResponse response) throws SVNException {
    
    Collection<SVNDirEntry> dir = null;
    
    logger.debug("Getting directory contents");
    dir = repository.getDir(svnCommand.getPath(), revision, new HashMap(), new ArrayList());
    
    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("svndir", dir);

    return new ModelAndView("repobrowser", model);
  }
}
