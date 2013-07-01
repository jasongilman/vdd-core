(ns vdd-core.system
  (:require [vdd-core.server :as server]
            [vdd-core.config :as config]))

(defn system
  "Returns a new instance of the whole application."
  []
  {:config (config/read-conf)
   :server nil})

(defn start
  "Performs side effects to initialize the system, acquire resources,
  and start it running. Returns an updated instance of the system."
  [system]
  (assoc-in system [:server] (server/start-server (:config system))))

(defn stop
  "Performs side effects to shut down the system and release its
  resources. Returns an updated instance of the system."
  [system]
  (server/stop-server (:server system))
  (assoc system :server nil))