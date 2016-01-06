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

(s/defn new-resource [{:keys [#^bytes src ^String href]} :- r/BookResource]
  (Resource. src href))

(s/defn new-date [{:keys [value event]} :- r/BookDate]
  (Date. value event))

(s/defn new-identifier
  ([] (Identifier.))
  ([{:keys [^String scheme ^String value]} :- r/BookIdentifier]
    (Identifier. scheme value)))

(s/defn set-metadata! [metadata model :- r/BookMeta]
  (if (:titles model)
    (.setTitles metadata (:titles model)))
  (if (:authors model)
    (.setAuthors metadata
      (map new-author (:authors model))))
  (if (:contributors model)
    (.setContributors metadata 
      (map new-author (:contributors model))))
  (if (:language model)
    (.setLanguage metadata (:language model)))
  (if (:dates model)
    (.setDates metadata 
      (map new-date (:dates model))))
  (if (:rights model)
    (.setRights metadata 
      (:rights model)))
  (if (:identifiers model)
    (.setIdentifierss metadata 
      (map new-identifier (:identifiers model))))
  (if (:subjects model)
    (.setSubjects metadata 
      (:subjects model)))
  (if (:types model)
    (.setTypes metadata 
      (:types model)))
  (if (:descriptions model)
    (.setDescriptions metadata 
      (:descriptions model)))
  (if (:publishers model)
    (.setPublishers metadata 
      (:publishers model)))
  metadata)

(s/defn set-resources! [^Book book model :- r/BookResource]
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
