package de.berlios.sventon.ctrl;

import java.util.HashMap;
import java.util.Map;

public class SVNBaseCommand {
  
  private String path = "";

  private String revision = null;

  /**
   * @return Returns the path.
   */
  public String getPath() {
    return path;
  }

  /**
   * @param path
   *          The path to set.
   */
  public void setPath(String path) {
    if (path != null) {
      this.path = path.trim();
    } else {
      this.path = "";
    }

  }

  /**
   * @return Returns the revision.
   */
  public String getRevision() {
    return revision;
  }

  /**
   * @param revision
   *          The revision to set.
   */
  public void setRevision(String revision) {
    this.revision = revision;
  }

  /**
   * Get target (leaf/end) part of the <code>completePath</code>, it could be
   * a file or a directory.
   * <p>
   * The returned string will have no final "/", even if it is a directory.
   * will be returned.
   * 
   * @return Target part of th epath.
   */
  public String getTarget() {

    String[] splittedString = path.split("/");
    int length = splittedString.length;
    if (length == 0) {
      return "";
    } else {
      return splittedString[splittedString.length - 1];
    }

  }

  /**
   * Get full path, excluding the end/leaf. For complete path including target,
   * see {@link SVNBaseCommand#getCompletePath()}
   * <p>
   * The returned string will have a final "/", if the path info is empty, "" (empty string)
   * will be returned.
   * 
   * @return Path excluding taget (end/leaf)
   */
  public String getPathPart() {
    String work = path;
    if (work.endsWith("/")) {
      work = work.substring(0, work.length() - 1);
    }

    int lastIndex = work.lastIndexOf('/');
    if (lastIndex == -1) {
      return "";
    } else {
      return work.substring(0, lastIndex) + "/";
    }
  }
  
  /**
   * Return the contents of this object as a map model where properties are mapped 
   * to map values.
   * @return The model map.
   */
  public Map<String, Object> asModel() {
    Map<String, Object> m = new HashMap<String, Object>();
    m.put("path", getPath());
    m.put("revision", getRevision());
    return m;
  }
  
  public String toString() {
    return "SVNBaseCommand{path=" + path + ", revision=" + revision + "}";
  }

}
