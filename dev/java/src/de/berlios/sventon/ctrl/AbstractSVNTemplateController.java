package de.berlios.sventon.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.springframework.web.servlet.view.RedirectView;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.io.SVNAuthenticationException;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNSimpleCredentialsProvider;

public abstract class AbstractSVNTemplateController extends AbstractCommandController {

  protected RepositoryConfiguration configuration = null;

  /** Logger for this class and subclasses */
  protected final Log logger = LogFactory.getLog(getClass());

  public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command,
      BindException exception) throws ServletException, IOException {
    
    SVNBaseCommand svnCommand = (SVNBaseCommand) command;
    
    long revision = ISVNWorkspace.HEAD;
    if (svnCommand.getRevision() != null) {
      revision = Long.parseLong(svnCommand.getRevision());
    }

    try {
      logger.debug("Getting SVN repository");
      HttpSession session = request.getSession(true);
      Credentials credentials = (Credentials) session.getAttribute("credentials");
      SVNRepository repository = SVNRepositoryFactory.create(configuration.getLocation());
      if (credentials != null) {
        SVNSimpleCredentialsProvider provider = new SVNSimpleCredentialsProvider(credentials.getUid(), credentials
            .getPwd());
        repository.setCredentialsProvider(provider);        
        logger.debug("Setting credentials");
      }
       ModelAndView modelAndView = svnHandle(repository, svnCommand, revision, request, response);
       
       //Fill in some common info
       Map model = new HashMap<String, Object>();
       model.put("url", configuration.getUrl());
       model.put("revision", revision == ISVNWorkspace.HEAD ? "HEAD" : Long.toString(revision));
       model.put("path", svnCommand.getPath());
       fillInCredentials(credentials, model);
       modelAndView.addAllObjects(model);
       return modelAndView;
    } catch (SVNAuthenticationException svnae) {
      return handleAuthenticationRedirect(request, svnCommand);
    } catch (SVNException e) {
      logger.error("SVN Exception", e);
      StringWriter writer = new StringWriter();
      e.printStackTrace(new PrintWriter(writer));
      return new ModelAndView("error", "throwable", writer.toString());
    }
  }

  public void setRepositoryConfiguration(final RepositoryConfiguration configuration) {
    this.configuration = configuration;
  }

  public RepositoryConfiguration getRepository() {
    return configuration;
  }

  protected AbstractSVNTemplateController() {
    setCommandClass(SVNBaseCommand.class);
  }

  protected ModelAndView handleAuthenticationRedirect(HttpServletRequest request, SVNBaseCommand svnCommand) {
    logger.debug("Authentication failed, redirecting to 'authenticate' view");
    Map<String, String> m = new HashMap<String, String>();
    m.put("path", svnCommand.getPath());
    m.put("revision", svnCommand.getRevision());
    HttpSession session = request.getSession(true);
    session.setAttribute("command", m);
    session.setAttribute("url", request.getRequestURL());
    return new ModelAndView(new RedirectView("authenticate.svn"));
  }

  protected void fillInCredentials(Credentials credentials, Map<String, Object> model) {
    if (credentials != null)
      model.put("uid", credentials.getUid());
  }
  
  protected abstract ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, long revision, HttpServletRequest request, HttpServletResponse response) throws SVNException;
}
