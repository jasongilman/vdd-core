(ns vdd-core.core
  (:require [vdd-core.internal.system :as vsystem]
            [clj-wamp.server :as wamp]
            [taoensso.timbre :as timbre
             :refer (trace debug info warn error fatal spy)]
            [vdd-core.internal.wamp-handler :as wamp-handler]))

(defn config 
  "Creates a new default config map and returns it."
  []
  ; The port the visualization server will run on
  {:port 8080
   ; The directory in your project that custom visualizations will be found in.
   :viz-root "viz"
   ; Log configuration options
   :log {:level :debug  ; The level to log out
         :file "log/vdd-core.log" ; The path to the file to log to
         :stdout-enabled true}}) ; Whether or not to log to standard out

(defn start-viz
  "Starts the visualization server and returns an instance of it."
  ([] 
   (start-viz (config)))
  ([config]
   (vsystem/start (vsystem/system config))))

(defn stop-viz
  "Stops the visualization server"
  [server] 
  (vsystem/stop server)
  nil)

(defn data->viz
  "Sends the captured data to the visualization on the specified channel"
  [channel data]
  (wamp/send-event! 
    (wamp-handler/evt-url channel)
    data))