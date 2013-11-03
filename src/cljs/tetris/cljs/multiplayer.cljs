(ns tetris.cljs.multiplayer
  (:require [cljs.core.async :as a]
            [tetris.cljs.multiplayer-model :refer [wire-up-multiplayer!]]
            [tetris.cljs.multiplayer-widget :refer [make-multiplayer-widget]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn make-multiplayer-section [!game-model]
  (let [!top-scores (atom nil)
        !player-name (atom nil)
        commands-ch (a/chan)
        widget (make-multiplayer-widget !top-scores !player-name commands-ch)]
    (wire-up-multiplayer! !game-model !top-scores !player-name commands-ch)
    widget))
