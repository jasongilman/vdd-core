(ns vdd-core.test.util
  (:require-macros [cemerick.cljs.test :refer (is deftest with-test run-tests testing are)])
  (:require [cemerick.cljs.test :as t]
            [vdd-core.util :as util]))

(deftest test-js-obj->map
  (are [json clj-obj] (= clj-obj (util/js-obj->map (js/JSON.parse json)))
      "{\"a\":5}" {:a 5}
      "{\"a\":5,\"b\":7}" {:a 5 :b 7}))
