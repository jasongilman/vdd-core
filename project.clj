(defproject vdd-core "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :main vdd-core.main
  
  ; Compiled ahead of time
  :aot [vdd-core.main]
  
  ; TODO look for more exclusions to reduce the size of the uberjar created
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.taoensso/timbre "2.1.2"]
                 [compojure "1.1.5"]
                 [ring-server "0.2.8" :exclusions [ring/ring-jetty-adapter]]
                 [http-kit "2.1.6"]
                 [cheshire "5.2.0"]
                 [clj-wamp "1.0.0-rc1"]
                 [jayq "2.4.0"]
                 [hiccup "1.0.3"]]
  :plugins [[lein-cljsbuild "0.3.2"]]
  
  ; Commented out to increase repl startup speed
  ; Use lein cljsbuild auto (or once) to compile
  ; :hooks [leiningen.cljsbuild]
  
  :source-paths ["src/clj"]
  
  :repl-options {:init-ns user}
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.3"]
                                  [clj-http "0.7.4"]]}}
  
  :cljsbuild {:builds {:main {:source-paths ["src/cljs"]
                              :compiler {:output-to "resources/public/vdd/vdd-core.js"
                                         :optimizations :whitespace
                                         :pretty-print true}
                              :jar true}}})
