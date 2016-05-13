(defproject sounds "0.1.0-SNAPSHOT"
  :description "Overtone sounds"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [overtone "0.10-SNAPSHOT"]
                 [leipzig "0.9.0"]
                 [org.clojure/core.async "0.2.374"]]
  :plugins [[org.clojure/tools.namespace "0.2.4"]
            [cider/cider-nrepl "0.11.0"]]
  :jvm-opts ^:replace [])
