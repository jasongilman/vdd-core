(defproject vdd-core "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.2.6"]
                 [log4j "1.2.17" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]
                 [compojure "1.1.5"]
                 [ring-server "0.2.8"]
                 [http-kit "2.1.2"]
                 [clj-wamp "1.0.0-beta3"]]
  :plugins [[lein-cljsbuild "0.3.2"]]
  :hooks [leiningen.cljsbuild]
  
  :source-paths ["src/clj"]
  
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.3"]
                                  [clj-http "0.7.4"]]}}
  
  :cljsbuild {:builds {:main {:source-paths ["src/cljs"]
                              :compiler {:output-to "resources/public/vdd/vdd-core.js"
                                         :optimizations :whitespace
                                         :pretty-print true}
                              :jar true}}})
