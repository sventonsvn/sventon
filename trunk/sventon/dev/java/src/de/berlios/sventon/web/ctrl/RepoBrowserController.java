/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.RepositoryEntrySorter;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.FileExtensionList;
import de.berlios.sventon.web.model.UserContext;
import de.berlios.sventon.web.support.FileExtensionFilter;
import org.springframework.validation.BindException;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RepoBrowserController.
 *
 * @author patrikfr@users.berlios.de
 * @author jesper@users.berlios.de
 */
public class RepoBrowserController extends AbstractSVNTemplateController implements Controller {

  @SuppressWarnings("unchecked")
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserContext userContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final String filterExtension = RequestUtils.getStringParameter(request, "filterExtension", "all");

    logger.debug("filterExtension: " + filterExtension);

    // Update trailing / for path
    if (!svnCommand.getPath().endsWith("/")) {
      svnCommand.setPath(svnCommand.getPath() + "/");
    }

    final String completePath = svnCommand.getPath();
    final Map<String, SVNLock> locks = getLocks(repository, completePath);

    logger.debug("Getting directory contents for: " + completePath);
    final HashMap properties = new HashMap();
    final Collection<SVNDirEntry> entries =
        repository.getDir(completePath, revision.getNumber(), properties, (Collection) null);

    final FileExtensionList fileExtensionList = new FileExtensionList(entries);
    logger.debug("Existing extensions in dir: " + fileExtensionList.getExtensions());

    final List<RepositoryEntry> directoryListing;
    if ("all".equals(filterExtension)) {
      directoryListing = RepositoryEntry.createEntryCollection(entries, completePath, locks);
    } else {
      final FileExtensionFilter fileExtensionFilter = new FileExtensionFilter(filterExtension);
      directoryListing = fileExtensionFilter.filter(RepositoryEntry.createEntryCollection(entries, completePath, locks));
    }

    new RepositoryEntrySorter(userContext.getSortType(), userContext.getSortMode()).sort(directoryListing);

    logger.debug("Create model");
    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("svndir", directoryListing);
    logger.debug(properties);
    model.put("properties", properties);
    model.put("existingExtensions", fileExtensionList.getExtensions());
    model.put("filterExtension", filterExtension);
    return new ModelAndView("repobrowser", model);
  }
}
