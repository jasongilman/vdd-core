(ns vdd-core.internal.routes
  (:use [compojure.core :only [defroutes routes GET]]
        (ring.middleware [keyword-params :only [wrap-keyword-params]]
                         [params :only [wrap-params]]
                         [session :only [wrap-session]]))
  (:require [compojure.route :as route]
            [vdd-core.internal.wamp-handler :as wamp-handler]
            [clojure.tools.logging :as log]))

;; define mapping here
(defn make-routes [config]
  (routes
    (GET "/ws" [:as req] (wamp-handler/handler config req))
    ;; static files under ./resources/public folder
    (route/resources "/")
    (route/files "/viz" {:root (:viz-root config)})
    ;; 404, modify for a better 404 page
    (route/not-found "<p>Page not found.</p>")))
  

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

(defn app [config] 
  (-> (make-routes config)
      wrap-session
      wrap-keyword-params
      wrap-params
      wrap-failsafe
      wrap-dir-index))