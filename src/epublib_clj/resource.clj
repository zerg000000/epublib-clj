(ns epublib-clj.resource
  (:require [schema.core :as s]
            [org.httpkit.client :as http]
            [clojure.core.async :as async]
            [clojure.java.io :as io]
            [epublib-clj.model :as r]))

(defn async-get [url result res]
  "fetch url to async channel"
  (http/get url {:as :byte-array} 
    #(async/go (async/>! result (assoc res :src (:body %))))))

(defn slurp-bytes
  "Slurp the bytes from a slurpable thing"
  [x]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (io/copy (io/input-stream x) out)
    (.toByteArray out)))

(defmulti as-bytes (fn [res _] (:type res)))
;FIXME
(defmethod as-bytes :stream [res ch] (async/go (async/>! ch res)))
;FIXME
(defmethod as-bytes :base64 [res ch] (async/go (async/>! ch res)))

(defmethod as-bytes :uri [res ch]
  (async-get (:src res) ch res))

(defmethod as-bytes :text [res ch] 
  (async/thread 
    (async/>!! ch 
      (assoc res :src (.getBytes (:src res) "UTF-8")))))

(defmethod as-bytes :fn [res ch] 
  (async/thread 
    (async/>!! ch 
      (assoc res :src (apply (:src res) res)))))

(defmethod as-bytes :file [res ch] 
  (async/thread 
    (async/>!! ch 
      (assoc res :src (slurp-bytes (:src res))))))

(defmethod as-bytes :default [res ch] (async/go (async/>! ch res)))

(defn- add-indexed-cursor [items cursor]
  (map-indexed #(assoc %2 :cursor (concat cursor [%1])) items))

(defn- resources-from-sections [model cur]
  (let [res-cur (concat cur [:resources])
        sec-cur (concat cur [:sections])]
    (flatten (concat []
      (add-indexed-cursor (:sections model) sec-cur)
      (add-indexed-cursor (:resources model) res-cur)
      (if (:sections model)
        (flatten (map-indexed 
          #(resources-from-sections %2 (concat sec-cur [%1])) 
          (:sections model))))))))

(s/defn fetch-resources [model :- r/Book]
  "fetching resources defined in clojure map convert them to byte-array
   and replace :src"
  (let [new-book (atom model)
        fetch-ch (async/chan 200)
        section-resources (concat
                            (resources-from-sections model [])
                            (if (:cover-image model) 
                              [(assoc (:cover-image model) :cursor [:cover-image])]
                              []))]
    (doseq [resource section-resources]
      (as-bytes resource fetch-ch))
    (doseq [_ (range (count section-resources))]
      (let [resource (async/<!! fetch-ch)]
        (swap! new-book update-in (concat (:cursor resource) [:src]) (fn [_] (:src resource)))))
    @new-book))