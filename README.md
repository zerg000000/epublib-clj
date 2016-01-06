# epublib-clj

[![Build Status](https://travis-ci.org/zerg000000/epublib-clj.svg)](https://travis-ci.org/zerg000000/epublib-clj)
[![Clojars Project](https://img.shields.io/clojars/v/epublib-clj.svg)](https://clojars.org/epublib-clj)

A Clojure library which wrap the epublib to convert clojure map to epub.

## Usage

This library do only one thing -- convert clojure map to a epub.

An simple example

```{clojure}
(use 'epublib-clj.core)
(use 'epublib-clj.resource)

(def data {
  :meta {
    :titles ["Welcome to Hello World"]  ; mandatory
    :authors [{:first-name "Peter" :last-name "Pan"}] ; mandatory
    :dates [{:value "2015-10-10" "publication"}
            {:value "2016-12-10" "creation"}]
  }
  :cover-image {
    :src "http://example.com/example.png" 
    :type :uri 
    :href "cover.png"}
  :sections [
    {:title "Chapter 1" :href "chapt1.html" :type :text :src "First Chapter"}
    {:title "Chapter 2" :href "chapt2.html" :type :text :src "Second"}
    {:title "Chapter 3" :href "chapt3.html" :type :text :src "The End"}
  ]
})

(-> data fetch-resources to-book (write "my.epub"))
```

An complete example 

```{clojure}
{
  :meta {
    :titles ["Welcome to Hello World"]  ; mandatory
    :authors [{:first-name "Peter" :last-name "Pan"}] ; mandatory
    :dates [{:value "2015-10-10" "publication"}
            {:value "2016-12-10" "creation"}]
  }
  :cover-image {
    :src "http://example.com/example.png" 
    :type :uri 
    :href "cover.png"}
  :resources [
    {:href "dog1.png" :type :file :src "doc/dog1.png"}
    {:href "dog2.png" :type :file :src "doc/dog2.png"}
    {:href "dog3.png" :type :file :src "doc/dog3.png"}
  ]
  :sections [
    {:title "Chapter 1" :href "chapt1.html" :type :file :src "doc/index.html"}
    {:title "Chapter 2" :href "chapt2.html" :type :uri :src "http://example.com/chapter2.html"
     :resources [
      {:href "dog1ch2.png" :type :file :src "doc/dog1ch2.png"}
     ]
    }
    {:title "Chapter 3" :href "chapt3.html" :type :text :src "The End"
     :sections [
       {:title "Chapter 3-?" :href "chapt3_99.html" :type :text :src "The Begin of other heros"}
     ]}
  ]
}
```

## Section / Resource / Cover Image

Actually, Section/Resources/Cover Image are both a resource, which means
they all share the same properties

```{clojure}
{:href "dog3.png" :type :file :src "doc/dog3.png"}
```

:href is the location of the resource in the epub.
:src is the location of the resource exists in the running machine
:type is how the program should handle the :src


## License

Copyright © 2016 Algo Technologies Limited

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
