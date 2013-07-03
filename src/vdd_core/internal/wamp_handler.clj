(ns vdd-core.internal.wamp-handler
  (:require [clojure.tools.logging :as log]
            [org.httpkit.server :as http-kit]
            [clj-wamp.server :as wamp]))

;; Topic BaseUrls
(def base-url "http://vdd-core")
(def rpc-base-url (str base-url "/rpc#"))
(def evt-base-url (str base-url "/event#"))

(defn rpc-url [path] (str rpc-base-url path))
(defn evt-url [path] (str evt-base-url path))

;; HTTP Kit/WAMP WebSocket handler

(defn- on-open [sess-id]
  (log/debug "WAMP client connected [" sess-id "]"))

(defn- on-close [sess-id status]
  (log/debug "WAMP client disconnected [" sess-id "] " status))

(defn- on-publish [sess-id topic event exclude include]
  (log/debug "WAMP publish:" sess-id topic event exclude include))

(defn- on-before-call [sess-id topic call-id call-params]
  (log/debug "WAMP call:" sess-id topic call-id call-params)
  [sess-id topic call-id call-params])

(defn handler
  "Returns a http-kit websocket handler with wamp subprotocol"
  [config req]
  (let [ws-origins-re (re-pattern (str "http://localhost:" (:port config)))]
    (wamp/with-channel-validation req channel ws-origins-re
      (wamp/http-kit-handler channel
        {:on-open        on-open
         :on-close       on-close
         :on-call        {(rpc-url "echo")  identity
                          (rpc-url "throw") (fn [] (throw (Exception. "An exception")))
                          :on-before        on-before-call}
         :on-subscribe   {(evt-url "vizdata")  true}
         :on-publish     {(evt-url "vizdata")  true
                          :on-after         on-publish}}))))


