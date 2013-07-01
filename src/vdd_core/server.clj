(ns vdd-core.server
  (:gen-class)
  (:use [clojure.tools.logging :only [info]]
       [clojure.tools.cli :only [cli]]
       [org.httpkit.server :only [run-server]]
       [ring.middleware.reload :only [wrap-reload]]
       [vdd-core.routes :only [app]]))

#_(defn- to-int [s] (Integer/parseInt s))

#_(defonce server (atom nil))

(defn stop-server [server]
  (when-not (nil? server)
    (info "stopping server...")
    ; contains the stop-server callback fn
    (server))) 

(defn start-server [config]
  (let [the-app (if (:hot-reload config) (wrap-reload (app config)) (app config))
        http-config (:http-kit config)
        server (run-server the-app http-config)]
    (info "server started. listen on" (:ip http-config) "@" (:port http-config))
    server))

; TODO can probably delete this
; also can remove clojure.tools.cli as a dependency of file and project
#_(defn -main [& args]
  (let [[options _ banner]
          (cli args
            ["-i" "--ip"     "The ip address to bind to"]
            ["-p" "--port"   "Port to listen"            :parse-fn to-int]
            ["-t" "--thread" "Http worker thread count"  :parse-fn to-int]
            ["--[no-]help"   "Print this help"])]
    (when (:help options) (println banner) (System/exit 0))
    ; Shutdown hook
    (. (Runtime/getRuntime) (addShutdownHook (Thread. stop-server)))
    (start-server options)))