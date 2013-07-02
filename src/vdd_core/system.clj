(ns vdd-core.system
  (:require [org.httpkit.server :as httpkit]
            [ring.middleware.reload :as ring-reload]
            [clojure.java.io :as io]
            [vdd-core.routes :as routes])
  (:use [clojure.tools.logging :only [info]]))

(defn- stop-server [server]
  (when-not (nil? server)
    (info "stopping server...")
    ; contains the stop-server callback fn
    (server))) 

(defn- start-server [config]
  (let [the-app (if (:hot-reload config) 
                  (ring-reload/wrap-reload (routes/app config)) 
                  (routes/app config))
        http-config (:http-kit config)
        server (httpkit/run-server the-app http-config)]
    (info "server started. listen on" (:ip http-config) "@" (:port http-config))
    server))

(defn- read-conf
  "returns a parsed application config.clj from a resource directory"
  []
  (let [path (io/resource "config.clj")]
    (info "Reading configuration from" path)
    (read-string (slurp path))))

(defn system
  "Returns a new instance of the whole application."
  []
  {:config (read-conf)
   :server nil})

(defn start
  "Performs side effects to initialize the system, acquire resources,
  and start it running. Returns an updated instance of the system."
  [system]
  (assoc-in system [:server] (start-server (:config system))))

(defn stop
  "Performs side effects to shut down the system and release its
  resources. Returns an updated instance of the system."
  [system]
  (stop-server (:server system))
  (assoc system :server nil))