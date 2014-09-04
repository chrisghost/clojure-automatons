(ns clife.rle
    (:require [goog.net.XhrIo :as xhr]))

(defn- parse [data]
    (for [[x ys] (map-indexed vector (js/rleParser data 600))
                  [y alive?] (map-indexed vector ys)
                  :when alive?]
          [x y]))

(defn fetch [file callback]
  (xhr/send (str "patterns/" file) #(->> %
                                      .-target
                                      .getResponse
                                      parse
                                      (take 2000)
                                      callback)))

