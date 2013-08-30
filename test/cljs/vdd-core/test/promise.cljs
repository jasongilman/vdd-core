(ns vdd-core.test.promise
  (:require-macros [cemerick.cljs.test :refer (is deftest with-test run-tests testing are)])
  (:require [cemerick.cljs.test :as t]
            [vdd-core.promise])
  (:use [vdd-core.util :only [log]]))

(defn set-timeout-mock
  "A mock version of set-timeout. It just immediately calls f."
  [f milliseconds]
  (f))

(deftest test-promise-deref-was-delivered
  (let [p (vdd-core.promise/promise)
        value 5]
    (vdd-core.promise/deliver p value)
    (cljs.core/with-redefs 
      [vdd-core.promise/set-timeout set-timeout-mock]
      (is (= value (vdd-core.promise/deref-then
               p
               identity
               (fn [_] :timedout)))))))

(deftest test-promise-deref-was-not-delivered
  (let [p (vdd-core.promise/promise)
        value 5]
    (cljs.core/with-redefs 
      [vdd-core.util/set-timeout set-timeout-mock]
      (is (= :timedout (vdd-core.promise/deref-then
               p
               identity
               (fn [_] :timedout)))))))
    
