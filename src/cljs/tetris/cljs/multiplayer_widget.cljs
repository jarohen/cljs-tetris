(ns tetris.cljs.multiplayer-widget
  (:require [dommy.core :as d]
            [clojure.string :as s]
            [cljs.core.async :as a])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defprotocol PlayerNameForm
  (form->node [_])
  (on-join [_ f])
  (player-name [_])
  (set-enabled! [_ enabled?])
  (set-visibility! [_ visible?]))

(defn make-player-name-form []
  (let [$name (node [:input.form-control {:type "text"
                                          :placeholder "Your name"
                                          :style {:width "15em"}}])
        $button (node [:button.btn.btn-primary {:style {:margin-left "1em"}}
                       "Join"])
        $form (node
               [:div
                [:form.form-inline {:role "form" :onsubmit "return false;"}
                 [:fieldset
                  $name
                  $button]]])]
    (reify PlayerNameForm
      (form->node [_]
        $form)
      (on-join [_ f]
        (d/listen! $button :click f))
      (player-name [_]
        (d/value $name))
      (set-enabled! [_ enabled?]
        (if enabled?
          (d/remove-attr! $button :disabled)
          (d/set-attr! $button :disabled true)))
      (set-visibility! [_ visible?]
        (if visible?
          (d/show! $form)
          (d/hide! $form))))))

(defprotocol TopScoresTable
  (table->node [_])
  (set-scores! [_ scores]))

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
  (let [$table (node [:div {:style {:margin "1em"}}])]
    (reify TopScoresTable
      (table->node [_]
        $table)
      (set-scores! [_ scores]
        (d/replace-contents! $table (ts-table-node scores))))))

(defn multiplayer-node [table form]
  (node
   [:div {:style {:padding "1em"
                  :margin-top "1em"
                  :border "1px solid black"
                  :border-radius "1em"}}
    [:h4 "Multiplayer:"]
    (form->node form)
    (table->node table)]))

(defn bind-join! [form commands-ch]
  (on-join form
           (fn []
             (let [name (player-name form)]
               (when-not (s/blank? name)
                 (set-enabled! form false)
                 (a/put! commands-ch {:name name}))))))

(defn bind-top-scores! [table !top-scores]
  (add-watch !top-scores ::renderer
             (fn [_ _ _ scores]
               (set-scores! table scores))))

(defn bind-form-visibility! [form !player-name]
  (set-visibility! form (nil? @!player-name))
  (add-watch !player-name ::form-visibility
             (fn [_ _ _ name]
               (set-visibility! form (nil? name)))))

(defn make-multiplayer-widget [!top-scores !player-name commands-ch]
  (def !test-top-scores !top-scores)
  (def !test-player-name !player-name)
  (def test-commands-ch commands-ch)

  (let [table (doto (make-top-scores-table)
                (bind-top-scores! !top-scores))
        form (doto (make-player-name-form)
               (bind-join! commands-ch)
               (bind-form-visibility! !player-name))]

    (multiplayer-node table form)))

(comment
  (reset! !test-top-scores [{:player "Bob" :score 14}
                            {:player "Steve" :score 8}
                            {:player "Chris" :score 5}
                            {:player "Tim" :score 2}])

  (reset! !test-player-name "James"))
