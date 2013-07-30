(ns vdd.wamp
  (:use [vdd.util :only [log]]))


(def base-topic-uri "http://vdd-core/")
(def connect-options {:maxRetries 60 :retryDelay 30000})

(defn- session-connection-handler 
  "Handles session connections. Configures the session and sets up the vizdata-handler as the function that is called
  when visualization data arrives."
  [vizdata-handler session]
  (log (str "Connected with session " (.sessionid session)))
  ;  Add CURI prefixes
  (.prefix session "event" (str base-topic-uri "event#"))
  (.prefix session "rpc"   (str base-topic-uri "rpc#"))
  (.subscribe session "event:vizdata" vizdata-handler))

(defn- disconnect-handler 
  "Handles disconnections from the server."
  [code reason]
  (when (not= 0 code)
    (log ("Connection lost (" reason ")"))))

(defn ^:export connect 
  "Starts up a connection to the wamp server using the autobahn js library.
  vizdata-handler is the function that is called to handle visualization data. Returns a 'session' object that should be used 
  with other operations such as sendData."
  ([vizdata-handler] (connect 8080 vizdata-handler))
  ([port vizdata-handler]
   (let [ws-uri (format "ws://localhost:%d/ws" port)
         session-atom (atom nil)
         connection-handler (fn [session]
                              (session-connection-handler vizdata-handler session)
                              (reset! session-atom session))]
     (.connect js/ab
               ws-uri 
               connection-handler 
               disconnect-handler
               connect-options)
     ; Session atom is returned as a poor man's future for the session. The caller should treat this as the session object.
     session-atom)))

(defn ^:export sendData
  "Sends data from the visualization to the coding environment to allow the visualization to drive the code
  being visualized. The data sent can be any JSON serializable object. Takes the following arguments:
  * session - the session object returned from calling connect.
  * data - The data to send
  * success-handler - optional callback function with the response data. Defaults to logging responses.
  * error-handler - optional callback function to handle errors. Defaults to logging errors."
  ([session-atom data]
   (sendData session-atom data
             (fn [data] (log (str "Successfully called and received " data)))))

  ([session-atom data success-handler]
   (sendData session-atom data success-handler
             (fn [data] (log (str "Error sending data from viz " data)))))

  ([session-atom data success-handler error-handler]
   (let [session @session-atom]
     (if session
       (.then (.call session "rpc:data-callback" data)
              success-handler
              error-handler)
       (log "The session object was nil. We don't appear to be connected.")))))
