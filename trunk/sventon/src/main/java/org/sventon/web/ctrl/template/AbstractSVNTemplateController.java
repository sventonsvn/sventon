/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.ctrl.template;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.springframework.web.servlet.view.RedirectView;
import org.sventon.RepositoryConnectionFactory;
import org.sventon.appl.Application;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.cache.CacheGateway;
import org.sventon.model.AvailableCharsets;
import org.sventon.model.RepositoryName;
import org.sventon.model.UserRepositoryContext;
import org.sventon.service.RepositoryService;
import org.sventon.util.RepositoryEntryComparator;
import org.sventon.util.RepositoryEntrySorter;
import org.sventon.web.command.SVNBaseCommand;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyEditor;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for use by controllers whishing to make use of basic
 * plumbing functionality such as authorization and basic application configuration.
 * <p/>
 * This abstract controller is based on the GoF Template pattern, the method to
 * implement for extending controllers is
 * <code>{@link #svnHandle(SVNRepository,SVNBaseCommand,long,UserRepositoryContext,
 * HttpServletRequest,HttpServletResponse,BindException)}</code>.
 * <p/>
 * Workflow for this controller:
 * <ol>
 * <li>The controller inspects the application configuration object to see if it's
 * user id and pwd have been provided during setup. If credentials are
 * configured they will be used for authorized repository access, if they do not
 * exist the controller will try to set up the repository with anonymous access.
 * If this fails the user will be forwarded to an error page.
 * <li>The controller configures the <code>SVNRepository</code> object and
 * calls the extending class'
 * {@link #svnHandle(SVNRepository,SVNBaseCommand,long,UserRepositoryContext,
 * HttpServletRequest,HttpServletResponse,BindException)}
 * method with the given {@link org.sventon.web.command.SVNBaseCommand}
 * containing request parameters.
 * <li>After the call returns, the controller adds additional information to
 * the the model (see below) and forwards the request to the view returned
 * together with the model by the
 * {@link #svnHandle(SVNRepository,SVNBaseCommand,long, org.sventon.model.UserRepositoryContext ,
 * HttpServletRequest,HttpServletResponse,BindException)}
 * method.
 * </ol>
 * <b>Input arguments</b><br>
 * Input to this argument is wrapped in a
 * <code>{@link SVNBaseCommand}</code> object by the
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
 * @author patrik@sventon.org
 * @author jesper@sventon.org
 */
public abstract class AbstractSVNTemplateController extends AbstractCommandController {

  /**
   * The application.
   */
  private Application application;

  /**
   * Gateway class for accessing the caches.
   */
  private CacheGateway cacheGateway;

  /**
   * Maximum number of revisions.
   */
  private int maxRevisionsCount;

  /**
   * Maximum number of entries per page.
   */
  private int maxEntriesCount;

  /**
   * Destination view name.
   */
  private String viewName;

  /**
   * Cached available charsets.
   */
  private AvailableCharsets availableCharsets;

  /**
   * Mode for searching entries.
   */
  public static final String ENTRIES_SEARCH_MODE = "entries";

  /**
   * Mode for searching log messages.
   */
  public static final String LOGMESSAGES_SEARCH_MODE = "logMessages";

  /**
   * Request parameter controlling charset.
   */
  private static final String CHARSET_REQUEST_PARAMETER = "charset";

  /**
   * Request parameter controlling search mode.
   */
  private static final String SEARCH_MODE_REQUEST_PARAMETER = "searchMode";

  /**
   * Request parameter controlling revision count.
   */
  private static final String REVISION_COUNT_REQUEST_PARAMETER = "revcount";

  /**
   * Service.
   */
  private RepositoryService repositoryService;

  /**
   * The repository factory.
   */
  private RepositoryConnectionFactory repositoryConnectionFactory;

  /**
   * Name property editor instance.
   */
  private PropertyEditor nameEditor;

  /**
   * Revision editor instance.
   */
  private PropertyEditor revisionEditor;

  /**
   * Revision editor instance.
   */
  private PropertyEditor svnFileRevisionEditor;

  /**
   * The first possible repository revision.
   */
  public static final long FIRST_REVISION = 1;

  /**
   * Sets the editor.
   *
   * @param nameEditor Editor.
   */
  public void setNameEditor(final PropertyEditor nameEditor) {
    this.nameEditor = nameEditor;
  }

  /**
   * Sets the editor.
   *
   * @param revisionEditor Editor.
   */
  public void setRevisionEditor(final PropertyEditor revisionEditor) {
    this.revisionEditor = revisionEditor;
  }

  /**
   * Sets the editor.
   *
   * @param svnFileRevisionEditor Editor.
   */
  public void setSvnFileRevisionEditor(final PropertyEditor svnFileRevisionEditor) {
    this.svnFileRevisionEditor = svnFileRevisionEditor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder) throws Exception {
    binder.registerCustomEditor(RepositoryName.class, nameEditor);
    binder.registerCustomEditor(SVNRevision.class, revisionEditor);
    binder.registerCustomEditor(SVNFileRevision.class, svnFileRevisionEditor);
  }

  /**
   * {@inheritDoc}
   */
  public final ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                   final Object cmd, final BindException errors) {

    final SVNBaseCommand command = (SVNBaseCommand) cmd;
    logger.info(command);

    // If application config is not ok - redirect to config
    if (!application.isConfigured()) {
      logger.debug("sventon not configured, redirecting to '/repos/listconfigs'");
      return new ModelAndView(new RedirectView("/repos/listconfigs", true));
    }

    if (command.getName() == null || !application.getRepositoryNames().contains(command.getName())) {
      logger.debug("RepositoryName [" + command.getName() + "] does not exist, redirecting to '/repos/list'");
      return new ModelAndView(new RedirectView("/repos/list", true));
    }

    if (errors.hasErrors()) {
      return prepareExceptionModelAndView(errors, command);
    }

    SVNRepository repository = null;
    try {
      final RepositoryConfiguration configuration = application.getRepositoryConfiguration(command.getName());
      final UserRepositoryContext repositoryContext = UserRepositoryContext.getContext(request, command.getName());
      final boolean showLatestRevInfo = ServletRequestUtils.getBooleanParameter(request, "showlatestrevinfo", false);

      repository = createConnection(configuration, repositoryContext);
      final long headRevision = getRepositoryService().getLatestRevision(repository);
      command.translateRevision(headRevision, repository);

      parseAndUpdateSortParameters(command, repositoryContext);
      parseAndUpdateLatestRevisionsDisplayCount(request, repositoryContext);
      parseAndUpdateCharsetParameter(request, repositoryContext);
      parseAndUpdateSearchModeParameter(request, repositoryContext);

      final ModelAndView modelAndView = svnHandle(repository, command, headRevision, repositoryContext, request, response, errors);

      // It's ok for svnHandle to return null in cases like GetController.
      // If the view is a RedirectView it's model has already been populated
      if (modelAndView != null && !(modelAndView.getView() instanceof RedirectView)) {
        final Map<String, Object> model = new HashMap<String, Object>();
        logger.debug("'command' set to: " + command);
        model.put("command", command); // This is for the form to work
        model.put("repositoryURL", configuration.getRepositoryDisplayUrl());
        model.put("isHead", command.getRevisionNumber() == headRevision);
        model.put("headRevision", headRevision);
        model.put("isUpdating", application.isUpdating(command.getName()));
        model.put("useCache", configuration.isCacheUsed());
        model.put("isZipDownloadsAllowed", configuration.isZippedDownloadsAllowed());
        model.put("isEntryTrayEnabled", configuration.isEntryTrayEnabled());
        model.put("repositoryNames", application.getRepositoryNames());
        model.put("maxRevisionsCount", getMaxRevisionsCount());
        model.put("charsets", availableCharsets.getCharsets());
        model.put("userRepositoryContext", repositoryContext);

        if (showLatestRevInfo) {
          logger.debug("Fetching [" + repositoryContext.getLatestRevisionsDisplayCount() + "] latest revisions for display");
          final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
          try {
            logEntries.addAll(getRepositoryService().getRevisions(command.getName(), repository, headRevision,
                FIRST_REVISION, "/", repositoryContext.getLatestRevisionsDisplayCount(), false));
          } catch (SVNException svnex) {
            if (SVNErrorCode.FS_NO_SUCH_REVISION == svnex.getErrorMessage().getErrorCode()) {
              logger.info(svnex.getMessage());
            } else {
              logger.error(svnex.getMessage());
            }
          }
          model.put("revisions", logEntries);
        }

        modelAndView.addAllObjects(model);
      }
      return modelAndView;
    } catch (SVNAuthenticationException svnae) {
      logger.debug(svnae.getMessage());
      return forwardToAuthenticationFailureView(request);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      final Throwable cause = ex.getCause();
      if (cause instanceof NoRouteToHostException || cause instanceof ConnectException) {
        errors.reject("error.message.no-route-to-host");
      } else {
        errors.reject(null, ex.getMessage());
      }
      return prepareExceptionModelAndView(errors, command);
    } finally {
      if (repository != null) {
        repository.closeSession();
      }
    }

  }

  /**
   * Creates a repository connection.
   *
   * @param configuration     Configuration
   * @param repositoryContext Context
   * @return Connection
   * @throws SVNException if a subversion error occur.
   */
  protected SVNRepository createConnection(final RepositoryConfiguration configuration, UserRepositoryContext repositoryContext) throws SVNException {
    final SVNRepository repository;
    final RepositoryName repositoryName = configuration.getName();
    final SVNURL svnurl = configuration.getSVNURL();

    if (configuration.isAccessControlEnabled() && repositoryContext.hasCredentials()) {
      repository = repositoryConnectionFactory.createConnection(repositoryName, svnurl, repositoryContext.getCredentials());
    } else {
      repository = repositoryConnectionFactory.createConnection(repositoryName, svnurl, configuration.getCredentials());
    }
    return repository;
  }

  /**
   * Parses the parameter controlling what charset to use.
   *
   * @param request     The request.
   * @param userContext The UserContext instance to update.
   */
  private void parseAndUpdateCharsetParameter(final HttpServletRequest request,
                                              final UserRepositoryContext userContext) {
    final String charset = ServletRequestUtils.getStringParameter(request, CHARSET_REQUEST_PARAMETER, null);
    if (charset != null) {
      userContext.setCharset(charset);
    } else if (userContext.getCharset() == null) {
      userContext.setCharset(availableCharsets.getDefaultCharset());
    }
  }

  /**
   * Parses the parameter controlling what search mode to use.
   *
   * @param request     The request.
   * @param userContext The UserContext instance to update.
   */
  private void parseAndUpdateSearchModeParameter(final HttpServletRequest request,
                                                 final UserRepositoryContext userContext) {
    final String searchMode = ServletRequestUtils.getStringParameter(request, SEARCH_MODE_REQUEST_PARAMETER, null);
    if (searchMode != null) {
      userContext.setSearchMode(searchMode);
    } else if (userContext.getSearchMode() == null) {
      userContext.setSearchMode(ENTRIES_SEARCH_MODE);
    }
  }

  /**
   * Parses the parameter controlling how many revisions should be displayed in the
   * <i>latest commit info</i> DIV.
   *
   * @param request     The request.
   * @param userContext The UserContext instance to update.
   */
  private void parseAndUpdateLatestRevisionsDisplayCount(final HttpServletRequest request,
                                                         final UserRepositoryContext userContext) {
    final int latestRevisionsDisplayCount =
        ServletRequestUtils.getIntParameter(request, REVISION_COUNT_REQUEST_PARAMETER, 0);
    if (latestRevisionsDisplayCount <= getMaxRevisionsCount() && latestRevisionsDisplayCount >= 0) {
      if (latestRevisionsDisplayCount > 0) {
        userContext.setLatestRevisionsDisplayCount(latestRevisionsDisplayCount);
      }
    } else {
      throw new IllegalArgumentException("Illegal revision count: " + latestRevisionsDisplayCount);
    }
  }

  /**
   * Parses sort mode and type parameters from the request instance and
   * updates the <code>UserContext</code> instance.
   *
   * @param command     The Command.
   * @param userContext The UserContext instance to update.
   */
  protected final void parseAndUpdateSortParameters(final SVNBaseCommand command,
                                                    final UserRepositoryContext userContext) {

    if (command.getSortType() != null) {
      userContext.setSortType(command.getSortType());
    } else if (userContext.getSortType() == null) {
      userContext.setSortType(RepositoryEntryComparator.SortType.FULL_NAME);
    }

    if (command.getSortMode() != null) {
      userContext.setSortMode(command.getSortMode());
    } else if (userContext.getSortMode() == null) {
      userContext.setSortMode(RepositoryEntrySorter.SortMode.ASC);
    }
  }

  /**
   * Prepare authentication model. This setus up a model and redirect view with
   * all stuff needed to redirect control to the login page.
   *
   * @param request Request.
   * @return Redirect view for logging in, with original request info stored in
   *         session to enable the authentication control to proceed with
   *         original request once the user is authenticated.
   */
  private ModelAndView forwardToAuthenticationFailureView(final HttpServletRequest request) {
    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("parameters", request.getParameterMap());
    model.put("action", request.getRequestURL());
    logger.debug("Forwarding to 'authenticationfailure' view");
    return new ModelAndView("error/authenticationFailure", model);
  }

  /**
   * Prepares the exception model and view with basic data
   * needed to for displaying a useful error message.
   *
   * @param exception Bind exception from Spring MVC validation.
   * @param command   Command object.
   * @return The packaged model and view.
   */
  @SuppressWarnings("unchecked")
  final ModelAndView prepareExceptionModelAndView(final BindException exception,
                                                  final SVNBaseCommand command) {
    final RepositoryConfiguration repositoryConfiguration = application.getRepositoryConfiguration(command.getName());
    final Map<String, Object> model = exception.getModel();
    model.put("command", command);
    model.put("repositoryURL", repositoryConfiguration != null ? repositoryConfiguration.getRepositoryDisplayUrl() : "");
    return new ModelAndView("goto", model);
  }

  /**
   * Abstract method to be implemented by the controller subclassing this
   * controller. This is where the actual work takes place. See class
   * documentation for info on workflow and on how all this works together.
   *
   * @param repository            Reference to the repository, prepared with authentication
   *                              if applicable.
   * @param command               Command (basically request parameters submitted in user
   *                              request)
   * @param headRevision          The head revision.
   * @param userRepositoryContext The user's context instance for this repository.
   * @param request               Servlet request.
   * @param response              Servlet response.
   * @param exception             BindException, could be used by the subclass to add error
   *                              messages to the exception.
   * @return Model and view to render.
   * @throws Exception Thrown if exception occurs during SVN operations.
   */
  protected abstract ModelAndView svnHandle(final SVNRepository repository,
                                            final SVNBaseCommand command,
                                            final long headRevision,
                                            final UserRepositoryContext userRepositoryContext,
                                            final HttpServletRequest request,
                                            final HttpServletResponse response,
                                            final BindException exception) throws Exception;


  /**
   * Sets the cache gateway instance.
   *
   * @param cacheGateway The cache gateway instance.
   */
  public final void setCacheGateway(final CacheGateway cacheGateway) {
    this.cacheGateway = cacheGateway;
  }

  /**
   * Gets the cache instance.
   *
   * @return The instance.
   */
  final CacheGateway getCache() {
    return cacheGateway;
  }

  /**
   * Sets the application.
   *
   * @param application Application
   */
  public final void setApplication(final Application application) {
    this.application = application;
  }

  /**
   * Get current application configuration.
   *
   * @param name Repository name
   * @return ApplicationConfiguration
   */
  final RepositoryConfiguration getRepositoryConfiguration(final RepositoryName name) {
    return application.getRepositoryConfiguration(name);
  }

  /**
   * Gets the repository service instance.
   *
   * @return Repository service
   */
  final RepositoryService getRepositoryService() {
    return repositoryService;
  }

  /**
   * Sets the repository service instance.
   *
   * @param repositoryService The service instance.
   */
  public final void setRepositoryService(final RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

  /**
   * Sets the maximum number of revisions.
   *
   * @param maxRevisionsCount Max count.
   */
  public final void setMaxRevisionsCount(final int maxRevisionsCount) {
    this.maxRevisionsCount = maxRevisionsCount;
  }

  /**
   * Gets the maximum number of revisions a user can choose to display
   * in the <i>latest commit info</i> DIV.
   *
   * @return Count
   */
  final int getMaxRevisionsCount() {
    return maxRevisionsCount;
  }

  /**
   * Sets the maximum number of entries per page.
   *
   * @param maxEntriesCount Max count.
   */
  public final void setMaxEntriesCount(final int maxEntriesCount) {
    this.maxEntriesCount = maxEntriesCount;
  }

  /**
   * Gets the maximum number of entries per page.
   *
   * @return Count
   */
  protected final int getMaxEntriesCount() {
    return maxEntriesCount;
  }

  /**
   * Sets the available charsets.
   *
   * @param availableCharsets Charsets
   */
  public final void setAvailableCharsets(final AvailableCharsets availableCharsets) {
    this.availableCharsets = availableCharsets;
  }

  /**
   * Sets the repository connection factory instance.
   *
   * @param repositoryConnectionFactory Factory instance.
   */
  public void setRepositoryConnectionFactory(final RepositoryConnectionFactory repositoryConnectionFactory) {
    this.repositoryConnectionFactory = repositoryConnectionFactory;
  }

  /**
   * Sets the destination view name.
   *
   * @param viewName View name.
   */
  public void setViewName(final String viewName) {
    this.viewName = viewName;
  }

  /**
   * Gets the destination view name.
   *
   * @return View name.
   */
  public String getViewName() {
    return viewName;
  }

}
