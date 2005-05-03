package nu.snart.os.sventon.ctrl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public abstract class SVNServletBaseController extends AbstractCommandController {

  protected RepositoryConfiguration configuration = null;
  /** Logger for this class and subclasses */
  protected final Log logger = LogFactory.getLog(getClass());

  public void setRepositoryConfiguration(final RepositoryConfiguration configuration) {
    this.configuration = configuration;
  }

  public RepositoryConfiguration getRepository() {
    return configuration;
  }
  
  protected SVNServletBaseController() {
    setCommandClass(SVNBaseCommand.class);
  }

}
