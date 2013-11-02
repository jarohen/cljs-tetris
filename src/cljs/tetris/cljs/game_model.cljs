(ns tetris.cljs.game-model
  (:require [tetris.cljs.board :as b]
            [tetris.cljs.tetraminos :as t]
            [cljs.core.async :as a])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn valid-starting-locations [{:keys [shape rotation] :as piece}]
  (let [cells (t/piece->cells (assoc piece :location [0 0]))
        {:keys [blocks-wide]} b/canvas-size
        ;; one row visible
        y (- (apply max (map second cells)))]

    ;; all of shape has to fit in width of canvas
    (for [valid-x (range (- (apply min (map first cells)))
                         (- blocks-wide (apply max (map first cells))))]
      [valid-x y])))

(defn random-piece []
  (let [shape (rand-nth (vec t/shapes))
        rotation (rand-int 4)]
    {:shape shape
     :color (rand-nth (vec t/colors))
     :rotation rotation
     :location (rand-nth (valid-starting-locations {:shape shape :rotation rotation}))}))

(defn new-game []
  {:current-piece (random-piece)})

;; ---------- TICK ----------

(defn ticker-ch [ms]
  (let [ch (a/chan)]
    (go-loop []
      (a/<! (a/timeout ms))
      (a/>! ch :tick)
      (recur))
    ch))

(defn apply-tick [game]
  (js/console.log "Tick!")
  (update-in game [:current-piece :location 1] inc))

(defn repeatedly-tick! [!game]
  (let [tick-ch (ticker-ch 500)]
    (go-loop []
      (<! tick-ch)
      (swap! !game apply-tick)
      (recur))))

;; ---------- COMMANDS ----------

(def movement-command?
  #{:piece-left :piece-right :piece-down
    :rotate-piece-clockwise :rotate-piece-anti-clockwise})

(defmulti calculate-new-position #(identity %2))

(defmethod calculate-new-position :piece-left [game _]
  (update-in game [:current-piece :location 0] dec))

(defmethod calculate-new-position :piece-right [game _]
  (update-in game [:current-piece :location 0] inc))

(defmethod calculate-new-position :rotate-piece-anti-clockwise [game _]
  (update-in game [:current-piece :rotation] inc))

(defmethod calculate-new-position :rotate-piece-clockwise [game _]
  (update-in game [:current-piece :rotation] dec))

(defmethod calculate-new-position :piece-down [game _]
  ;; TODO 
  game)

(defn valid-game? [{:keys [current-piece] :as new-game}]
  (let [cells (t/piece->cells current-piece)
        {:keys [blocks-wide]} b/canvas-size]
    (every? (fn [[x y]]
              (and (< -1 x blocks-wide)))
            cells)))

(defn apply-movement [game command]
  (let [new-game (calculate-new-position game command)]
    (if (valid-game? new-game)
      new-game
      game)))

(defn apply-command [game command]
  (cond
   (movement-command? command) (apply-movement game command)
   (= :new-game command) (new-game)))

(defn apply-commands! [!game command-ch]
  (go-loop []
    (let [command (<! command-ch)]
      (swap! !game apply-command command))
    (recur)))

;; ---------- WIRING UP ----------

(defn wire-up-model! [!game command-ch]
  (def !test-game !game)
  (def test-command-ch command-ch)

  (doto !game
    (reset! (new-game))
    (repeatedly-tick!)
    (apply-commands! command-ch)))
