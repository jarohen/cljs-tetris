(ns tetris.handler
  (:require [ring.util.response :refer [response content-type]]
            [compojure.core :refer [routes GET]]
            [compojure.route :refer [resources]]
            [compojure.handler :refer [api]]
            [hiccup.page :refer [html5 include-css include-js]]
            [frodo :refer [repl-connect-js]]
            [tetris.multiplayer :as multiplayer]))

(defn page-frame []
  (html5
   [:head
    [:title "Tetris - ClojureX demo"]
    (include-js "//cdnjs.cloudflare.com/ajax/libs/jquery/2.0.3/jquery.min.js")
    (include-js "//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js")
    (include-css "//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css")

    (include-js "/js/tetris.js")]
   [:body
    [:div.container
     [:div#content]
     [:script (repl-connect-js)]]]))

(defn app-routes []
  (routes
    (GET "/" [] (-> (response (page-frame))
                    (content-type "text/html")))
    (let [!users (multiplayer/init-conns)]
      (GET "/scores" {:as req} (multiplayer/user-joined! req !users)))
    (resources "/js" {:root "js"})))

(defn app []
  (-> (app-routes)
      api))
