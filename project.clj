(defproject chat "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [
                 ;; Server
                 [org.clojure/clojure "1.7.0"]
                 [compojure "1.4.0"]

                 ;; Cross-platform
                 [org.clojure/core.async "0.2.374"]

                 ;; Client
                 [org.clojure/clojurescript "1.7.170"]
                 [reagent "0.5.1"]
                 [cljs-http "0.1.39"]
                 ]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-3"]
            [lein-ring "0.9.7"]]

  :source-paths ["src-clj"]

  :ring {:handler chat.server/app}

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src-cljs"]

                :figwheel {:on-jsload "chat.core/on-js-reload"}

                :compiler {:main chat.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/chat.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}
               ;; This next build is an compressed minified build for
               ;; production. You can build this with:
               ;; lein cljsbuild once min
               {:id "min"
                :source-paths ["src-cljs"]
                :compiler {:output-to "resources/public/js/compiled/chat.js"
                           :main chat.core
                           :optimizations :advanced
                           :pretty-print false}}]}

  :figwheel {;; :http-server-root "public" ;; default and assumes "resources"
             ;; :server-port 3449 ;; default
             ;; :server-ip "127.0.0.1"

             :css-dirs ["resources/public/css"] ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process
             ;; :nrepl-port 7888

             ;; Server Ring Handler (optional)
             ;; if you want to embed a ring handler into the figwheel http-kit
             ;; server, this is for simple ring servers, if this
             ;; doesn't work for you just run your own server :)
             ;; :ring-handler hello_world.server/handler

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             ;; if you want to disable the REPL
             ;; :repl false

             ;; to configure a different figwheel logfile path
             ;; :server-logfile "tmp/logs/figwheel-logfile.log"
             })
