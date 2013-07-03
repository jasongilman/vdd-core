(ns vdd-core.core
  (:require [vdd-core.internal.system :as vsystem]
            [clj-wamp.server :as wamp]
            [clojure.tools.logging :as log]
            [vdd-core.internal.wamp-handler :as wamp-handler]))

(defn config 
  "Creates a new default config map and returns it."
  []
  ; The port the visualization server will run on
  {:port 8080
   ; The directory in your project that custom visualizations will be found in.
   :viz-root "viz"})

(defn start-viz
  "Starts the visualization server and returns an instance of it."
  ([] 
   (start-viz (config)))
  ([config]
   (log/info "Viz Server Starting")
   (vsystem/start (vsystem/system config))))

(defn stop-viz
  "Stops the visualization server"
  [server] 
  (log/info "Viz Server Stopping")
  (vsystem/stop server))

(defn data->viz
  "Sends the captured data to the visualization on the specified channel"
  [channel data]
  (wamp/send-event! 
    (wamp-handler/evt-url channel)
    data))