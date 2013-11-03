(ns tetris.cljs.multiplayer-widget
  (:require [dommy.core :as d])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defprotocol PlayerNameForm
  (form->node [_]))

(defn make-player-name-form []
  (reify PlayerNameForm
    (form->node [_]
      (node
       [:div
        [:form.form-inline {:role "form" :onsubmit "return false;"}
         [:fieldset
          [:input.form-control {:type "text"
                                :placeholder "Your name"
                                :style {:width "15em"}}]
          [:button.btn.btn-primary {:style {:margin-left "1em"}} "Join"]]]]))))

(def sample-top-scores
  [{:player "Bob" :score 14}
   {:player "Steve" :score 8}
   {:player "Chris" :score 5}
   {:player "Tim" :score 2}])

(defprotocol TopScoresTable
  (table->node [_]))

(defn ts-table-node [top-scores]
  (node
   [:div
    [:table.table.table-striped.table-hover.table-condensed
     [:thead
      [:th "Player"] [:th {:style {:text-align :right}} "Score"]]
     [:tbody
      (for [{:keys [player score]} top-scores]
        (node
         [:tr
          [:td player] [:td {:style {:text-align :right}} score]]))]]]))

(defn make-top-scores-table []
  (reify TopScoresTable
    (table->node [_]
      (node [:div {:style {:margin "1em"}}
             (ts-table-node sample-top-scores)]))))

(defn multiplayer-node [table form]
  (node
   [:div {:style {:padding "1em"
                  :margin-top "1em"
                  :border "1px solid black"
                  :border-radius "1em"}}
    [:h4 "Multiplayer:"]
    (form->node form)
    (table->node table)]))

(defn make-multiplayer-widget [!top-scores !player-name commands-ch]
  (def !test-top-scores !top-scores)
  (def !test-player-name !player-name)
  (def test-commands-ch commands-ch)

  (let [table (make-top-scores-table)
        form (make-player-name-form)]
    (multiplayer-node table form)))
