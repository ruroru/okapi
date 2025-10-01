(defproject okapi/lein-template "1.0.0"

  :description "A simple leiningen template "
  :url "https://github.com/ruroru/okapi"
  :license {:name "EPL-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[hato "1.0.0"]
                 [org.clojure/data.json "2.5.1"]]

  :profiles {:test {:dependencies [[babashka/fs "0.5.27"]]}}

  :plugins [[org.clojars.jj/bump "1.0.4"]
            [org.clojars.jj/strict-check "1.0.2"]]
  :eval-in-leiningen true)
