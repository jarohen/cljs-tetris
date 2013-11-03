(ns tetris.cljs.canvas
  (:require [tetris.cljs.board :as b]
            [tetris.cljs.game-board :as gb]
            [cljs.core.async :as a]
            [dommy.core :as d]
            [goog.events.KeyCodes :as kc])
  (:require-macros [dommy.macros :refer [node]]
                   [cljs.core.async.macros :refer [go-loop]]))

(defn canvas-node []
  (node
   (let [{:keys [blocks-tall blocks-wide]} b/canvas-size]
     [:canvas {:height (* b/block-size blocks-tall)
               :width (* b/block-size blocks-wide)
               :tabindex 0}])))

(defn render-grid! [$canvas]
  (let [context (.getContext $canvas "2d")
        {:keys [blocks-tall blocks-wide]} b/canvas-size]
    (doseq [i (range blocks-wide)
            j (range blocks-tall)]
      (.strokeRect context
                   (* i b/block-size)
                   (* j b/block-size) b/block-size b/block-size))))

(defn color-cell! [$canvas [x y] color]
  (let [context (.getContext $canvas "2d")]
    (set! (.-fillStyle context) color)
    (.fillRect context
               (inc (* x b/block-size))
               (inc (* y b/block-size))
               (- b/block-size 2)
               (- b/block-size 2))))

(defn color-cells! [$canvas cells color]
  (doseq [cell cells]
    (color-cell! $canvas cell color)))

(defn color-rows! [$canvas rows color]
  (let [cells (for [y rows
                    x (range (:blocks-wide b/canvas-size))]
                [x y])]
    (color-cells! $canvas cells color)))

(defn color-all! [$canvas color]
  (let [{:keys [blocks-tall blocks-wide]} b/canvas-size
        cells (for [x (range blocks-wide)
                    y (range blocks-tall)]
                [x y])]
    (color-cells! $canvas cells color)))

(def keycode->command
  {kc/SPACE :piece-down
   kc/LEFT :piece-left
   kc/RIGHT :piece-right
   kc/UP :rotate-piece-clockwise
   kc/DOWN :rotate-piece-anti-clockwise
   kc/N :new-game
   kc/P :toggle-pause})

(defn command-ch [$canvas]
  (let [ch (a/chan)]
    (d/listen! $canvas :keydown
               (fn [e]
                 (when-let [command (keycode->command (.-keyCode e))]
                   (a/put! ch command)
                   (.preventDefault e))))
    ch))

(defn bind-cell-flashing! [$canvas cells-ch]
  (go-loop [cells []
            colour "red"]
    (let [[v c] (a/alts! [(a/timeout 600) cells-ch])]
      ;; not v if timeout, v if new cells list
      (if v
        (do
          (color-cells! $canvas cells "white")
          (recur v colour))
        (do
          (color-cells! $canvas cells colour)
          (recur cells ({"white" "red" "red" "white"} colour)))))))

(defn make-canvas []
  (let [flashing-cells-ch (a/chan)
        $canvas (doto (canvas-node)
                  (render-grid!)
                  (bind-cell-flashing! flashing-cells-ch))]
    
    (reify gb/GameBoard
      (board->node [_] $canvas)
      (color-cell! [_ cells color]
        (color-cell! $canvas cells color))
      (color-cells! [_ cells color]
        (color-cells! $canvas cells color))
      (color-rows! [_ rows color]
        (color-rows! $canvas rows color))
      (color-all! [_ color]
        (color-all! $canvas color))
      
      (command-ch [_]
        (command-ch $canvas))

      (flash-cells! [_ cells]
        (a/put! flashing-cells-ch cells)))))
