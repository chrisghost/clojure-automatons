(ns clife.render)

(def color-alive "#b0b0b0")
(def color-dead "#202020")
(def colors-alive ["red" "green" "blue" "purple" "white"])

(defn cell
  "render a single cell, dead or alive, on a world"
  [{:keys [canvas ctx rows cols cell-size]} [x y] alive?]

    (let [px (* x cell-size) 
          py (* y cell-size)]

      ;(set! (.-fillStyle ctx) (if alive? (get colors-alive (rand-int (count colors-alive))) color-dead))
      (set! (.-fillStyle ctx) (if alive? color-alive color-dead))
      (.fillRect ctx px py cell-size cell-size)
    )
  )

(defn world
  "render a complete world with a set of alive cells"
  [{:keys [canvas ctx rows cols cell-size] :as world } cells]
  ;(pp cells)
  (doseq [cel cells] (cell world cel true))
)

