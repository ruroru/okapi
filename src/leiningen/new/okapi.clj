(ns leiningen.new.okapi
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [leiningen.core.main :as main]
            [hato.client :as hato]
            [clojure.data.json :as json]
            [leiningen.new.templates :refer [->files name-to-path renderer year]]))

(def render (renderer "okapi"))

(defn get-latest-version-from-maven-repository [artifact]
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


(defn okapi
  "Generates template"
  [name]
  (let [data {:name                 name
              :sanitized            (name-to-path name)
              :year                 (year)
              :clojure-version      (get-latest-version-from-maven-repository "org.clojure/clojure")
              :bump-version         (format "\"%s\"" (get-latest-version-from-clojars "org.clojars.jj/bump"))
              :strict-check-version (format "\"%s\"" (get-latest-version-from-clojars "org.clojars.jj/strict-check"))}]

    (main/info "Generating fresh 'lein new' okapi project.")
    (->files data
             [".github/workflows/release-major.yaml" (render "release-major.yaml" data)]
             [".github/workflows/release-minor.yaml" (render "release-minor.yaml" data)]
             [".github/workflows/release-patch.yaml" (render "release-patch.yaml" data)]
             [".github/workflows/release-snapshot.yaml" (render "release-snapshot.yaml" data)]
             [".github/workflows/test.yaml" (render "test.yaml" data)]
             ["src/{{sanitized}}/core.clj" (render "core.clj" data)]
             ["test/{{sanitized}}/core_test.clj" (render "core_test.clj" data)]
             ["project.clj" (render "project.clj" data)]
             [".gitignore" (render "gitignore" data)]
             ["renovate.json" (render "renovate.json" data)]
             ["README.md" (render "README.md" data)])))
