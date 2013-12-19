(defproject tetris ""

  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :dependencies [[org.clojure/clojure "1.5.1"]

                 [ring/ring-core "1.2.0"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.4"]

                 [prismatic/dommy "0.1.1"]

                 [org.clojure/core.async "0.1.242.0-44b1e3-alpha"]
                 [org.clojure/clojurescript "0.0-2120"]
                 [org.clojure/tools.reader "0.8.0"]

                 [jarohen/chord "0.2.1"]]

  :plugins [[jarohen/lein-frodo "0.2.6-SNAPSHOT"]
            [lein-cljsbuild "1.0.0"]
            [lein-pdo "0.1.1"]]

  :frodo/config-resource "tetris-config.edn"

  :source-paths ["src/clojure"]

  :resource-paths ["resources" "target/resources"]

  :cljsbuild {:builds [{:source-paths ["src/cljs"]
                        :compiler {:output-to "target/resources/js/tetris.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]}

  :aliases {"dev" ["pdo" "cljsbuild" "auto," "frodo"]
            "start" ["do" "cljsbuild" "once," "trampoline" "frodo"]})
