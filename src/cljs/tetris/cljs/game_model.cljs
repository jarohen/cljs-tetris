(ns tetris.cljs.game-model
  (:require [tetris.cljs.board :as b]
            [tetris.cljs.tetraminos :as t]
            [cljs.core.async :as a])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn valid-starting-locations [{:keys [shape rotation] :as piece}]
  (let [cells (t/piece->cells (assoc piece :location [0 0]))
        {:keys [blocks-wide]} b/canvas-size
        ;; one row visible
        y (- (apply max (map second cells)))]

    ;; all of shape has to fit in width of canvas
    (for [valid-x (range (- (apply min (map first cells)))
                         (- blocks-wide (apply max (map first cells))))]
      [valid-x y])))

(defn random-piece []
  (let [shape (rand-nth (vec t/shapes))
        rotation (rand-int 4)]
    {:shape shape
     :color (rand-nth (vec t/colors))
     :rotation rotation
     :location (rand-nth (valid-starting-locations {:shape shape :rotation rotation}))}))

(defn new-game []
  {:current-piece (random-piece)})

(defn repeatedly-tick! [!game]
  )

(defn apply-commands! [!game command-ch]
  )

(defn wire-up-model! [!game command-ch]
  (def !test-game !game)
  (def test-command-ch command-ch)

  (doto !game
    (reset! (new-game))
    (repeatedly-tick!)
    (apply-commands! command-ch)))
