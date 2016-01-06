(ns epublib-clj.model
  (:require [schema.core :as s]))

(def o s/optional-key)

(def support-types [:stream :base64 :uri :text :fn :file])

(def BookResource
  {:src s/Any
   :type (s/enum support-types)
   :href s/Str})

(def BookAuthor
  {:first-name s/Str
   :last-name s/Str
   (o :role) s/Str})

(def identifier-schemes [:ISBN :UUID :URL :URI])

(def BookIdentifier
  {:scheme (s/enum identifier-schemes) 
   :value s/Str})

(def BookDate
  {:value s/Str
   (o :event) s/Str})

(def BookMeta
  {:title            s/Str
   :authors           [BookAuthor]
   (o :contributors) [BookAuthor]
   (o :language)     s/Str
   (o :dates)        [BookDate]
   (o :rights)       [s/Str]
   (o :titles)       [s/Str]
   (o :identifiers)  [BookIdentifier]
   (o :subjects)     [s/Str]
   (o :types)        [s/Str]
   (o :descriptions) [s/Str]
   (o :publishers)   [s/Str]
   (o :attributes)   {s/Str s/Str}})

(def BookSection
  (merge BookResource
    {:title s/Str
     (o :resources) [BookResource]
     (o :sections) [BookResource]}))

(def Book
  {:meta BookMeta
   (o :cover-image) BookResource
   (o :resources) [BookResource]
   :sections [BookSection]})