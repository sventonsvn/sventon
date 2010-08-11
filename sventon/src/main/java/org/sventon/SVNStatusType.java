/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */

package org.sventon;

/**
 * This enum holds all status operation types. The status types reports from
 * merge, copy and lock operations are not included here.
 */
public enum SVNStatusType {

  /**
   * In a status operation (if it's being running with an option to report
   * of all items set to <span class="javakeyword">true</span>) denotes that the
   * item in the Working Copy being currently processed has no local changes
   * (in a normal state).
   */
  STATUS_NORMAL(1, "normal", ' '),

  /**
   * In a status operation denotes that the item in the Working Copy being
   * currently processed has local modifications.
   */
  STATUS_MODIFIED(2, "modified", 'M'),

  /**
   * In a status operation denotes that the item in the Working Copy being
   * currently processed is scheduled for addition to the repository.
   */
  STATUS_ADDED (3, "added", 'A'),

  /**
   * In a status operation denotes that the item in the Working Copy being
   * currently processed is scheduled for deletion from the repository.
   */
  STATUS_DELETED(4, "deleted", 'D'),

  /**
   * In a status operation denotes that the item in the Working Copy being
   * currently processed is not under version control.
   */
  STATUS_UNVERSIONED(5, "unversioned", '?'),


  /**
   * In a status operation denotes that the item in the Working Copy being
   * currently processed is under version control but is missing  - for example,
   * removed from the filesystem with a non-SVN, non-SVNKit or
   * any other SVN non-compatible delete command).
   */
  STATUS_MISSING(6, "missing", '!'),

  /**
   * In a status operation denotes that the item in the Working Copy being
   * currently processed was replaced by another item with the same name (within
   * a single revision the item was scheduled for deletion and then a new one with
   * the same name was scheduled for addition). Though they may have the same name
   * the items have their own distinct histories.
   */
  STATUS_REPLACED(7, "replaced", 'R'),

  /**
   * In a status operation denotes that the item in the Working Copy being
   * currently processed is in a conflict state (local changes overlap those
   * that came from the repository). The conflicting overlaps need to be manually
   * resolved.
   */
  STATUS_CONFLICTED(9, "conflicted", 'C'),

  /**
   * In a status operation denotes that the item in the Working Copy being
   * currently processed has a non-expected kind. For example, a file is
   * considered to be obstructed if it was deleted (with an SVN client non-compatible
   * delete operation) and a directory with the same name as the file had had was added
   * (but again with an SVN client non-compatible operation).
   */
  STATUS_OBSTRUCTED(10, "obstructed", '~'),

  /**
   * In a status operation denotes that the file item in the Working Copy being
   * currently processed was set to be ignored (was added to svn:ignore property).
   */
  STATUS_IGNORED(11, "ignored", 'I'),


  /**
   * In a status operation denotes that the item in the Working Copy being
   * currently processed is under version control but is somehow incomplete -
   * for example, it may happen when the previous update was interrupted.
   */
  STATUS_INCOMPLETE(12, "incomplete", '!'),

  /**
   * In a status operation denotes that the item in the Working Copy being
   * currently processed is not under version control but is related to
   * externals definitions.
   */
  STATUS_EXTERNAL(13, "external", 'X');


  private final int id;
  private final String name;
  private final char code;


  SVNStatusType(int id, String name, char code) {
    this.id = id;
    this.name = name;
    this.code = code;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public char getCode() {
    return code;
  }

  public static SVNStatusType fromId(final int id){
    for (SVNStatusType statusType : SVNStatusType.values()) {
      if (statusType.getId() == id){
        return statusType;
      }
    }

    throw new IllegalArgumentException("No such status id: " + id);
  }
}
