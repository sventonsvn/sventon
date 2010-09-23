(ns org.sventon.service.javahl
  (:require [org.sventon.service.perftest])
  (:import [org.sventon.service RepositoryService]
    [org.sventon SVNConnection]
    [org.sventon.service.javahl JavaHLConnection JavaHLRepositoryService]
    [org.sventon.export ExportDirectoryImpl]
    [org.sventon.model PathRevision Revision SVNURL RepositoryName]
    [org.sventon.colorer JHighlightColorer]
    [org.sventon.web.command DiffCommand]
    [org.tigris.subversion.javahl SVNClient]
    [java.io OutputStream File]
    [java.nio.charset Charset]
    [org.mockito Mockito])
  )

(defn create-connection [url]
  (let [client (SVNClient.)
        svn-url (SVNURL/parse url)
        credentials nil]
    (JavaHLConnection. client svn-url credentials)))

(defn create-service []
  (JavaHLRepositoryService.))

(defn run-svnkit-test [n]
  (binding [org.sventon.service.perftest/create-service create-service
            org.sventon.service.perftest/create-connection create-connection]
    (org.sventon.service.perftest/run-tests n)))

(run-svnkit-test 1)


