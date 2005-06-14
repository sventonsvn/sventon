package de.berlios.sventon.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.springframework.web.servlet.view.RedirectView;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.io.SVNAuthenticationException;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNSimpleCredentialsProvider;

/**
 * Abstract base class for use by controllers whishing to make use of basic
 * plumbing functionalit such as authorization and basic repository
 * configuration.
 * <p>
 * This abstract controller is based on the GoF Template pattern, the method to
 * implement for extending controllers is
 * <code>{@link #svnHandle(SVNRepository, SVNBaseCommand, long, HttpServletRequest, HttpServletResponse)}</code>.
 * <p>
 * Workflow for this controller:
 * <ol>
 * <li>The controller inspects the user HttpSession to see if it contains a
 * {@link de.berlios.sventon.ctrl.Credentials} object named
 * <code>sventon.credentials</code>. If this object exists information
 * contained in it will be used for authorized repository access, if it does not
 * exist the controller will try to set up the repository with anonymous access.
 * <li>The controller configures the <code>SVNRepository</code> object and
 * calls the extending class'
 * {@link #svnHandle(SVNRepository, SVNBaseCommand, long, HttpServletRequest, HttpServletResponse)}
 * method with the given {@link de.berlios.sventon.ctrl.SVNBaseCommand}
 * containing request parameters.
 * <li>After the call returns, the controller adds additional information to
 * the the model (see below) and forwards the request to the view returned
 * together with the model by the
 * {@link #svnHandle(SVNRepository, SVNBaseCommand, long, HttpServletRequest, HttpServletResponse)}
 * method.
 * </ol>
 * 
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
 * <td>revision</td>
 * <td>SVN revision this request concerns, either an actual revision number or
 * <code>HEAD</code></td>
 * </tr>
 * <tr>
 * <td>path</td>
 * <td>SVN path this request concerns</td>
 * </tr>
 * <tr>
 * <td>target</td>
 * <td>target part of the complete path, see
 * {@link de.berlios.sventon.ctrl.SVNBaseCommand#getTarget()}</td>
 * </tr>
 * <tr>
 * <td>pathPart</td>
 * <td>path part of the complete path, see
 * {@link de.berlios.sventon.ctrl.SVNBaseCommand#getPathPart()}</td>
 * </tr>
 * </table> <p/>
 * 
 * <b>Input arguments</b><br>
 * Input to this argument is wrapped in a
 * <code>{@link de.berlios.sventon.ctrl.SVNBaseCommand}</code> object by the
 * Spring framework. If the extending controller is configured in the Spring
 * config file with a validator for the <code>SVNBaseCommand</code> it will be
 * checked for binding errors. If binding errors were detected the SVN path and
 * revision will be reset to <code>root</code> (/) and <code>HEAD</code>
 * respectively.
 * 
 * <b>Exception handling</b>
 * <dl>
 * <dt>Authentication exception
 * <dd>If a SVN authentication exception occurs during the call the command
 * paramters and the original request URL is stored in the session (using keys
 * <code>sventon.command</code> and <code>sventon.url</code>), a redirect
 * to the authentication page is made. The authentication page will redirect
 * back to this page after credentials have been collected from the user.
 * <dt>Other SVN exceptions
 * <dd>Other SVN exceptons are currently forwarded to a generic error handlng
 * page.
 * </dl>
 * 
 * @author patrikfr@users.berlios.de
 * 
 */
public abstract class AbstractSVNTemplateController extends AbstractFormController {

  protected RepositoryConfiguration configuration = null;

  /** Logger for this class and subclasses */
  protected final Log logger = LogFactory.getLog(getClass());

  protected AbstractSVNTemplateController() {
    // TODO: Move to XML-file?
    setCommandClass(SVNBaseCommand.class);
    setBindOnNewForm(true);
    setSessionForm(false);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.mvc.AbstractFormController#processFormSubmission(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse, java.lang.Object,
   *      org.springframework.validation.BindException)
   */
  @Override
  protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response,
      Object command, BindException exception) throws Exception {
    SVNBaseCommand svnCommand = (SVNBaseCommand) command;

    logger.debug("Getting SVN repository");
    HttpSession session = request.getSession(true);
    Credentials credentials = (Credentials) session.getAttribute("sventon.credentials");
    SVNRepository repository = SVNRepositoryFactory.create(configuration.getLocation());
    if (credentials != null) {
      logger.debug("Credentials found, configureing repository with: " + credentials);
      SVNSimpleCredentialsProvider provider = new SVNSimpleCredentialsProvider(credentials.getUid(), credentials
          .getPwd());
      repository.setCredentialsProvider(provider);
      logger.debug("Setting credentials");
    }

    long revision = ISVNWorkspace.HEAD;
    // TODO: Parsing of revision must be stricter
    if (svnCommand.getRevision() != null && !"HEAD".equals(svnCommand.getRevision())) {
      revision = Long.parseLong(svnCommand.getRevision());
    }

    String redirectUrl = null;

    try {

      SVNNodeKind kind = repository.checkPath(svnCommand.getPath(), revision);

      logger.debug("Node kind: " + kind);

      // TODO: Handle invalid path/revision combo
      if (kind == SVNNodeKind.DIR) {
        redirectUrl = "repobrowser.svn";
      } else if (kind == SVNNodeKind.FILE) {
        redirectUrl = "showfile.svn";
      } else {
        exception.rejectValue("path", "goto.command.invalidpath", "Invalid path");
        // Fill in some common info
        // TODO: Perhaps add entire command object instead of the stringified
        // version?s
        Map model = exception.getModel();
        logger.info("'command' set to: " + svnCommand);
        model.put("command", svnCommand); // This is for the form to work
        model.put("url", configuration.getUrl());
        model.put("revision", svnCommand.getRevision());
        model.put("path", svnCommand.getPath());
        model.put("target", svnCommand.getTarget());
        model.put("pathPart", svnCommand.getPathPart());
        fillInCredentials(credentials, model);
        return new ModelAndView("goto", model);
      }
    } catch (SVNAuthenticationException svnae) {
      return handleAuthenticationRedirect(request, svnCommand);
    }

    logger.debug("Submitted command: " + svnCommand);
    logger.debug("Redirecting to: " + redirectUrl);

    Map<String, Object> m = new HashMap<String, Object>();
    m.put("path", svnCommand.getPath());
    m.put("revision", svnCommand.getRevision());
    return new ModelAndView(new RedirectView(redirectUrl), m);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.mvc.AbstractFormController#showForm(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse,
   *      org.springframework.validation.BindException)
   */
  @Override
  protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException exception)
      throws Exception {
    // This is for preparing the requested model and view and also rendering the
    // "Go To" form.

    return handle(request, response, exception.getTarget(), exception);
  }

  /**
   * {@inheritDoc}
   */
  public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command,
      BindException exception) throws ServletException, IOException {

    SVNBaseCommand svnCommand = (SVNBaseCommand) command;

    // Move error handling, this should not be OK!
    if (exception.hasErrors()) {
      svnCommand.setPath(null);
      svnCommand.setRevision("HEAD");
    }

    svnCommand.setRevision(svnCommand.getRevision() == null ? "HEAD" : svnCommand.getRevision());

    long revision = ISVNWorkspace.HEAD;

    try {
      logger.debug("Getting SVN repository");
      HttpSession session = request.getSession(true);
      Credentials credentials = (Credentials) session.getAttribute("sventon.credentials");
      SVNRepository repository = SVNRepositoryFactory.create(configuration.getLocation());
      if (credentials != null) {
        SVNSimpleCredentialsProvider provider = new SVNSimpleCredentialsProvider(credentials.getUid(), credentials
            .getPwd());
        repository.setCredentialsProvider(provider);
        logger.debug("Setting credentials");
      }

      // TODO: Parsing of revision must be stricter
      if (svnCommand.getRevision() != null && !"HEAD".equals(svnCommand.getRevision())) {
        revision = Long.parseLong(svnCommand.getRevision());
      }

      final ModelAndView modelAndView = svnHandle(repository, svnCommand, revision, request, response);

      // Fill in some common info
      // TODO: Perhaps add entire command object instead of the stringified
      // version?s
      Map<String, Object> model = new HashMap<String, Object>();
      logger.info("'command' set to: " + svnCommand);
      model.put("command", svnCommand); // This is for the form to work
      model.put("url", configuration.getUrl());
      model.put("revision", svnCommand.getRevision());
      model.put("path", svnCommand.getPath());
      model.put("target", svnCommand.getTarget());
      model.put("pathPart", svnCommand.getPathPart());
      fillInCredentials(credentials, model);
      modelAndView.addAllObjects(model);
      return modelAndView;
    } catch (SVNAuthenticationException svnae) {
      return handleAuthenticationRedirect(request, svnCommand);
    } catch (SVNException e) {
      logger.error("SVN Exception", e);
      Throwable cause = e.getCause();
      if (cause instanceof java.net.NoRouteToHostException || cause instanceof ConnectException) {
        exception.reject("error.message.no-route-to-host");
      } else {
        exception.reject(null, e.getMessage());
      }
      return new ModelAndView("goto", exception.getModel());
    }

  }

  public void setRepositoryConfiguration(final RepositoryConfiguration configuration) {
    this.configuration = configuration;
  }

  public RepositoryConfiguration getRepository() {
    return configuration;
  }

  protected ModelAndView handleAuthenticationRedirect(HttpServletRequest request, SVNBaseCommand svnCommand) {
    logger.debug("Authentication failed, redirecting to 'authenticate' view");
    Map<String, String> m = new HashMap<String, String>();
    m.put("path", svnCommand.getPath());
    m.put("revision", svnCommand.getRevision());
    HttpSession session = request.getSession(true);
    session.setAttribute("sventon.command", m);
    session.setAttribute("sventon.url", request.getRequestURL());
    return new ModelAndView(new RedirectView("authenticate.svn"));
  }

  protected void fillInCredentials(Credentials credentials, Map<String, Object> model) {
    if (credentials != null)
      model.put("uid", credentials.getUid());
  }

  protected abstract ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, long revision,
      HttpServletRequest request, HttpServletResponse response) throws SVNException;
}
