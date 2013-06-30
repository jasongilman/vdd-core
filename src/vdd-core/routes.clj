(ns vdd-core.routes
  (:use [compojure.core :only [defroutes GET]]
        (ring.middleware [keyword-params :only [wrap-keyword-params]]
                         [params :only [wrap-params]]
                         [session :only [wrap-session]])
        [vdd-core.websocket :only [wamp-handler]])
  (:require [compojure.route :as route]
            [clojure.tools.logging :as log]))

;; define mapping here
(defroutes server-routes*
  (GET "/ws" [:as req] (wamp-handler req))
  ;; static files under ./resources/public folder
  (route/resources "/")
  ;; 404, modify for a better 404 page
  (route/not-found "<p>Page not found.</p>"))

(defn wrap-failsafe [handler]
  (fn [req]
    (try (handler req)
      (catch Exception e
        (log/error e "error handling request" req)
        ;; FIXME provide a better page for 500 here
        {:status 500 :body "Sorry, an error occured."}))))

(defn wrap-dir-index
  "Rewrite requests of / to /index.html"
  [handler]
  (fn [req]
    (handler
      (update-in req [:uri]
        #(if (= "/" %) "/index.html" %)))))

(defn app [] (-> #'server-routes*
              wrap-session
              wrap-keyword-params
              wrap-params
              wrap-failsafe
              wrap-dir-index))