(defproject {{name}}  "1.0.0-SNAPSHOT"
  :description "TODO"
  :url "TODO"
  :license {:name "EPL-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "{{clojure-version}}"]]

  :deploy-repositories [["clojars" {:url      "https://repo.clojars.org"
                                    :username :env/clojars_user
                                    :password :env/clojars_pass}]]

  :plugins [[org.clojars.jj/bump {{{bump-version}}}]
            [org.clojars.jj/strict-check {{{strict-check-version}}}]])
