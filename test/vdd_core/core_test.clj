(ns vdd-core.core-test
  (:require [clojure.test :refer :all]
            [vdd-core.core :as c]
            [clj-http.client :as client]))

(deftest start-viz
  (testing "Is available on default port when started"
    (let [port (:port (c/config))
          server (c/start-viz)]
      (try
        (is (= 200 (:status(client/get (str "http://localhost:" port)))))
        (finally (c/stop-viz server)))))
  
  (testing "Is available on specified port when started"
    (let [config (update-in (c/config) [:port] inc)
          server (c/start-viz config)]
      (try
        (is (= 200 (:status(client/get (str "http://localhost:" (:port config))))))
        (finally (c/stop-viz server))))))
