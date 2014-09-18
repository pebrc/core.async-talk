(ns core.async-talk
  (:require  [clojure.core.async :refer :all :as async]
             [clojure.pprint :refer [pprint]]))

;; Examples are taken or inspired from Tim Baldriges Conj 2013 talk
;; https://github.com/halgari
;; and the 'official' core.async walkthrough
;; https://github.com/clojure/core.async


;; BUILDING BLOCKS

(chan)

;; Buffered channel
(chan 10)

;; Put

(def c (chan))

(>!! c "hello");; never succeeds

(go (>! c "hello"))

;;; Complexity of the go macro
(pprint (macroexpand '(go 42)))


;; Take

(<!! c)

;; Closing a channel

(close! c)

(<!! c)


;;;Backpressure


(def fixedchan (chan 1))

(go (>! fixedchan 1)
    (println "done 1"))
(go (>! fixedchan 2)
    (println "done 2"))

(<!! fixedchan)
(<!! fixedchan)

;;; ALTS
(def a (chan))
(def b (chan))

(put! a 42)

(alts!! [a b]) ;; returns [value chan]

;;; TIMEOUTS

(<!! (timeout 2000))


;;; Combine the two

(go (let [[v c] (alts! [a (timeout 2000)])]
      (println "Read " v " from " c)))

(put! a 42)


;;; ClojureScript examples from T. Baldridge ;;;;

(require 'cljs.repl.browser)

(cemerick.piggieback/cljs-repl
  :repl-env (cljs.repl.browser/repl-env :port 9000))

(ns cljs-examples
  (:require [cljs.core.async :refer [chan put! take! timeout] :as async]
            [clojure.walk :refer [prewalk]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(js/alert "We're running ClojureScript")


(def canvas (.getElementById js/document "canvas"))


(def colors ["#816D62"
             "#665A55"
             "#474E5D"
             "#40B0F9"
             "#F37B20"
             "#FFFFFF"])

(defn make-cell [canvas x y]
  (let [ctx (-> js/document
                (.getElementById canvas)
                (.getContext "2d"))]
    (go (while true
          (set! (.-fillStyle ctx) (rand-nth colors))
          (.fillRect ctx x y 10 10)
          (<! (timeout (rand-int 1000)))))))

(defn make-scene [canvas rows cols]
  (dotimes [x cols]
    (dotimes [y rows]
      (make-cell canvas (* 10 x) (* 10 y)))))


(make-scene "canvas" 100 100)

