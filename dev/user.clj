(ns user
  (:require [clojure.pprint :refer (pprint pp)]
            [clojure.test :refer (run-all-tests)]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [vdd-core.internal.system :as system]
            [vdd-core.core :as core])
  (:use [clojure.repl]))

; See http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded 
; for information on why this file is setup this way

(def system nil)

(defn start
  "Starts the current development system."
  []
  (alter-var-root #'system 
                  (constantly (core/start-viz (core/config)))))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'system
    (fn [s] (when s (system/stop s)))))

(defn reset []
  ; Stops the running code
  (stop)
  ; Refreshes all of the code and then restarts the system
  (refresh :after 'user/start))

(println "Custom user.clj loaded.")