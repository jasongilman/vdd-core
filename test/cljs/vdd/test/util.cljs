(ns vdd.test.util
  (:require-macros [cemerick.cljs.test :refer (is deftest with-test run-tests testing are)])
  (:require [cemerick.cljs.test :as t])
  (:use [vdd.util :only [js-obj->map]]))

(comment 
  (js-obj->map (js/JSON.parse "{\"test\":5}"))
  (t/test-ns 'vdd.test.util)
)


(deftest test-js-obj->map
  (are [json clj-obj] (= clj-obj (js-obj->map (js/JSON.parse json)))
      "{\"a\":5}" {:a 5}))
