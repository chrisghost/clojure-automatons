(ns clife (:require [clife.render :as render][clife.rle :as rle]))

(defn pp
  "print and pass the parameter"
  [v]
  (.log js/console (clj->js v))
  v)

(defn get-cell [pos cells]
  (contains? (set cells) pos)
  )

(defn turn-left [dir]
  (cond
    (= dir "up") "left"
    (= dir "down") "right"
    (= dir "left") "down"
    :else "up"
  ))

(defn turn-right [dir]
  (cond
    (= dir "up") "right"
    (= dir "down") "left"
    (= dir "left") "up"
    :else "down"
  ))

(defn next-cell [[x y] dir]
  (cond
    (= dir "up") [x (- y 1)]
    (= dir "down") [x (+ y 1)]
    (= dir "left") [(- x 1) y]
    :else [(+ x 1) y]
  ))

(defn langton-step [{:keys [canvas ctx rows cols cell-size ant-pos ant-dir cells] :as world }]
  (let
    [ next-dir  (if (get-cell ant-pos cells)
                  (turn-right ant-dir)
                  (turn-left ant-dir))
     ;_ (pp next-dir)
     ncell      (next-cell ant-pos next-dir)
     ;_ (pp ant-pos)
     ;_ (pp ncell)
     ]
    (if (get-cell ncell cells)
      [next-dir (filter #(not= % ncell) cells)]
      [next-dir (conj cells ncell)]
      )
    )
  )

(defn langton [{:keys [canvas ctx rows cols cell-size ant-pos ant-dir cells step] :as world }]
   (let [[ndir next-cells] (langton-step world)
         npos (next-cell ant-pos ndir)
         nstep (+ step 1)
         nworld (merge world {:cells next-cells :ant-pos npos :ant-dir ndir :step nstep})
         ]
      (set! (.-fillStyle ctx) "black")
      (.fillRect ctx 0 0 (* cols cell-size) (* rows cell-size) )

      ;(pp ndir)
      ;(pp next-cells)
      (render/world nworld next-cells)
      ;(reset! gState next-cells)
      ;(pp ant-pos)
      (js/setTimeout #(langton nworld) 1)
     ))

(defn ^:export hop
  "Entry point"
  []
  (let [cols 250
        rows 250
        cell-size 2
        canvas (.getElementById js/document "world")
        ctx (.getContext canvas "2d")
        world {:canvas canvas
               :ctx ctx
               :rows rows
               :cols cols
               :cell-size cell-size
               :ant-pos [ (/ cols 2) (/ rows 2) ]
               :ant-dir "up"
               :cells #{ [ (/ cols 2) (/ rows 2) ]}
               :step 0
               }
       ]
    (set! (.-width canvas) (* cols cell-size))
    (set! (.-height canvas) (* rows cell-size))

    (langton world)
    ))
