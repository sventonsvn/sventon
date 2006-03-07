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
package de.berlios.sventon.ctrl;

import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.index.RevisionIndexer;
import de.berlios.sventon.svnsupport.RepositoryFactory;
import de.berlios.sventon.svnsupport.SventonException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.springframework.web.servlet.view.RedirectView;
import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;
import static org.tmatesoft.svn.core.wc.SVNRevision.HEAD;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for use by controllers whishing to make use of basic
 * plumbing functionality such as authorization and basic repository configuration.
 * <p/>
 * This abstract controller is based on the GoF Template pattern, the method to
 * implement for extending controllers is
 * <code>{@link #svnHandle(SVNRepository, SVNBaseCommand, SVNRevision, HttpServletRequest, HttpServletResponse, BindException)}</code>.
 * <p/>
 * Workflow for this controller:
 * <ol>
 * <li>The controller inspects the repository configuration object to see if it's
 * user id and pwd have been provided during setup. If credentials are
 * configured they will be used for authorized repository access, if they do not
 * exist the controller will try to set up the repository with anonymous access.
 * If this fails the user will be forwarded to an error page.
 * <li>The controller configures the <code>SVNRepository</code> object and
 * calls the extending class'
 * {@link #svnHandle(SVNRepository, SVNBaseCommand, SVNRevision, HttpServletRequest, HttpServletResponse, BindException)}
 * method with the given {@link de.berlios.sventon.command.SVNBaseCommand}
 * containing request parameters.
 * <li>After the call returns, the controller adds additional information to
 * the the model (see below) and forwards the request to the view returned
 * together with the model by the
 * {@link #svnHandle(SVNRepository, SVNBaseCommand, SVNRevision, HttpServletRequest, HttpServletResponse, BindException)}
 * method.
 * </ol>
 * <b>Model</b><br>
 * The following information will be added by this controller to the model
 * returned by the controller called (see flow above): <table>
 * <tr>
 * <th>key</th>
 * <th>content</th>
 * </tr>
 * <tr>
 * <td>url</td>
 * <td>SVN URL as configured for this web application</td>
 * </tr>
 * <tr>
 * <td>numrevision</td>
 * <td>SVN revision this request concerns, actual revision number</td>
 * </tr>
 * <tr>
 * <td>command</td>
 * <td>{@link de.berlios.sventon.command.SVNBaseCommand}-object</td>
 * </tr>
 * </table> <p/> <b>Input arguments</b><br>
 * Input to this argument is wrapped in a
 * <code>{@link de.berlios.sventon.command.SVNBaseCommand}</code> object by the
 * Spring framework. If the extending controller is configured in the Spring
 * config file with a validator for the <code>SVNBaseCommand</code> it will be
 * checked for binding errors. If binding errors were detected an exception
 * model will be created an control forwarded to an error view. respectively.
 * <b>Exception handling</b>
 * <dl>
 * <dt>Authentication exception
 * <dd>If a SVN authentication exception occurs during the call the request
 * will be forwarded to the authenticationfailuare.jsp page.
 * <dt>Other SVN exceptions
 * <dd>Other SVN exceptons are currently forwarded to a generic error handlng
 * page.
 * </dl>
 *
 * @author patrikfr@users.berlios.de
 */
public abstract class AbstractSVNTemplateController extends AbstractCommandController {

  protected RepositoryConfiguration configuration = null;

  private RevisionIndexer revisionIndexer;

  /**
   * Logger for this class and subclasses.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * Cached most recent log entry.
   */
  private static SVNLogEntry cachedLogs;

  /**
   * Cached revision number (HEAD).
   */
  private static long cachedRevision;

  protected AbstractSVNTemplateController() {
    // TODO: Move to XML-file?
    setCommandClass(SVNBaseCommand.class);
  }

  /**
   * {@inheritDoc}
   */
  public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command,
                             BindException exception) {

    SVNBaseCommand svnCommand = (SVNBaseCommand) command;

    // If repository config is not ok - redirect to config.jsp
    if (!configuration.isConfigured()) {
      logger.debug("sventon not configured, redirecting to 'config.svn'");
      return new ModelAndView(new RedirectView("config.svn"));
    }

    if (exception.hasErrors()) {
      return prepareExceptionModelAndView(exception, svnCommand);
    }

    SVNRevision revision = convertAndUpdateRevision(svnCommand);

    try {
      SVNRepository repository = RepositoryFactory.INSTANCE.getRepository(configuration);

      final ModelAndView modelAndView = svnHandle(repository, svnCommand, revision, request, response, exception);

      // It's ok for svnHandle to return null in cases like GetController.
      // If the view is a RedirectView it's model has already been populated
      if (modelAndView != null && !(modelAndView.getView() instanceof RedirectView)) {
        long latestRevision = repository.getLatestRevision();
        Map<String, Object> model = new HashMap<String, Object>();
        logger.debug("'command' set to: " + svnCommand);
        model.put("command", svnCommand); // This is for the form to work
        model.put("url", configuration.getUrl());
        model.put("numrevision", (revision == HEAD ? Long.toString(latestRevision) : null));
        model.put("latestCommitInfo", getLatestRevisionInfo(repository, latestRevision));
        model.put("isIndexing", getRevisionIndexer().isIndexing());
        modelAndView.addAllObjects(model);
      }
      return modelAndView;
    } catch (SVNAuthenticationException svnae) {
      return forwardToAuthenticationFailureView(svnae);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      Throwable cause = ex.getCause();
      if (cause instanceof NoRouteToHostException || cause instanceof ConnectException) {
        exception.reject("error.message.no-route-to-host");
      } else {
        exception.reject(null, ex.getMessage());
      }
      return prepareExceptionModelAndView(exception, svnCommand);
    }

  }

  /**
   * Gets the repository locks, recursively.
   *
   * @param repository The repository
   * @param startPath  The start path. If <code>null</code> locks will be gotten from root.
   * @return Lock info
   */
  protected Map<String, SVNLock> getLocks(final SVNRepository repository, final String startPath) throws SVNException {
    final String path = startPath == null ? "/" : startPath;
    logger.debug("Getting lock info for path [" + path + "] and below");

    final Map<String, SVNLock> locks = new HashMap<String, SVNLock>();
    SVNLock[] locksArray = null;

    try {
      locksArray = repository.getLocks(path);
      logger.debug("Locks found: " + Arrays.asList(locksArray));
      for (SVNLock lock : locksArray) {
        locks.put(lock.getPath(), lock);
      }
    } catch (SVNException svne) {
      logger.info("Unable to get locks for path [" + path + "]. Directory may not exist in HEAD");
    }
    return locks;
  }

  /**
   * Gets the latest commit log and puts the result into the cache.
   * If cache already contains the latest info it will be returned
   * directly.
   *
   * @param repository     The repository
   * @param latestRevision The latest revision
   * @return The <tt>SVNLogEntry</tt> for the latest revision
   * @throws SVNException if subversion error.
   */
  private SVNLogEntry getLatestRevisionInfo(final SVNRepository repository, final long latestRevision) throws SVNException {
    if (latestRevision != cachedRevision) {
      cachedLogs = getRevisionInfo(repository, latestRevision);
      cachedRevision = latestRevision;
    }
    return cachedLogs;
  }

  /**
   * Gets the latest commit log.
   *
   * @param repository The repository
   * @param revision   The revision
   * @return The <tt>SVNLogEntry</tt> for given revision
   * @throws SVNException if subversion error.
   */
  protected SVNLogEntry getRevisionInfo(final SVNRepository repository, final long revision) throws SVNException {
    String[] targetPaths = new String[]{"/"}; // the path to show logs for
    return (SVNLogEntry) repository.log(
        targetPaths, null, revision, revision, true, false).iterator().next();
  }

  /**
   * Prepare authentication model. This setus up a model and redirect view with
   * all stuff needed to redirect control to the login page.
   *
   * @param svnae The SVNAuthenticationException.
   * @return Redirect view for logging in, with original request info stored in
   *         session to enable the authentication control to proceed with
   *         original request once the user is authenticated.
   */
  private ModelAndView forwardToAuthenticationFailureView(final SVNAuthenticationException svnae) {
    logger.debug("Authentication failed, forwarding to 'authenticationfailure' view");
    logger.error("Authentication failed", svnae);
    return new ModelAndView("authenticationfailure");
  }

  /**
   * Prepares the exception model and view with basic data
   * needed to for displaying a useful error message.
   *
   * @param exception  Bind exception from Spring MVC validation.
   * @param svnCommand Command object.
   * @return The packaged model and view.
   */
  @SuppressWarnings("unchecked")
  protected ModelAndView prepareExceptionModelAndView(final BindException exception, final SVNBaseCommand svnCommand) {
    final Map<String, Object> model = exception.getModel();
    logger.debug("'command' set to: " + svnCommand);
    model.put("command", svnCommand);
    model.put("url", configuration.getUrl());
    model.put("numrevision", null);
    return new ModelAndView("goto", model);
  }

  /**
   * Converts the revision <code>String</code> to a format suitable for SVN,
   * also handles special logical revision HEAD. <code>null</code> and empty
   * string revision are converted to the HEAD revision.
   * <p/>
   * The given <code>SVNBaseCommand</code> instance will be updated with key
   * word HEAD, if revision was <code>null</code> or empty <code>String</code>.
   * <p/>
   * TODO: This (could perhaps) be a suitable place to also handle conversion of
   * date to revision to expand possible user input to handle calendar
   * intervals.
   *
   * @param svnCommand Command object.
   * @return The converted SVN revision.
   */
  private SVNRevision convertAndUpdateRevision(final SVNBaseCommand svnCommand) {
    if (svnCommand.getRevision() != null && !"".equals(svnCommand.getRevision())
        && !"HEAD".equals(svnCommand.getRevision())) {
      return SVNRevision.parse(svnCommand.getRevision());
    } else {
      svnCommand.setRevision("HEAD");
      return HEAD;
    }
  }

  /**
   * Abstract method to be implemented by the controller subclassing this
   * controller. This is where the actual work takes place. See class
   * documentation for info on workflow and on how all this works together.
   *
   * @param repository Reference to the repository, prepared with authentication
   *                   if applicable.
   * @param svnCommand Command (basically request parameters submitted in user
   *                   request)
   * @param revision   SVN type revision.
   * @param request    Servlet request.
   * @param response   Servlet response.
   * @param exception  BindException, could be used by the subclass to add error
   *                   messages to the exception.
   * @return Model and view to render.
   * @throws SventonException Thrown if a sventon error occurs.
   * @throws SVNException     Thrown if exception occurs during SVN operations.
   */
  protected abstract ModelAndView svnHandle(final SVNRepository repository,
                                            SVNBaseCommand svnCommand, SVNRevision revision,
                                            HttpServletRequest request, HttpServletResponse response,
                                            BindException exception) throws SventonException, SVNException;


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
   * Set repository configuration.
   *
   * @param configuration Configuration
   */
  public void setRepositoryConfiguration(final RepositoryConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Get current repository configuration.
   *
   * @return Configuration
   */
  public RepositoryConfiguration getRepositoryConfiguration() {
    return configuration;
  }

}
