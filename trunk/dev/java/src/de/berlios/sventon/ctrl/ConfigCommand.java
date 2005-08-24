package de.berlios.sventon.ctrl;

/**
 * ConfigCommand.
 * <p>
 * Command class used to bind and pass servlet parameter arguments for sventon configuration.
 *
 * @author jesper@users.berlios.de
 */
public class ConfigCommand {

  private String repositoryURL;
  private String username;
  private String password;

  public String getRepositoryURL() {
    return repositoryURL;
  }

  public void setRepositoryURL(String repositoryURL) {
    this.repositoryURL = repositoryURL;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
