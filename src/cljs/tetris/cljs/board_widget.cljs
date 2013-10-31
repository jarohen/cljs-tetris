(ns tetris.cljs.board-widget
  (:require [dommy.core :as d]
            [tetris.cljs.game-board :as gb]
            [tetris.cljs.canvas :refer [make-canvas]]
            [tetris.cljs.tetraminos :as t]

            [tetris.cljs.board :as b])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defn render-tetramino! [game-board {:keys [shape location rotation color] :as piece}]
  (gb/color-cells! game-board (t/piece->cells piece) color))

(defn render-current-piece! [game-board {old-piece :current-piece} {new-piece :current-piece}]
  (when (not= old-piece new-piece)
    (when old-piece
      (render-tetramino! game-board (assoc old-piece :color "white")))
    (when new-piece
      (render-tetramino! game-board new-piece))))

(defn watch-game! [game-board !game]
  (add-watch !game ::renderer
            (fn [_ _ old-game new-game]
              (render-current-piece! game-board old-game new-game))))

(defn make-board-widget [!game]
  (def !test-game !game)

  (let [game-board (doto (make-canvas)
                     (watch-game! !game))]
    (gb/board->node game-board)))

(comment
  (reset! !test-game
          (let [{:keys [blocks-wide blocks-tall]} b/canvas-size]
            {:current-piece {:shape (rand-nth (vec t/shapes))
                             :color (rand-nth (vec t/colors))
                             :rotation (rand-int 4)
                             :location (map rand-int [blocks-wide blocks-tall])}})))
