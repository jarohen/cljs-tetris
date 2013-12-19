(ns tetris.multiplayer
  (:require [chord.http-kit :refer [with-channel]]
            [clojure.core.async :refer [<! >! put! close! go go-loop]]
            [clojure.tools.reader.edn :as edn]))

(defn send-scores! [conns]
  (let [scores (->> conns
                    vals
                    (sort-by :score >)
                    (take 10)
                    (map #(dissoc % :ch))
                    pr-str)]
    (doseq [ch (keys conns)]
      (put! ch scores))))

(defn user-disconnected [conns {:keys [ch] :as conn}]
  (doto (dissoc conns ch)
    send-scores!))

(defn update-score [conns {:keys [ch score] :as conn}]
  (doto (assoc-in conns [ch :score] score)
    send-scores!))

(defn add-connection [conns {:keys [ch] :as conn} !conns]
  (let [new-conns (assoc conns ch conn)]
    (send-scores! new-conns)

    (go-loop []
      (if-let [{:keys [message]} (<! ch)]
        (let [{:keys [score]} (edn/read-string message)]
          (send-off !conns update-score (assoc conn :score score))
          (recur))
        (send-off !conns user-disconnected conn)))

    new-conns))

(defn user-joined! [req !conns]
  (with-channel req ws-ch
    (go
      (let [{:keys [message]} (<! ws-ch)
            {:keys [player score] :as user} (edn/read-string message)]
        (>! ws-ch (pr-str :success))
        (send-off !conns add-connection !conns (assoc user :ch ws-ch))))))

(defn init-conns []
  (agent {}))
