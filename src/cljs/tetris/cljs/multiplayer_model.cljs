(ns tetris.cljs.multiplayer-model
  (:require [cljs.core.async :as a]
            [chord.client :refer [ws-ch]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn wire-up-multiplayer! [!game-model !top-scores !player-name commands-ch]
  (go
   (let [{:keys [name]} (a/<! commands-ch)
         scores-ch (a/<! (ws-ch "ws://localhost:3000/scores"))]
     (a/>! scores-ch (pr-str {:name name}))
     (js/console.log "Client received: '" (:message (a/<! scores-ch)) "'"))))

