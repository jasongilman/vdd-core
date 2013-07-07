(ns vdd-core.internal.system
  (:require [org.httpkit.server :as httpkit]
            [ring.middleware.reload :as ring-reload]
            [clojure.java.io :as io]
            [vdd-core.internal.routes :as routes]
            [taoensso.timbre :as timbre
             :refer (trace debug info warn error fatal spy)]))

(defn- stop-server [server]
  (when-not (nil? server)
    (info "stopping server...")
    ; contains the stop-server callback fn
    (server))) 

(defn- start-server [config]
  (let [http-config {:ip "0.0.0.0"
                     :port (:port config)
                     :thread 2}
        the-app (ring-reload/wrap-reload (routes/app config)) 
        server (httpkit/run-server the-app http-config)]
    (info "server started. listen on" (:ip http-config) "@" (:port http-config))
    server))

(defn system
  "Returns a new instance of the whole application."
  [config]
  {:pre [(:port config)
         (:viz-root config)]}
  {:config config
   :server nil})

(defn- setup-logging [config]
  (let [log-config (:log config)]
    (timbre/set-level! (or (:level log-config) :warn))
    (timbre/set-config! [:timestamp-pattern] "yyyy-MM-dd HH:mm:ss")
    
    ; Enable file logging
    (timbre/set-config! [:appenders :spit :enabled?] true)
    (timbre/set-config! [:shared-appender-config :spit-filename] 
                        (:file log-config))
    
    ; Enable/disable stdout logs
    (timbre/set-config! [:appenders :standard-out :enabled?] 
                        (:stdout-enabled log-config))))

(defn start
  "Performs side effects to initialize the system, acquire resources,
  and start it running. Returns an updated instance of the system."
  [system]
  (setup-logging (:config system))
  (assoc-in system [:server] (start-server (:config system))))

(defn stop
  "Performs side effects to shut down the system and release its
  resources. Returns an updated instance of the system."
  [system]
  (stop-server (:server system))
  (assoc system :server nil))