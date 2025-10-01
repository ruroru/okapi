(ns leiningen.new.okapi-test
  (:require [babashka.fs :as fs]
            [clojure.test :refer [deftest is]]
            [leiningen.new.okapi :as okapi]))


(defn- verify-files-exist [directory expected-files]
  (doseq [file expected-files]
    (is (fs/exists? (format "%s/%s" directory file)))))


(deftest test-creation
  (okapi/okapi "mock")
  (verify-files-exist "./mock" (list
                                   ".gitignore"
                                   "project.clj"
                                   "README.md"
                                   "renovate.json"))

  (verify-files-exist "./mock/.github/workflows" (list
                                                     "release-major.yaml"
                                                     "release-minor.yaml"
                                                     "release-patch.yaml"
                                                     "release-snapshot.yaml"
                                                     "test.yaml"
                                                     ))
  (verify-files-exist "./mock/src/mock" (list "core.clj"))
  (verify-files-exist "./mock/test/mock" (list "core_test.clj")))
