/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

/**
 * LogMessage.
 *
 * @author jesper@sventon.org
 */
public final class LogMessage {

  private final long revision;
  private final String message;

  /**
   * Constructor.
   *
   * @param revision The revision
   * @param message  The log message
   */
  public LogMessage(final long revision, final String message) {
    this.revision = revision;
    this.message = message;
  }

  /**
   * Gets the revision for this log message.
   *
   * @return The revision
   */
  public long getRevision() {
    return revision;
  }

  /**
   * Gets the log message.
   *
   * @return The message.
   */
  public String getMessage() {
    return message;
  }

}
