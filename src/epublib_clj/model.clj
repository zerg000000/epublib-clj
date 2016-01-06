(ns epublib-clj.model
  (:require [schema.core :as s]))

(def o s/optional-key)

; default only support for :stream :text :fn :file
(def BookResource
  {:src s/Any
   :type s/Keyword
   :href s/Str})

(def BookAuthor
  {:first-name s/Str
   :last-name s/Str
   (o :role) s/Str})

(def identifier-schemes (s/enum :ISBN :UUID :URL :URI))

(def BookIdentifier
  {:scheme identifier-schemes
   :value s/Str})

(def BookDate
  {:value s/Str
   (o :event) s/Str})

(def BookMeta
  {:titles           [s/Str]
   :authors          [BookAuthor]
   (o :contributors) [BookAuthor]
   (o :language)     s/Str
   (o :dates)        [BookDate]
   (o :rights)       [s/Str]
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
     (o :sections) [s/Any]}))
;FIXME recursive definition don't work

(def Book
  {:meta BookMeta
   (o :cover-image) BookResource
   (o :resources) [BookResource]
   :sections [BookSection]})