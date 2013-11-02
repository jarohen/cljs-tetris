(ns tetris.cljs.home
  (:require [dommy.core :as d]
            [tetris.cljs.board-widget :refer [make-board-widget]]
            [tetris.cljs.game-model :refer [wire-up-model!]]
            [cljs.core.async :as a])
  (:require-macros [dommy.macros :refer [node sel1]]
                   [cljs.core.async.macros :refer [go go-loop]]))

(defn watch-hash! [!hash]
  (add-watch !hash :home-page
             (fn [_ _ _ hash]
               (when (= "#/" hash)
                 (let [!game (atom {})
                       command-ch (a/chan)]

                   (d/replace-contents! (sel1 :#content)
                                        (node [:div.row {:style {:margin-top "2em"}}
                                               [:div.col-md-6
                                                (make-board-widget !game command-ch)]]))
                   (wire-up-model! !game command-ch))))))
