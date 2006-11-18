package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.config.InstanceConfiguration;
import de.berlios.sventon.diff.DiffException;
import de.berlios.sventon.web.command.DiffCommand;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserContext;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
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
 * Abstract DiffController to be subclasses by diff generating controllers.
 *
 * @author jesper@users.berlios.de
 */
public abstract class AbstractDiffController extends AbstractSVNTemplateController implements Controller {

  private String viewName;

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserContext userContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final String[] entries = ServletRequestUtils.getStringParameters(request, "entry");
    logger.debug("Diffing file content for: " + svnCommand);
    final Map<String, Object> model = new HashMap<String, Object>();

    try {
      final DiffCommand diffCommand = new DiffCommand(entries);
      model.put("diffCommand", diffCommand);
      logger.debug("Using: " + diffCommand);

      model.putAll(diffInternal(repository, diffCommand,
          getConfiguration().getInstanceConfiguration(svnCommand.getName())));

    } catch (DiffException dex) {
      model.put("diffException", dex.getMessage());
    }

    logger.debug("Forwarding to view: " + viewName);
    return new ModelAndView(viewName, model);
  }

  /**
   * Internal method for creating the diff between two entries.
   *
   * @param repository    The repository instance.
   * @param diffCommand   The <code>DiffCommand</code> including to/from diff instructions.
   * @param configuration Instance configuration.
   * @return A populated map containing the following info:
   *         <ul>
   *         <li><i>isBinary</i>, indicates whether any of the entries were a binary file.</li>
   *         <li><i>leftFileContent</i>, <code>List</code> of <code>SourceLine</code>s for the left (from) file.</li>
   *         <li><i>rightFileContent</i>, <code>List</code> of <code>SourceLine</code>s for the right (to) file.</li>
   *         </ul>
   * @throws DiffException if unable to produce diff.
   * @throws SVNException  if a subversion error occurs.
   */
  protected abstract Map<String, Object> diffInternal(final SVNRepository repository,
                                                      final DiffCommand diffCommand,
                                                      final InstanceConfiguration configuration)
      throws DiffException, SVNException;

  /**
   * Gets the view name.
   *
   * @return View name
   */
  public String getViewName() {
    return viewName;
  }

  /**
   * Sets the view name
   *
   * @param viewName View name to set.
   */
  public void setViewName(final String viewName) {
    this.viewName = viewName;
  }

}
