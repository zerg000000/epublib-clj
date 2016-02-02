(defproject epublib-clj "0.1.0"
  :description "Clojure wrapper for epublib java library"
  :url "https://github.com/zerg000000/epublib-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.374"]
                 [prismatic/schema "1.0.4"]
                 [nl.siegmann.epublib/epublib-core "3.1"]]
  :repositories [["pageturner" "http://repo.pageturner-reader.org/"]])
