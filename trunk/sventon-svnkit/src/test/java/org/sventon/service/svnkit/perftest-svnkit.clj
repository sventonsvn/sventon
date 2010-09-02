(ns org.sventon.service.svnkit.perftest-svnkit
  (:import [org.sventon.service RepositoryService]
    [org.sventon SVNConnection]
    [org.sventon.service.svnkit SVNKitConnection SVNKitRepositoryService]
    [org.sventon.model]
    [org.tmatesoft.svn.core SVNURL]
    [org.tmatesoft.svn.core.internal.io.dav DAVRepositoryFactory]
    [org.tmatesoft.svn.core.internal.io.fs FSRepositoryFactory]
    [org.tmatesoft.svn.core.internal.io.svn SVNRepositoryFactoryImpl]
    [org.tmatesoft.svn.core.io SVNRepository SVNRepositoryFactory])
  )

(defn create-connection [url]
  (do (SVNRepositoryFactoryImpl/setup)
    (DAVRepositoryFactory/setup)
    (FSRepositoryFactory/setup)
    (SVNKitConnection. (SVNRepositoryFactory/create (SVNURL/parseURIDecoded url)))))

(defn create-service []
  (SVNKitRepositoryService.))

(defn get-latest-revision [url]
  (let [connection (create-connection url)
        service (create-service)]
    (.getLatestRevision service connection)))

(defn get-log-entry [url rev]
  (let [connection (create-connection url)
        service (create-service)]
    (.getLogEntry service nil connection rev)))

(defn get-log-entries-from-repository-root [url from-rev to-rev]
  (let [connection (create-connection url)
        service (create-service)]
    (.getLogEntriesFromRepositoryRoot service connection from-rev to-rev)))

(defn time-method [n m]
  (time (dotimes [_ n] (m))))



(defn run-tests [n]
  (do (println "getLatestRevision")
    (time-method n #(get-latest-revision "svn://svn.berlios.de/sventon")))

  (do (println "getLogEntry")
    (time-method n #(get-log-entry "svn://svn.berlios.de/sventon" 1800)))

  (do (println "getLogEntriesFromRepositoryRoot")
    (time-method n #(get-log-entries-from-repository-root "svn://svn.berlios.de/sventon" 1000 1800))))
