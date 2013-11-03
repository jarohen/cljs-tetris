(ns tetris.cljs.multiplayer-model
  (:require [cljs.core.async :as a]
            [chord.client :refer [ws-ch]]
            [cljs.reader :refer [read-string]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def ws-url
  (let [loc js/location]
    (str "ws://" (.-host loc) "/scores")))

(defn send-scores! [name !game-model scores-ch]
  (a/put! scores-ch {:player name :score (or (:cleared-row-count @!game-model) 0)})
  (add-watch !game-model ::multiplayer
             (fn [_ _ old-model new-model]
               (let [{old-count :cleared-row-count} old-model
                     {new-count :cleared-row-count} new-model]
                 (when (not= old-count new-count)
                   (a/put! scores-ch {:player name :score (or new-count 0)}))))))

(defn watch-other-scores! [scores-ch !top-scores]
  (go-loop []
    (reset! !top-scores (read-string (:message (a/<! scores-ch))))
    (recur)))

(defn wire-up-multiplayer! [!game-model !top-scores !player-name commands-ch]
  (go
    (let [{:keys [name]} (a/<! commands-ch)
          scores-ch (a/<! (ws-ch ws-url))]
      (send-scores! name !game-model scores-ch)
      (when (= :success (read-string (:message (a/<! scores-ch))))
        (watch-other-scores! scores-ch !top-scores)
        (reset! !player-name name)))))

