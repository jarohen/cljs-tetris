(ns tetris.cljs.home
  (:require [dommy.core :as d]
            [tetris.cljs.board-widget :refer [make-board-widget]]
            [tetris.cljs.cleared-rows-widget :refer [make-cleared-rows-widget]]
            [tetris.cljs.game-model :refer [wire-up-model!]]
            [cljs.core.async :as a])
  (:require-macros [dommy.macros :refer [node sel1]]
                   [cljs.core.async.macros :refer [go go-loop]]))

(defn render-keys []
  (node
   [:div {:style {:padding "1em"
                  :border "1px solid black"
                  :border-radius "12pt"}}
    [:h4 "Keys:"]
    [:ul
     (for [[key purpose] [["LEFT/RIGHT" "Move piece"]
                          ["UP/DOWN" "Rotate piece"]
                          ["SPACE" "Place piece"]
                          ["p" "Pause game"]
                          ["n" "New game"]]]
       [:li [:strong key] ": " purpose])]]))

(defn watch-hash! [!hash]
  (add-watch !hash :home-page
             (fn [_ _ _ hash]
               (when (= "#/" hash)
                 (let [!game (atom {})
                       command-ch (a/chan)]

                   (d/replace-contents! (sel1 :#content)
                                        (node [:div.row {:style {:margin-top "2em"}}
                                               [:div.col-md-5
                                                (make-board-widget !game command-ch)
                                                (make-cleared-rows-widget !game)]
                                               [:div.col-md-4
                                                (render-keys)]]))
                   (wire-up-model! !game command-ch))))))
