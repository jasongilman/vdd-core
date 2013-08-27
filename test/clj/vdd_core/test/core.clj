(ns vdd-core.test.core
  (:require [clojure.test :refer :all]
            [vdd-core.core :as c]
            [taoensso.timbre :as timbre]
            [clj-http.client :as client]))

(deftest start-viz
  (let [port (inc (:port (c/config)))
        config (assoc (c/config) :port port)
        server (c/start-viz config)]
    (try
      (is (= 200 (:status (client/get (str "http://localhost:" port)))))
      (finally (c/stop-viz server)))))


(def saved-data (atom nil))

(defn test-data-handler [data]
  (reset! saved-data data))

(defn clear-saved-data []
  (reset! saved-data nil))

(defn set-stdout-logging! [enabled]
  (timbre/set-config! [:appenders :standard-out :enabled?] enabled))

(use-fixtures :each (fn [f]
                      (clear-saved-data)
                      (set-stdout-logging! false)
                      (f)
                      (set-stdout-logging! true)))

(deftest handle-viz-call
  (testing "success"
    (#'c/handle-viz-call {"fn" "vdd-core.test.core/test-data-handler" "data" 5})
    (is (= 5 @saved-data)))
  (testing "error"
    (clear-saved-data)
    (#'c/handle-viz-call {"fn" "vdd-core.core-test/does-not-exist" "data" 5})
    (is (= nil @saved-data))))