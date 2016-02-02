(ns epublib-clj.core
  (:require [epublib-clj.model :as r]
            [clojure.java.io :as io]
            [schema.core :as s])
  (:import [nl.siegmann.epublib.domain Book Author Resource Identifier Date]
           [nl.siegmann.epublib.epub EpubWriter]))

; utility

(defn write [^Book book path]
  (let [writer (EpubWriter.)]
    (with-open [w (io/output-stream path)]
      (.write writer book w))))

(s/defn new-author [{:keys [first-name last-name role]} :- r/BookAuthor]
  (let [author (Author. first-name last-name)]
    (if role
      (.setRole author role))
    author))

(s/defn new-resource [{:keys [#^bytes src ^String href]}]
  (Resource. src href))

(s/defn new-date [{:keys [value event]} :- r/BookDate]
  (Date. value (str event)))

(s/defn new-identifier
  ([] (Identifier.))
  ([{:keys [^Keyword scheme ^String value]} :- r/BookIdentifier]
    (Identifier. (name scheme) value)))

(def meta-ops
 {:titles [ #(.setTitles %1 %2) :list]
  :authors [ #(.setAuthors %1 %2) :list new-author]
  :contributors [ #(.setContributors %1 %2) :list new-author]
  :language [ #(.setLanguage %1 %2) :single]
  :dates [ #(.setDates %1 %2) :list new-date]
  :rights [ #(.setRights %1 %2) :list]
  :identifiers [ #(.setIdentifiers %1 %2) :list new-identifier]
  :subjects [ #(.setSubjects %1 %2) :list]
  :types [ #(.setTypes %1 %2) :list]
  :descriptions [ #(.setDescriptions %1 %2) :list]
  :publishers [ #(.setPublishers %1 %2) :list]
  :attributes [ #(.setMetaAttributes %1 %2) :single]})

(s/defn set-metadata! [metadata model :- r/BookMeta]
  (doall
  (for [[cursor [set-fn is-list? trans-fn]] meta-ops]
    (let [value (cursor model)
          trans (or trans-fn identity)]
      (if (= is-list? :list)
        (set-fn metadata (map trans value))
        (set-fn metadata (trans value)))))))

(s/defn set-resources! [^Book book model :- (s/maybe [r/BookResource])]
  (doall
    (map 
      (fn [resource]
        (if-let [rs (new-resource resource)]
          (-> book (.getResources) (.add rs))))
      model)))

(s/defn set-sections! [^Book book model :- [r/BookSection] toc-ref]
  (doall
    (map 
      (fn [section]
        (let [title (:title section)
              rs (new-resource section)
              current-toc 
              (if toc-ref
                (.addSection book toc-ref title rs)
                (.addSection book title rs))]
          (if (:resources section) (set-resources! book (:resources section)))
          (if (:sections section)
            (set-sections! book (:sections section) current-toc))))
      model)))

(s/defn to-book [model :- r/Book]
  "convert a clojure map to epublib Book presentation"
  (let [book (Book.)
        metadata (.getMetadata book)]
    (set-metadata! metadata (:meta model))
    (if (:cover-image model)
      (.setCoverImage book (new-resource (:cover-image model))))
    (set-resources! book (:resources model))
    (set-sections! book (:sections model) nil)
    book))
