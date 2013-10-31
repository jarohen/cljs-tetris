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

(defn make-canvas []
  (doto (canvas-node)
    (render-grid!)
    (render-cells!)))

(comment
  (let [$canvas $test-canvas
        color "red"]

    (doseq [[x y] [[4 4] [4 5] [4 6]]]
      
      (let [context (.getContext $canvas "2d")]
        (set! (.-fillStyle context) color)
        (.fillRect context
                   (* x b/block-size)
                   (* y b/block-size)
                   b/block-size
                   b/block-size)))))
