(ns clife (:require [clife.render :as render][clife.rle :as rle]))

; some predefined interesting patterns
(def blinker #{[1 0] [1 1] [1 2]})
(def glider #{[1 0] [2 1] [0 2] [1 2] [2 2]})
(def r-pentomino #{[0 1] [1 0] [1 1] [1 2] [2 0]})
(def acorn #{[0 2] [1 0] [1 2] [3 1] [4 2] [5 2] [6 2]})
(def lightweight-spaceship #{[0 1] [0 2] [0 3] [1 0] [1 3] [2 3] [3 3] [4 0] [4 2]})

(defn pp
  "print and pass the parameter"
  [v]
  (.log js/console (clj->js v))
  v)

(def gState (atom #{}))

(defn neigb [[x y]]
  [
    [(- x 1) (- y 1)]
    [(- x 1) (+ y 0)]
    [(- x 1) (+ y 1)]
    [(+ x 0) (- y 1)]
    [(+ x 0) (+ y 1)]
    [(+ x 1) (- y 1)]
    [(+ x 1) (+ y 0)]
    [(+ x 1) (+ y 1)]
  ]
  )

(defn shift
  "return cells shifted by dx and dy"
  [cells dx dy]

  (map (fn [[x y]] [(+ x dx) (+ y dy)]) cells)
  )

(defn step [cells]
  (let [isalive (fn [cell] (contains? cells cell))]
    (filter (fn [c]
        (if (isalive c)
          (case (count (filter isalive (neigb c)))
            2 true
            3 true
            false
          )
          (case (count (filter isalive (neigb c)))
            3 true
            false
          )
        )
      )
      (into cells (flatmap neigb cells))
    )
  )
)

(defn game-step [{:keys [canvas ctx rows cols cell-size] :as world }]
   (let [next-cells (step (set @gState))]
      (set! (.-fillStyle ctx) "black")
      (.fillRect ctx 0 0 (* cols cell-size) (* rows cell-size) )
      (render/world world next-cells)
      (reset! gState next-cells)
      (js/setTimeout #(game-step world) 16)
     ))



(defn load-pattern []
  (let [
      slct (.getElementById js/document "selector")
      get-size (fn [pat]
                 (let [
                    minx (apply min (map first pat))
                    miny (apply min (map second pat))
                    maxx (apply max (map first pat))
                    maxy (apply max (map second pat))
                  ]
                  [(- maxx minx) (- maxy miny)]
                  )
               )
      ]
    (clife.rle/fetch (.-value slct)
      (fn [s]
        (let [ [x y] (get-size s) ]
          (pp x)
          (pp y)
          (reset! gState (shift s (/ x 2) (/ y 2))))))
))

(defn ^:export hop
  "Entry point"
  []
  (let [cols 250
        rows 150
        cell-size 4
        canvas (.getElementById js/document "world")
        ctx (.getContext canvas "2d")
        world {:canvas canvas
               :ctx ctx
               :rows rows
               :cols cols
               :cell-size cell-size}
        cells (shift r-pentomino 125 75)
        slct (.getElementById js/document "selector")
       ]
    (set! (.-width canvas) (* cols cell-size))
    (set! (.-height canvas) (* rows cell-size))

    (.addEventListener slct "change" load-pattern)

    (game-step world)
    ))
