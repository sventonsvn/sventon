package de.berlios.sventon.ctrl;

import static org.tmatesoft.svn.core.wc.SVNRevision.HEAD;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.springframework.web.servlet.view.RedirectView;
import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * Abstract base class for use by controllers whishing to make use of basic
 * plumbing functionalit such as authorization and basic repository
 * configuration.
 * <p>
 * This abstract controller is based on the GoF Template pattern, the method to
 * implement for extending controllers is
 * <code>{@link #svnHandle(SVNRepository, SVNBaseCommand, SVNRevision, HttpServletRequest, HttpServletResponse)}</code>.
 * <p>
 * Workflow for this controller:
 * <ol>
 * <li>The controller inspects the repository configuration object to see if it 
 * user id and pwd have been provided during setup. If credentials are configured
 * they will be used for authorized repository access, if they do not
 * exist the controller will try to set up the repository with anonymous access.
 * If this fails the user will be forwarded to an error page.
 * <li>The controller configures the <code>SVNRepository</code> object and
 * calls the extending class'
 * {@link #svnHandle(SVNRepository, SVNBaseCommand, SVNRevision, HttpServletRequest, HttpServletResponse)}
 * method with the given {@link de.berlios.sventon.ctrl.SVNBaseCommand}
 * containing request parameters.
 * <li>After the call returns, the controller adds additional information to
 * the the model (see below) and forwards the request to the view returned
 * together with the model by the
 * {@link #svnHandle(SVNRepository, SVNBaseCommand, SVNRevision, HttpServletRequest, HttpServletResponse)}
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
 * <td>SVN URL as configured for this webb application</td>
 * </tr>
 * <tr>
 * <td>numrevision</td>
 * <td>SVN revision this request concerns, actual revision number</td>
 * </tr>
 * <tr>
 * <td>command</td>
 * <td>{@link de.berlios.sventon.ctrl.SVNBaseCommand}-object</td>
 * </tr>
 * </table> <p/> <b>Input arguments</b><br>
 * Input to this argument is wrapped in a
 * <code>{@link de.berlios.sventon.ctrl.SVNBaseCommand}</code> object by the
 * Spring framework. If the extending controller is configured in the Spring
 * config file with a validator for the <code>SVNBaseCommand</code> it will be
 * checked for binding errors. If binding errors were detected an exception
 * model will be created an control forwarded to an error view. respectively.
 * <b>Exception handling</b>
 * <dl>
 * <dt>Authentication exception
 * <dd>If a SVN authentication exception occurs during the call the request will
 * be forwarded to the authenticationfailuare.jsp page.
 * <dt>Other SVN exceptions
 * <dd>Other SVN exceptons are currently forwarded to a generic error handlng
 * page.
 * </dl>
 * <b>GoTo form support</b> This controller also contains support for rendering
 * the GoTo form and processing GoTo form submission.
 * 
 * @author patrikfr@users.berlios.de
 */
public abstract class AbstractSVNTemplateController extends AbstractFormController {

  protected RepositoryConfiguration configuration = null;

  /** Logger for this class and subclasses. */
  protected final Log logger = LogFactory.getLog(getClass());

  protected AbstractSVNTemplateController() {
    // TODO: Move to XML-file?
    setCommandClass(SVNBaseCommand.class);
    setBindOnNewForm(true);
    setSessionForm(false);
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

  /**
   * {@inheritDoc}
   */
  @Override
  protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response,
      Object command, BindException exception) throws Exception {
    SVNBaseCommand svnCommand = (SVNBaseCommand) command;

    // If repository config is not ok - redirect to config.jsp
    if (!configuration.isConfigured()) {
      return new ModelAndView(new RedirectView("config.svn"));
    }
    
    if (exception.hasErrors()) {
      return prepareExceptionModelAndView(exception, svnCommand);
    }

    SVNRepository repository = SVNRepositoryFactory.create(configuration.getSVNURL());
    assignCredentials(repository);

    SVNRevision revision = revision = convertAndUpdateRevision(svnCommand);

    String redirectUrl = null;

    try {

      logger.debug("Checking node kind for command: " + svnCommand);
      SVNNodeKind kind = repository.checkPath(svnCommand.getPath(), revision.getNumber());

      logger.debug("Node kind: " + kind);

      if (kind == SVNNodeKind.DIR) {
        redirectUrl = "repobrowser.svn";
      } else if (kind == SVNNodeKind.FILE) {
        redirectUrl = "showfile.svn";
      } else {
        // Invalid path/rev combo. Forward to error page.
        exception.rejectValue("path", "goto.command.invalidpath", "Invalid path");
        return prepareExceptionModelAndView(exception, svnCommand);
      }
    } catch (SVNAuthenticationException svnae) {
      // Redirect to login page. This will lose track of the submit, the user
      // will have to start over
      // after logging in. That's OK. Yes.
      return forwardToAuthenticationFailureView(request, svnCommand);
    } catch (SVNException e) {
      logger.error("SVN Exception", e);
      Throwable cause = e.getCause();
      if (cause instanceof NoRouteToHostException || cause instanceof ConnectException) {
        exception.reject("error.message.no-route-to-host");
      } else {
        exception.reject(null, e.getMessage());
      }
      return prepareExceptionModelAndView(exception, svnCommand);
    }

    logger.debug("Submitted command: " + svnCommand);
    logger.debug("Redirecting to: " + redirectUrl);

    Map<String, Object> m = new HashMap<String, Object>();
    m.put("path", svnCommand.getPath());
    m.put("revision", svnCommand.getRevision());
    return new ModelAndView(new RedirectView(redirectUrl), m);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException exception)
      throws Exception {
    // This is for preparing the requested model and view and also rendering the
    // "Go To" form.
    
    //Also validate the form backing command (this is not done by Spring MVC and must be handled manually)
    getValidator().validate(exception.getTarget(), exception);

    return handle(request, response, exception.getTarget(), exception);
  }

  /**
   * {@inheritDoc}
   */
  public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command,
      BindException exception) throws ServletException, IOException {

    SVNBaseCommand svnCommand = (SVNBaseCommand) command;

    // If repository config is not ok - redirect to config.jsp
    if (!configuration.isConfigured()) {
      return new ModelAndView(new RedirectView("config.svn"));
    }
    
    if (exception.hasErrors()) {
      return prepareExceptionModelAndView(exception, svnCommand);
    }

    SVNRevision revision = convertAndUpdateRevision(svnCommand);

    try {
      logger.debug("Getting SVN repository");
      SVNRepository repository = SVNRepositoryFactory.create(configuration.getSVNURL());
      assignCredentials(repository);

      final ModelAndView modelAndView = svnHandle(repository, svnCommand, revision, request, response);

      Map<String, Object> model = new HashMap<String, Object>();
      logger.debug("'command' set to: " + svnCommand);
      model.put("command", svnCommand); // This is for the form to work
      model.put("url", configuration.getUrl());
      model.put("numrevision", (revision == HEAD ? Long.toString(repository.getLatestRevision()) : null));

      // It's ok for svnHandle to return null in cases like GetController.
      if (modelAndView != null) {
        modelAndView.addAllObjects(model);
      }
      return modelAndView;
    } catch (SVNAuthenticationException svnae) {
      return forwardToAuthenticationFailureView(request, svnCommand);
    } catch (SVNException e) {
      logger.error("SVN Exception", e);
      Throwable cause = e.getCause();
      if (cause instanceof java.net.NoRouteToHostException || cause instanceof ConnectException) {
        exception.reject("error.message.no-route-to-host");
      } else {
        exception.reject(null, e.getMessage());
      }

      return prepareExceptionModelAndView(exception, svnCommand);
    }

  }

  /**
   * Prepare authentication model. This setus up a model and redirect view with
   * all stuff needed to redirect control to the login page.
   * 
   * @param request Servlet request, original command and url info will be
   *          stored here during authentication.
   * @param svnCommand Command object.
   * @return Redirect view for logging in, with original request info stored in
   *         session to enable the authentication control to proceed with
   *         original request once the user is authenticated.
   */
  private ModelAndView forwardToAuthenticationFailureView(HttpServletRequest request, SVNBaseCommand svnCommand) {
    logger.debug("Authentication failed, forwarding to 'authenticationfailure' view");
    return new ModelAndView("authenticationfailure");
  }

  /**
   * Prepare the exception model. This sets up a model and view with all stuff
   * needed to for displaying a useful error mesage.
   * 
   * @param exception Bind exception from Spring MVC validation.
   * @param svnCommand Command object.
   * @param credentials Credentials, may be <code>null</code> if the user is
   *          not authenticated.
   * @return The packaged model and view.
   */
  @SuppressWarnings("unchecked")
  private ModelAndView prepareExceptionModelAndView(final BindException exception, final SVNBaseCommand svnCommand) {
    final Map<String, Object> model = exception.getModel();
    logger.debug("'command' set to: " + svnCommand);
    model.put("command", svnCommand);
    model.put("url", configuration.getUrl());
    model.put("numrevision", null);
    return new ModelAndView("goto", model);
  }

  /**
   * Assigns an <code>AuthenticationManager</code> instance to the given
   * repository with credentials from the given <code>configuration</code> instance. 
   * If a user id is not configured instance (is <code>null</code>) in the 
   * <code>configuration</code> , no <code>AuthenticationManager</code> will be set.
   * 
   * @param repository Repository object.
   */
  private void assignCredentials(final SVNRepository repository) {
    if (configuration.getConfiguredUID() != null) {
      ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(new File(configuration
          .getSVNConfigurationPath()), configuration.getConfiguredUID(), configuration.getConfiguredPWD(), false);
      repository.setAuthenticationManager(authManager);
    }
  }

  /**
   * Converts the revision <code>String</code> to a format suitable for SVN,
   * also handles special logical revision HEAD. <code>null</code> and empty
   * string revision are converted to the HEAD revision.
   * <p>
   * The given <code>SVNBaseCommand</code> instance will be updated with key
   * word HEAD, if revision was <code>null</code> or empty <code>String</code>.
   * <p>
   * TODO: This (could perhaps) be a suitable place to also handle conversion of
   * date to revision to expand possilbe user input to handle calendar
   * intervalls.
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
   *          if applicable.
   * @param svnCommand Command (basically request parameters submitted in user
   *          request)
   * @param revision SVN type revision.
   * @param request Servlet request.
   * @param response Servlet response.
   * @return Model and view to render.
   * @throws SVNException Thrown if exception occurs during SVN operations.
   */
  protected abstract ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
      HttpServletRequest request, HttpServletResponse response) throws SVNException;
}