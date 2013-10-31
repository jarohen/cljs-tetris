(ns tetris.cljs.canvas
  (:require [tetris.cljs.board :as b])
  (:require-macros [dommy.macros :refer [node]]))

(defn make-canvas []
  (node
   (let [{:keys [blocks-tall blocks-wide]} b/canvas-size]
     [:canvas {:height (* b/block-size blocks-tall)
               :width (* b/block-size blocks-wide)}])))
