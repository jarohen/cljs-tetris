(ns tetris.cljs.board-widget
  (:require [dommy.core :as d]
            [tetris.cljs.game-board :as gb]
            [tetris.cljs.canvas :refer [make-canvas]]
            [tetris.cljs.tetraminos :as t]
            [cljs.core.async :as a])
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

(defn listen-for-keypresses! [game-board command-ch]
  )

(defn make-board-widget [!game command-ch]
  (def !test-game !game)
  (def test-command-ch command-ch)
  
    (let [game-board (doto (make-canvas)
                       (watch-game! !game)
                       (listen-for-keypresses! command-ch))]
      (gb/board->node game-board)))

