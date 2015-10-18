(defproject cdm "0.1.0"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.48"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [cljsjs/react "0.13.3-1"]
                 [sablono "0.3.6"]
                 [cljs-react-reload "0.1.1"]
                 [cljsjs/showdown "0.4.0-1"]
                 [devcards "0.2.0-3"]
                 [rum "0.5.0"]
                 [jayq "2.5.4"]]

  :plugins [[lein-cljsbuild "1.1.0"]
            [lein-figwheel "0.4.1"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "resources/public/js/devcards_out"
                                    "resources/public/js"
                                    "target"]

  :cljsbuild {
    :builds [{:id "devcards"
              :source-paths ["src"]

              :figwheel {:on-jsload "cdm.core/on-js-reload"
                         :devcards true}

              :compiler {:main cdm.devcards
                         :asset-path "js/devcards_out"
                         :output-to "resources/public/js/cdm_devcards.js"
                         :output-dir "resources/public/js/devcards_out"
                         :source-map-timestamp true }}

             {:id "dev"
              :source-paths ["src"]

              :figwheel { :on-jsload "cdm.core/on-js-reload" }

              :compiler {:main cdm.core
                         :asset-path "js/compiled/out"
                         :output-to "resources/public/js/compiled/cdm.js"
                         :output-dir "resources/public/js/compiled/out"
                         :source-map-timestamp true }}

             {:id "min"
              :source-paths ["src"]
              :compiler {:output-to "resources/public/js/compiled/cdm.js"
                         :main cdm.core
                         :optimizations :advanced
                         :pretty-print false}}]}

  :figwheel {
             ;; :http-server-root "public" ;; default and assumes "resources"
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
