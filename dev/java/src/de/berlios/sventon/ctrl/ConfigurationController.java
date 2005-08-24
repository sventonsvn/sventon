package de.berlios.sventon.ctrl;

import org.springframework.web.servlet.mvc.AbstractFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.validation.BindException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller handling the initial sventon configuration.
 * <p>
 *
 * @author jesper@users.berlios.de
 */
public class ConfigurationController extends AbstractFormController {

  /** Repository configuration. */
  private RepositoryConfiguration configuration;

  /** Logger for this class and subclasses. */
  private final Log logger = LogFactory.getLog(getClass());

  protected ConfigurationController() {
    // TODO: Move to XML-file?
    setCommandClass(ConfigCommand.class);
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

  protected ModelAndView showForm(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BindException e) throws Exception {
    logger.debug("showForm started");
    logger.info("sventon configuration ok: " + configuration.isConfigured());
    if (configuration.isConfigured()) {
      // sventon already configured - return to browser view.
      logger.debug("Already configured - returning to browser view.");
      return new ModelAndView(new RedirectView("repobrowser.svn"));
    } else {
      logger.debug("Displaying the config page.");
      return new ModelAndView("config");
    }
  }

  protected ModelAndView processFormSubmission(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, BindException e) throws Exception {
    logger.debug("processFormSubmission started");
    logger.info("sventon configuration ok: " + configuration.isConfigured());
    if (configuration.isConfigured()) {
      // sventon already configured - return to browser view.
      logger.debug("Already configured - returning to browser view.");
      return new ModelAndView(new RedirectView("repobrowser.svn"));
    } else {
      logger.debug("Storing configuration properties.");
      return new ModelAndView(new RedirectView("repobrowser.svn"));
    }
  }
}
