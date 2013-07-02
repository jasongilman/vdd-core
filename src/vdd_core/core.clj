(ns vdd-core.core
  (:require [vdd-core.internal.system :as vsystem]
            [clj-wamp.server :as wamp]
            [vdd-core.internal.websocket :as vddsocket]))

(defn start-viz
  "Starts the visualization server and returns an instance of it."
  [] 
  (vsystem/start (vsystem/system)))

(defn stop-viz
  "Stops the visualization server"
  [server] 
  (vsystem/stop server))

(defn data->viz
  "Sends the captured data to the visualization on the specified channel"
  [channel data]
  (wamp/send-event! 
    (vddsocket/evt-url channel)
    data))