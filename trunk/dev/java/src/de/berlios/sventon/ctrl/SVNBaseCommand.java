package de.berlios.sventon.ctrl;

public class SVNBaseCommand {
  private String path  = "";

  private String revision = null;

  /**
   * @return Returns the path.
   */
  public String getPath() {
    return path;
  }

  /**
   * @param path The path to set.
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * @return Returns the revision.
   */
  public String getRevision() {
    return revision;
  }

  /**
   * @param revision The revision to set.
   */
  public void setRevision(String revision) {
    this.revision = revision;
  }
  
  
}
