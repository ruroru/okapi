(ns leiningen.new.okapi
  (:require [leiningen.core.main :as main]
            [leiningen.new.templates :refer [->files name-to-path renderer year]]))

(def render (renderer "okapi"))

(defn okapi
  "FIXME: write documentation"
  [name]
  (let [data {:name      name
              :sanitized (name-to-path name)
              :year      (year)}]
    (main/info "Generating fresh 'lein new' okapi project.")
    (->files data
             [".github/workflows/release-major.yaml" (render "release-major.yaml" data)]
             [".github/workflows/release-minor.yaml" (render "release-minor.yaml" data)]
             [".github/workflows/release-patch.yaml" (render "release-patch.yaml" data)]
             [".github/workflows/release-snapshot.yaml" (render "release-snapshot.yaml" data)]
             [".github/workflows/test.yaml" (render "test.yaml" data)]
             ["src/{{sanitized}}/core.clj" (render "core.clj" data)]
             ["project.clj" (render "project.clj" data)]
             [".gitignore" (render "gitignore" data)]
             ["renovate.json" (render "renovate.json" data)]
             ["README.md" (render "README.md" data)])))
