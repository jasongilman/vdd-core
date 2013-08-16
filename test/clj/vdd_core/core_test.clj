(ns vdd-core.core-test
  (:require [clojure.test :refer :all]
            [vdd-core.core :as c]
            [clj-http.client :as client]))

(deftest start-viz
  (let [port (inc (:port (c/config)))
        config (assoc (c/config) :port port)
        server (c/start-viz config)]
    (try
      (is (= 200 (:status(client/get (str "http://localhost:" port)))))
      (finally (c/stop-viz server)))))


