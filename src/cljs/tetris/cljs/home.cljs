(ns tetris.cljs.home
  (:require [dommy.core :as d]
            [tetris.cljs.game-board :as gb]
            [tetris.cljs.canvas :refer [make-canvas]]

            [tetris.cljs.board :as b]
            [tetris.cljs.tetraminos :as t])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defn watch-hash! [!hash]
  (add-watch !hash :home-page
             (fn [_ _ _ hash]
               (when (= "#/" hash)
                 (let [game-board (make-canvas)]
                   (def test-game-board game-board)
                   (d/replace-contents! (sel1 :#content)
                                        (node [:div.row {:style {:margin-top "2em"}}
                                               [:div.col-md-6
                                                (gb/board->node game-board)]])))))))

(comment
  (defn render-tetramino! [game-board {:keys [shape location rotation color] :as piece}]
    (gb/color-cells! game-board (t/piece->cells piece) color))
  
  (let [game-board test-game-board
        {:keys [blocks-wide blocks-tall]} b/canvas-size]

    (render-tetramino! game-board
                       {:shape (rand-nth (vec t/shapes))
                        :color (rand-nth (vec t/colors))
                        :rotation (rand-int 4)
                        :location (map rand-int [blocks-wide blocks-tall])})))
