/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.service;

import org.sventon.SVNConnection;
import org.sventon.SventonException;
import org.sventon.model.LogEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Common Test Suite intended for use with different SVN providers (like JavaHL and SVNKit) to test that we get the
 * same result from the same method calls to the RepositoryService.
 * <p/>
 * //TODO: Not used because Idea could not find runtime dependencies between sventon-javahl module and sventon-core. Is it even possible?
 */
public class CoreTestSuite {
  public interface TestToolClosure {
    void execute(final RepositoryService service, final SVNConnection connection) throws SventonException;
  }

  private List<TestToolClosure> closures = new ArrayList<TestToolClosure>();

  CoreTestSuite(TestToolClosure... closures) {
    for (TestToolClosure closure : closures) {
      this.closures.add(closure);
    }
  }

  public void run(final RepositoryService service, SVNConnection connection) throws SventonException {
    for (TestToolClosure closure : closures) {
      closure.execute(service, connection);
    }
  }

  public static CoreTestSuite getInstance() {
    return new CoreTestSuite(
        new TestToolClosure() {
          @Override
          public void execute(RepositoryService service, SVNConnection connection) throws SventonException {
            final long latestRevision = service.getLatestRevision(connection);
            System.out.println("\nLatest revision for " + connection.getRepositoryRootUrl().getUrl() + " : " + latestRevision);
          }
        },
        new TestToolClosure() {
          @Override
          public void execute(RepositoryService service, SVNConnection connection) throws SventonException {
            System.out.println("\nLatest Revisions:");
            final List<LogEntry> logEntries = service.getLatestRevisions(connection, null, 2);
            for (LogEntry logEntry : logEntries) {
              System.out.println("logEntry = " + logEntry);
            }
          }
        },
        new TestToolClosure() {
          @Override
          public void execute(RepositoryService service, SVNConnection connection) throws SventonException {
            System.out.println("\nLogEntries from root:");
            final List<LogEntry> logEntries2 = service.getLogEntriesFromRepositoryRoot(connection, 100, 110);
            for (LogEntry logEntry : logEntries2) {
              System.out.println("logEntry = " + logEntry);
            }
          }
        },
        new TestToolClosure() {
          @Override
          public void execute(RepositoryService service, SVNConnection connection) throws SventonException {
            System.out.println("\nLogEntry for single revision:");
            final LogEntry logEntry = service.getLogEntry(connection, null, 1817);
            System.out.println(logEntry);
          }
        }
    );
  }
}