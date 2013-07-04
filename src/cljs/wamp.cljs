(ns vdd.wamp)

(def base-topic-uri "http://vdd-core/")
(def connect-options {:maxRetries 60 :retryDelay 30000})

(defn log [str]
  (.log js/console str))

(defn- connection-handler 
  "Handles connection callbacks. Sets up the vizdata-handler as the function that is called
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
  vizdata-handler is the function that is called to handle visualization data."
  ([vizdata-handler] (connect 8080 vizdata-handler))
  ([port vizdata-handler]
   (let [ws-uri (format "ws://localhost:%d/ws" port)
         conn-handler (partial connection-handler vizdata-handler)]
     (.connect js/ab ws-uri conn-handler disconnect-handler connect-options))))