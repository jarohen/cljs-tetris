(ns tetris.cljs.cleared-rows-widget
  (:require [dommy.core :as d])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defprotocol ClearedRowsWidget
  (widget->node [_])
  (set-cleared-row-count! [_ cleared-count]))

(defn cleared-rows-widget []
  (let [$widget (node
                 [:div {:style {:margin-top "1em"}}])]
    (reify ClearedRowsWidget
      (widget->node [_] $widget)
      (set-cleared-row-count! [_ cleared-count]
        (d/replace-contents! $widget
                             (node [:span [:strong "Cleared rows: "] (or cleared-count 0)]))))))

(defn watch-game! [widget !game]
  (add-watch !game ::cleared-rows
             (fn [_ _ _ {:keys [cleared-row-count] :as game}]
               (set-cleared-row-count! widget cleared-row-count))))

(defn make-cleared-rows-widget [!game]
  (let [widget (doto (cleared-rows-widget)
                 (watch-game! !game))]
    (widget->node widget)))
