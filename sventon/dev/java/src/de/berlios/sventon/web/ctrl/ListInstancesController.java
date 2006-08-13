package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.config.ApplicationConfiguration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller that creates a list of all configured repository instance.
 *
 * @author jesper@users.berlios.de
 */
public class ListInstancesController extends AbstractController {

  /**
   * The application configuration. Used to get all instance names.
   */
  private ApplicationConfiguration configuration;

  protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
      throws Exception {

    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("instanceNames", configuration.getInstanceNames());
    return new ModelAndView("listinstances", model);
  }

  /**
   * Sets the application configuration.
   *
   * @param configuration Configuration.
   */
  public void setConfiguration(final ApplicationConfiguration configuration) {
    this.configuration = configuration;
  }

}
