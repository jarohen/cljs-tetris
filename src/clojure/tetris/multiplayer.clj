(ns tetris.multiplayer
  (:require [chord.http-kit :refer [with-channel]]
            [clojure.core.async :refer [<! >! put! close! go go-loop]]
            [clojure.tools.reader.edn :as edn]))

(defn user-joined! [req]
  (with-channel req ws-ch
    (go-loop []
      (when-let [{:keys [message]} (<! ws-ch)]
        (>! ws-ch (str "Hi client! Server received: " message))
        (recur)))))
