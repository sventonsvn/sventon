(ns org.sventon.service.perftest
  (:import [org.sventon.service RepositoryService]
    [org.sventon SVNConnection]
    [org.sventon.export ExportDirectoryImpl]
    [org.sventon.model PathRevision Revision RepositoryName]
    [org.sventon.colorer JHighlightColorer]
    [org.sventon.web.command DiffCommand]
    [java.io OutputStream File]
    [java.nio.charset Charset]
    [org.mockito Mockito])
  )

(def create-connection)

(def create-service)

(defmacro repo-service-exec [method url & args]
  `(let [connection# (create-connection ~url)
         service# (create-service)]
    (. service# ~method connection# ~@args)))

(defn get-latest-revision [url]
  (repo-service-exec getLatestRevision url))

(defn get-log-entry [url rev]
  (let [connection (create-connection url)
        service (create-service)]
    (.getLogEntry service nil connection rev)))

(defn get-log-entries-from-repository-root [url from-rev to-rev]
  (repo-service-exec getLogEntriesFromRepositoryRoot url from-rev to-rev))

(defn get-log-entries
  [url from-rev to-rev path limit stop-on-copy? include-changed-paths?]
  (let [connection (create-connection url)
        service (create-service)]
    (.getLogEntries service nil connection from-rev to-rev path limit stop-on-copy? include-changed-paths?)))

(defn export [url targets peg-revision export-dir]
  (repo-service-exec export url targets peg-revision export-dir))

(defn get-file-contents [url path revision output]
  (repo-service-exec getFileContents url path revision output))

(defn list-properties [url path revision]
  (repo-service-exec listProperties url path revision))

(defn get-node-kind [url path revision]
  (repo-service-exec getNodeKind url path revision))

(defn get-locks [url start-path recursive?]
  (repo-service-exec getLocks url start-path recursive?))

(defn list-dir [url path revision]
  (repo-service-exec list url path revision))

(defn get-entry-info [url path revision]
  (repo-service-exec getEntryInfo url path revision))

(defn get-file-revisions [url path revision]
  (repo-service-exec getFileRevisons url path revision))

(defn diff-sideby-side [url diff-command peg-rev charset]
  (repo-service-exec diffSideBySide url diff-command peg-rev charset))

(defn diff-unified [url diff-command peg-rev charset]
  (repo-service-exec diffUnified url diff-command peg-rev charset))

(defn diff-inline [url diff-command peg-rev charset]
  (repo-service-exec diffInline url diff-command peg-rev charset))

(defn diff-paths [url diff-command]
  (repo-service-exec diffPaths url diff-command))

(defn blame [url path revision charset colorer]
  (repo-service-exec blame url path revision charset colorer))

(defn get-node-kind-for-diff [url diff-command]
  (repo-service-exec getNodeKindForDiff url diff-command))

(defn translate-revision [url revision head-revision]
  (repo-service-exec translateRevision url revision head-revision))

(defn get-latest-revisions [url revision-count]
  (repo-service-exec getLatestRevision url revision-count))

(defn get-revisions-for-path [url path from-rev to-rev stop-on-copy? limit]
  (repo-service-exec url path from-rev to-rev stop-on-copy? limit))


(defn time-method [n m]
  (time (dotimes [_ n] (m))))

(def root "http://localhost/repos/svn/sventon-repo-dump/")

(defn run-tests [n]
  (do
    (println "Running tests agains:" root "with" n "samples")

    (println "getLatestRevision")
    (time-method n #(get-latest-revision root))

    (println "getLogEntry")
    (time-method n #(get-log-entry root 1800))

    (println "getLogEntriesFromRepositoryRoot")
    (time-method n #(get-log-entries-from-repository-root root 1000 1800))

    (println "getLogEntries")
    (time-method n #(get-log-entries root 1500 1800 "/trunk/sventon/src/main/java/org/sventon", 1000, false true))

    (println "export")
    (let [paths [(PathRevision. "/trunk/lib" (Revision/parse "1800")),
                 (PathRevision. "/trunk/licenses" (Revision/parse "1800")),
                 (PathRevision. "/trunk/sventon" (Revision/parse "1800"))]
          export-dir (ExportDirectoryImpl.
        (RepositoryName. "test")
        (File. ".")
        (Charset/defaultCharset))]
      (time-method n #(export root paths 1800 export-dir)))

    (println "getFileContents")
    (time-method n
      #(get-file-contents
        root
        "/trunk/sventon/src/main/java/org/sventon/AuthenticationException.java"
        1800
        (Mockito/mock OutputStream)))

    (println "listProperties")
    (time-method n
      #(list-properties
        root
        "/trunk/sventon/src/main/java/org/sventon/AuthenticationException.java"
        1800))

    (println "getNodeKind")
    (time-method n
      #(get-node-kind
        root
        "/trunk/sventon/src/main/java/org/sventon/AuthenticationException.java"
        1800))

    (println "getLocks")
    (time-method n
      #(get-locks root "/trunk" true))

    (println "list")
    (time-method n
      #(list-dir root "/trunk" 1800))

    (println "getEntryInfo")
    (time-method n
      #(get-entry-info
        root
        "/trunk/sventon/src/main/java/org/sventon/AuthenticationException.java"
        1800))

    (println "getFileRevisions")
    (time-method n
      #(get-file-revisions
        root
        "/trunk/sventon/src/main/java/org/sventon/AuthenticationException.java"
        1800))


    (let [command
          (doto (DiffCommand.)
            (.setEntries (into-array [
              (PathRevision.
                "/trunk/sventon-core/src/main/java/org/sventon/AuthenticationException.java"
                (Revision/parse "1791")),
              (PathRevision.
                "/branches/features/svn_facade/sventon/src/main/java/org/sventon/SVNAuthenticationException.java"
                (Revision/parse "1698"))])))]

      (println "diffSideBySide")
      (time-method n
        #(diff-sideby-side root command (Revision/parse "1800") "UTF-8"))

      (println "diffUnified")
      (time-method n
        #(diff-unified root command (Revision/parse "1800") "UTF-8"))

      (println "diffInline")
      (time-method n
        #(diff-sideby-side root command (Revision/parse "1800") "UTF-8"))

      (println "diffPaths")
      (time-method n
        #(diff-paths root command))

      (println "getNodeKindForDiff")
      (time-method n
        #(get-node-kind-for-diff root command)))

    (println "blame")
    (time-method n
      #(blame
        root
        "/trunk/sventon/src/main/java/org/sventon/AuthenticationException.java"
        1800
        "UTF-8"
        (JHighlightColorer.)))

    (println "translateRevision")
    (time-method n
      #(translate-revision root (Revision/parse "{2009-08-30}") 1900))

    (println "getLatestRevisions")
    (time-method n
      #(get-latest-revisions root 10))

    (println "getRevisionForPath")
    (time-method n
      #(get-revisions-for-path
        root
        "/trunk/readme.txt"
        1800
        1000
        false
        10))))