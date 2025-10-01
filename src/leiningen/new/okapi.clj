(ns leiningen.new.okapi
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [leiningen.core.main :as main]
            [hato.client :as hato]
            [clojure.data.json :as json]
            [leiningen.new.templates :refer [->files name-to-path renderer year]])
  (:import (java.io File)))

(def render (renderer "okapi"))

(defn- get-latest-version-from-maven-repository [artifact]
  (let [[group-id artifact-id] (str/split artifact #"/")]
    (-> (hato/get (str "https://search.maven.org/solrsearch/select?q=g:%22"
                       group-id
                       "%22+AND+a:%22"
                       artifact-id
                       "%22&wt=json"))
        (get :body)
        json/read-str
        (get "response")
        (get "docs")
        first
        (get "latestVersion"))))


(defn- get-latest-version-from-clojars [artifact]
  (let [url (format "https://clojars.org/api/artifacts/%s" artifact)]
    (-> (hato/get url {:headers {"Accept" "application/edn"}})
        (get :body)
        edn/read-string
        (get :latest_release))))

(defn- copy-files [project-name file-pairs]
  (doseq [[source-path dest-path] file-pairs]
    (let [resource-path (str "leiningen/new/okapi/" source-path)
          source (io/resource resource-path)
          dest (io/file (str (System/getProperty "user.dir") "/" project-name "/" dest-path))]

      (.mkdirs ^File (.getParentFile ^File dest))

      (do
        (io/make-parents dest)
        (io/copy (io/input-stream source) dest)))))


(defn okapi
  "Generates template"
  [name]
  (let [context {:name                 name
                 :sanitized            (name-to-path name)
                 :year                 (year)
                 :clojure-version      (get-latest-version-from-maven-repository "org.clojure/clojure")
                 :bump-version         (format "\"%s\"" (get-latest-version-from-clojars "org.clojars.jj/bump"))
                 :strict-check-version (format "\"%s\"" (get-latest-version-from-clojars "org.clojars.jj/strict-check"))}]

    (main/info "Generating fresh 'lein new' okapi project.")
    (->files context
             ["src/{{sanitized}}/core.clj" (render "core.clj" context)]
             ["test/{{sanitized}}/core_test.clj" (render "core_test.clj" context)]
             ["project.clj" (render "project.clj" context)]
             [".gitignore" (render "gitignore" context)]
             ["renovate.json" (render "renovate.json" context)]
             ["README.md" (render "README.md" context)])
    (copy-files name
                [["test.yaml" ".github/workflows/test.yaml"]
                 ["release-major.yaml" ".github/workflows/release-major.yaml"]
                 ["release-minor.yaml" ".github/workflows/release-minor.yaml"]
                 ["release-patch.yaml" ".github/workflows/release-patch.yaml"]
                 ["release-snapshot.yaml" ".github/workflows/release-snapshot.yaml"]])))
