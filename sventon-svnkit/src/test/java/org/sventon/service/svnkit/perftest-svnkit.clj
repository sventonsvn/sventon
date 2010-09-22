(ns org.sventon.service.svnkit.perftest-svnkit
  (:require [org.sventon.service.perftest])
  (:import [org.sventon.service RepositoryService]
    [org.sventon SVNConnection]
    [org.sventon.service.svnkit SVNKitConnection SVNKitRepositoryService]
    [org.sventon.export ExportDirectoryImpl]
    [org.sventon.model PathRevision Revision RepositoryName]
    [org.sventon.colorer JHighlightColorer]
    [org.sventon.web.command DiffCommand]
    [org.tmatesoft.svn.core SVNURL]
    [org.tmatesoft.svn.core.internal.io.dav DAVRepositoryFactory]
    [org.tmatesoft.svn.core.internal.io.fs FSRepositoryFactory]
    [org.tmatesoft.svn.core.internal.io.svn SVNRepositoryFactoryImpl]
    [org.tmatesoft.svn.core.io SVNRepository SVNRepositoryFactory]
    [java.io OutputStream File]
    [java.nio.charset Charset]
    [org.mockito Mockito])
  )

(defn create-connection [url]
  (do (SVNRepositoryFactoryImpl/setup)
    (DAVRepositoryFactory/setup)
    (FSRepositoryFactory/setup)
    (SVNKitConnection. (SVNRepositoryFactory/create (SVNURL/parseURIDecoded url)))))

(defn create-service []
  (SVNKitRepositoryService.))

(defn run-svnkit-test [n]
  (binding [org.sventon.service.perftest/create-service create-service
            org.sventon.service.perftest/create-connection create-connection]
    (org.sventon.service.perftest/run-tests n)))

(run-svnkit-test 1)