package de.berlios.sventon.ctrl;

import de.berlios.sventon.command.ConfigCommand;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

  public static final String SVENTON_PROPERTIES = "WEB-INF/classes/default-sventon.properties";

  public static final String PROPERTY_KEY_REPOSITORY_URL = "svn.root";

  public static final String PROPERTY_KEY_USERNAME = "svn.uid";

  public static final String PROPERTY_KEY_PASSWORD = "svn.pwd";

  public static final String PROPERTY_KEY_CONFIGPATH = "svn.configpath";

  public static final String PROPERTY_KEY_MOUNTPOINT = "svn.mountpoint";

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

  protected ModelAndView showForm(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  BindException e) throws Exception {
    logger.debug("showForm() started");
    logger.info("sventon configuration ok: " + configuration.isConfigured());
    if (configuration.isConfigured()) {
      // sventon already configured - return to browser view.
      logger.debug("Already configured - returning to browser view.");
      return new ModelAndView(new RedirectView("repobrowser.svn"));
    } else {
      Map<String, Object> model = new HashMap<String, Object>();
      ConfigCommand configCommand = new ConfigCommand();
      File currentDir = new File(".");
      logger.debug("currentDir is: " + currentDir.getCanonicalPath());
      configCommand.setCurrentDir(currentDir.getCanonicalPath());
      logger.debug("'command' set to: " + configCommand);
      model.put("command", configCommand); // This is for the form to work
      logger.debug("Displaying the config page.");
      return new ModelAndView("config", model);
    }
  }

  protected ModelAndView processFormSubmission(HttpServletRequest httpServletRequest,
                                               HttpServletResponse httpServletResponse, Object command, BindException exception) throws Exception {
    logger.debug("processFormSubmission() started");
    logger.info("sventon configuration ok: " + configuration.isConfigured());
    if (configuration.isConfigured()) {
      // sventon already configured - return to browser view.
      logger.debug("Already configured - returning to browser view.");
      return new ModelAndView(new RedirectView("repobrowser.svn"));
    } else {
      if (exception.hasErrors()) {
        return new ModelAndView("config", exception.getModel());
      }

      //TODO: validate entered url by opening a connection.

      ConfigCommand confCommand = (ConfigCommand) command;
      Properties config = new Properties();
      config.put(PROPERTY_KEY_REPOSITORY_URL, confCommand.getRepositoryURL());
      config.put(PROPERTY_KEY_USERNAME, confCommand.getUsername());
      config.put(PROPERTY_KEY_PASSWORD, confCommand.getPassword());
      config.put(PROPERTY_KEY_CONFIGPATH, confCommand.getConfigPath());
      config.put(PROPERTY_KEY_MOUNTPOINT, confCommand.getMountPoint());
      logger.debug(config.toString());
      File propFile = new File(System.getProperty("sventon.root") + SVENTON_PROPERTIES);
      logger.debug("Storing configuration properties in: " + propFile.getAbsolutePath());

      config.store(new FileOutputStream(propFile), createPropertyFileComment());

      //TODO: Find a way to refresh the app context, the lines below fails for some reason
      //(also, are not threadsafe) 
//      AbstractApplicationContext abstractApplicationContext = ((AbstractApplicationContext) getWebApplicationContext());
//      logger.debug("Refreshing application context");
//      abstractApplicationContext.refresh();

      return new ModelAndView("restart");
    }
  }

  /**
   * Creates the property file comment.
   * 
   * @return The comments.
   */
  private String createPropertyFileComment() {
    StringBuilder comments = new StringBuilder();
    comments.append("###############################################################################\n");
    comments.append("# sventon configuration file - http://sventon.berlios.de                       #\n");
    comments.append("################################################################################\n\n");
    comments.append("################################################################################\n");
    comments.append("# Key: svn.root                                                                #\n");
    comments.append("#                                                                              #\n");
    comments.append("# Description:                                                                 #\n");
    comments.append("# SVN root URL, this is the repository that will be browsable with this        #\n");
    comments.append("# sventon instance.                                                            #\n");
    comments.append("#                                                                              #\n");
    comments.append("# Example:                                                                     #\n");
    comments.append("#   svn.root=svn://svn.berlios.de/sventon/                                     #\n");
    comments.append("#   svn.root=http://domain.com/project/                                        #\n");
    comments.append("#   svn.root=svn+ssh://domain.com/project/                                     #\n");
    comments.append("################################################################################\n\n");
    comments.append("################################################################################\n");
    comments.append("# Key: svn.mountpoint                                                          #\n");
    comments.append("#                                                                              #\n");
    comments.append("# Description:                                                                 #\n");
    comments.append("# Optionally set a mount point to restrict repository browsing to a specific   #\n");
    comments.append("# part of the repository. E.g. by setting the mount point to '/trunk/doc' only #\n");
    comments.append("# the 'doc' directory and its subdirectories will be browsable. This must be   #\n");
    comments.append("# an absolute path from the repository root.                                   #\n");
    comments.append("#                                                                              #\n");
    comments.append("# Example:                                                                     #\n");
    comments.append("#   svn.mountpoint=/trunk/doc                                                  #\n");
    comments.append("################################################################################\n\n");
    comments.append("################################################################################\n");
    comments.append("# Key: svn.configpath                                                          #\n");
    comments.append("#                                                                              #\n");
    comments.append("# Description:                                                                 #\n");
    comments.append("# Path to the the SVN configuration directory, the user running the sventon    #\n");
    comments.append("# web container must have read/write access to this directory.                 #\n");
    comments.append("################################################################################\n\n");
    comments.append("################################################################################\n");
    comments.append("# Key: svn.uid                                                                 #\n");
    comments.append("# Key: svn.pwd                                                                 #\n");
    comments.append("#                                                                              #\n");
    comments.append("# Description:                                                                 #\n");
    comments.append("# Subversion user and password to use for repository access. If not set, the   #\n");
    comments.append("# individual sventon web user will be promted for user ID and password.        #\n");
    comments.append("# The latter approach brings several security issues, assigning a dedicated    #\n");
    comments.append("# Subversion user for sventon repository browsing is the preferred approach.   #\n");
    comments.append("################################################################################\n\n");
    return comments.toString();
  }
}
