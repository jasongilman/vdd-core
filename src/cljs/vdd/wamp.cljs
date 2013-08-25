(ns vdd.wamp
  (:require [vdd.promise])
  (:use [vdd.util :only [log js-obj->map]]))

(def base-topic-uri "http://vdd-core/")
(def connect-options {:maxRetries 60 :retryDelay 30000})

(defn- session-connection-handler 
  "Handles session connections. Configures the session and sets up the vizdata-handler as the function that is called
  when visualization data arrives."
  [channel-config session]
  (log (str "Connected with session " (.sessionid session)))
  ;  Add CURI prefixes
  (.prefix session "event" (str base-topic-uri "event#"))
  (.prefix session "rpc"   (str base-topic-uri "rpc#"))
  (doseq [[channel handler] channel-config]
    (.subscribe session 
                (format "event:%s" 
                        (if (keyword? channel)
                          (name channel)
                          channel))
                handler)))

(defn- disconnect-handler 
  "Handles disconnections from the server."
  [code reason]
  (when (not= 0 code)
    (log ("Connection lost (" reason ")"))))

(defn- channels-to-handlers-or-callback->config 
  "TODO"
  [channels-to-handlers-or-callback]
  (if (fn? channels-to-handlers-or-callback)
    {:vizdata channels-to-handlers-or-callback}
    (js-obj->map channels-to-handlers-or-callback)))

(defn ^:export connect 
  "Starts up a connection to the wamp server using the autobahn js library.
  channels-to-handlers should be either a javascript map of channel names to callback functions
  or a single callback function for normal vizdata.
  Returns a 'session' object that should be used with other operations such as sendData."
  [channels-to-handlers-or-callback]
  (let [channel-config (channels-to-handlers-or-callback->config channels-to-handlers-or-callback)
        ws-uri (format "ws://localhost:%d/ws" (or (:port channel-config) (.-port js/location)))
        session-promise (vdd.promise/promise)
        connection-handler (fn [session]
                             (session-connection-handler channel-config session)
                             (vdd.promise/deliver session-promise session))]
    (.connect js/ab
              ws-uri 
              connection-handler 
              disconnect-handler
              connect-options)
    session-promise))

(defn send-data
  "Sends data from the visualization to the coding environment to allow the visualization to drive the code
  being visualized. The data sent can be any JSON serializable object. Takes the following arguments:
  * session - the session object returned from calling connect.
  * server-data-fn - A string containing the namespace and function to call on server side. ie. 'my-ns/test-code'
  * data - The data to send
  * success - optional callback function with the response data. Defaults to logging responses.
  * error - optional callback function to handle errors. Defaults to logging errors.
  * channel - the channel to send the data on."
  ([session-promise server-data-fn data]
    (send-data session-promise 
               server-data-fn 
               data 
               {:success (fn [data] (log (str "Successfully called and received " data)))
                :error (fn [data] (log (str "Error sending data from viz " data)))
                :channel "data-callback"}))
  ([session-promise 
    server-data-fn 
    data
    {success-handler :success 
     error-handler :error
     channel :channel}]
   (vdd.promise/deref-then 
     session-promise
     (fn [session]
       (if session
         (do 
           (log (format "Sending data %s on channel [%s]" data (str "rpc:" channel)))
           (.then (.call session (str "rpc:" channel) (clj->js {:fn server-data-fn :data data}))
                  success-handler
                  error-handler))
         (log "The session object was nil. We don't appear to be connected.")))
     #(do 
        (log "Timed out waiting for session object while trying to send data")
        (error-handler "timeout")))))

(defn ^:export sendData 
  "Public version of send-data to be called from javascript. It handles javascript options object."
  ([session-promise server-data-fn data]
   (send-data session-promise server-data-fn data))
  ([session-promise server-data-fn data js-options]
   (log (format "Using options %s" js-options))
   (send-data session-promise server-data-fn data (js-obj->map js-options))))

           
