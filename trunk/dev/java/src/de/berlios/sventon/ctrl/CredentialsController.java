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
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class CredentialsController extends SimpleFormController {
  /** Logger for this class and subclasses */
  protected final Log logger = LogFactory.getLog(getClass());

  private SimpleUrlHandlerMapping urlMapping;

  public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
      BindException errors) throws ServletException {

    Credentials credentials = (Credentials) command;
    logger.debug("Retrieved credentials: " + credentials);

    HttpSession session = request.getSession(true);
    session.setAttribute("credentials", credentials);
    
    final StringBuffer url = (StringBuffer) session.getAttribute("url");
    final Map model = (Map) session.getAttribute("model");
    session.removeAttribute("url");
    session.removeAttribute("model");
    return new ModelAndView(new RedirectView(url.toString()),model);

  }

  public void setUrlMapping(SimpleUrlHandlerMapping urlMapping) {
    this.urlMapping = urlMapping;
  }
}
