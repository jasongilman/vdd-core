(ns vdd-core.core
  (:require [vdd-core.internal.system :as vsystem]
            [clj-wamp.server :as wamp]
            [taoensso.timbre :as timbre
             :refer (trace debug info warn error fatal spy)]
            [vdd-core.internal.wamp-handler :as wamp-handler]))

; TODO can we change this to something other than mutable state or at least not put the atom in a var

(def data-callback (atom nil))

(defn- handle-viz-call 
  "Handles rpc calls from the visualization by forwarding to the data callback"
  [data]
  (if-let [callback @data-callback]
    (callback data)))

(defn config 
  "Creates a new default config map and returns it."
  []
  ; The port the visualization server will run on
  {:port 8080
   ; The directory in your project that custom visualizations will be found in.
   :viz-root "viz"
   ; A list of data channels over which visualization data or other data can be sent.
   :data-channels ["vizdata"]
   
   ; A map of rpc urls to functions that will handle requests.
   ; Visualizations can call these RPC functions and get back some data or trigger data to be sent over a data channel 
   :viz-request-handlers {"data-callback" handle-viz-call} 
   
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
  ([data] (data->viz "vizdata" data))
  ([channel data]
    (wamp/send-event! (wamp-handler/evt-url channel) data)))

(defn set-viz-request-callback!
  "Adds a callback that will be invoked when data is sent from the visualization"
  [callback]
  (reset! data-callback callback))
