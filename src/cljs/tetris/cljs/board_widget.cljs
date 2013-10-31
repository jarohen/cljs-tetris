(ns tetris.cljs.board-widget
  (:require [dommy.core :as d]
            [tetris.cljs.game-board :as gb]
            [tetris.cljs.canvas :refer [make-canvas]]
            [tetris.cljs.tetraminos :as t])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defn render-tetramino! [game-board {:keys [shape location rotation color] :as piece}]
  (gb/color-cells! game-board (t/piece->cells piece) color))

(defn watch-game! [game-board !game]
  (add-watch !game ::renderer
             (fn [_ _ old-game new-game]
               (js/console.log (pr-str {:old old-game
                                        :new new-game})))))

(defn make-board-widget [!game]
  (def !test-game !game)

  (let [game-board (doto (make-canvas)
                     (watch-game! !game))]
    (gb/board->node game-board)))
