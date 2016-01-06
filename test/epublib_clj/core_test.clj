(ns epublib-clj.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [epublib-clj.core :refer :all]
            [epublib-clj.resource :refer :all]))

(def book-fixture {
  :meta {:titles ["A Book"]
         :authors [{:first-name "Tom" :last-name "Lee"}]}
  :sections [
    {:title "Chapter One" :type :stream :src (.getBytes "Hello World" "UTF-8") :href "index.html"}
  ]
  })

(def book-file-fixture {
  :meta {:titles ["B Book"]
         :authors [{:first-name "Tom" :last-name "Lee"}]
         :dates [{:value "2015-01-01" :event "publication"}]
         :language "zh_TW"}
  :cover-image {:src "https://realworldocaml.org/media/img/coversmall.png"
                :href "cover.png"
                :type :uri}
  :sections [
    {:title "Chapter One" :type :text :src "Bye World" :href "index.html"
     :resources [{:href "index3.png" :type :text :src "FHFHF"}]}
    {:title "Chapter 2" :type :text :src "Bye World 2" :href "index2.html"
     :sections [
       {:title "Chapter 2.1" :type :text :src "Bye World 2.1" :href "index2_1.html"}
     ]}
  ]
  })

(deftest a-test
  (testing "generate book from in-mem"
    (write (to-book book-fixture) "target/abc.epub")
    (let [book (fetch-resources book-file-fixture)]
      (write (to-book book) "target/edf.epub"))
    (is (= 1 1))))