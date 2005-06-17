package de.berlios.sventon.ctrl;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Simple controller for retrieving credentials from the user and store these in the session.
 * <p>
 * Credentials as entered by a user form POST is stored as {@link de.berlios.sventon.ctrl.Credentials} object
 * in the session using the key <code>sventon.credentials</code>. Redirect information stored by the forwarder
 * in the session (using keys <code>sventon.url</code> and <code>sventon.model</code>) are used for redirecting back
 * to the forwarder. URL and model info is purged from the session. See 
 * {@link de.berlios.sventon.ctrl.AbstractSVNTemplateController} for more specific workflow.
 * <p>
 * Note, it may be considered unsafe to store the passwords unencrypted in the session. Session data may be 
 * written to disk as deemed necessary by the container, also, many containers provide tools for inspecting the session
 * contents, this may expose entered uid/pwd data. This should be documented in the user manuals (equiv).
 *  
 * @author patrikfr@users.berlios.de
 * @see de.berlios.sventon.ctrl.Credentials
 * @see de.berlios.sventon.ctrl.CredentialsValidator
 *
 */
public class CredentialsController extends SimpleFormController {
  /** Logger for this class and subclasses */
  protected final Log logger = LogFactory.getLog(getClass());

  public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
      BindException errors) throws ServletException {

    Credentials credentials = (Credentials) command;
    logger.debug("Retrieved credentials: " + credentials);

    HttpSession session = request.getSession(true);
    session.setAttribute("sventon.credentials", credentials);
    
    final StringBuffer url = (StringBuffer) session.getAttribute("sventon.url");
    final Map model = (Map) session.getAttribute("sventon.model");
    session.removeAttribute("sventon.url");
    session.removeAttribute("sventon.model");
    //TODO: url may be null!
    return new ModelAndView(new RedirectView(url.toString()),model);

  }
}
