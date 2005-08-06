package de.berlios.sventon.ctrl;

import java.io.Serializable;

/**
 * Wrapper class to hold credentials data.
 * <p>
 * This class is <code>java.io.Serializable</code> and is intended to be
 * stored in the HTTP session.
 * <p>
 * Note that storing credentials like this this can be security issue.
 * 
 * @author patrikfr@users.berlios.de
 */
public class Credentials implements Serializable {

  private static final long serialVersionUID = -625238617578609818L;

  /** Password. */
  private String pwd = null;

  /** The user identification. */
  private String uid = null;

  /**
   * @return Returns the password.
   */
  public final String getPwd() {
    return pwd;
  }

  /**
   * @param pwd The password to set.
   */
  public final void setPwd(final String pwd) {
    this.pwd = pwd;
  }

  /**
   * @return Returns the user id.
   */
  public final String getUid() {
    return uid;
  }

  /**
   * @param uid The user id to set.
   */
  public final void setUid(final String uid) {
    this.uid = uid;
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return "Credential{" + "uid=" + uid + ", pwd=(nope, will not tell)";
  }
}
