(ns tetris.cljs.canvas
  (:require [tetris.cljs.board :as b])
  (:require-macros [dommy.macros :refer [node]]))

(defn canvas-node []
  (node
   (let [{:keys [blocks-tall blocks-wide]} b/canvas-size]
     [:canvas {:height (* b/block-size blocks-tall)
               :width (* b/block-size blocks-wide)}])))

(defn render-grid! [$canvas]
  (let [context (.getContext $canvas "2d")
        {:keys [blocks-tall blocks-wide]} b/canvas-size]
    (doseq [i (range blocks-wide)
            j (range blocks-tall)]
      (.strokeRect context
                   (* i b/block-size)
                   (* j b/block-size) b/block-size b/block-size))))

(defn render-cells! [$canvas]
  (def $test-canvas $canvas))

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

(defn make-canvas []
  (doto (canvas-node)
    (render-grid!)
    (render-cells!)))
