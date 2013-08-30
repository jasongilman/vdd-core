(ns vdd-core.test.player
  (:require-macros [cemerick.cljs.test :refer (is deftest with-test run-tests testing are)])
  (:require [cemerick.cljs.test :as t]
            [vdd-core.util :as util]
            [vdd-core.ui.slider :as slider]
            [vdd-core.player :as p]))

(defn create-player-state [items]
  (let [items (range 5)
        data-handler (fn [_] nil)
        slider-mock "slider-mock"
        options {:duration 50}]
    (p/create-player-state slider-mock options items data-handler)))

(defn assert-playing [player-state-atom]
  (is (:playing @player-state-atom)))

(defn refute-playing [player-state-atom]
  (is (not (:playing @player-state-atom))))

(defn assert-index [player-state-atom index]
  (is (= index (:index @player-state-atom))))

(defn fail-if-called
  "A function that can be used in place of one that shouldn't be called"
  []
  (throw "should not be called"))

(deftest test-play-from-start 
  (let [player-state-atom (atom (create-player-state (range 5)))]
    (cljs.core/with-redefs
      [util/set-timeout (fn [f ms] (is (= 50 ms)))
       slider/set-value! (constantly nil)]
      (p/play player-state-atom)
      (assert-playing player-state-atom)
      (assert-index player-state-atom 1))))

(deftest test-play-at-end
  (let [items (range 5)
        initial-state (create-player-state items)
        initial-state (assoc initial-state :index (-> items count dec))
        player-state-atom (atom initial-state)]
    (cljs.core/with-redefs
      [util/set-timeout (fn [f ms] (is (= 50 ms)))
       slider/set-value! (constantly nil)]
      (p/play player-state-atom)
      (assert-playing player-state-atom)
      
      ; Should jump back to beginning
      (assert-index player-state-atom 0))))
      
(deftest test-play-when-already-playing
  (let [initial-state (create-player-state (range 5))
        initial-state (assoc initial-state :playing true)
        player-state-atom (atom initial-state)]
    (cljs.core/with-redefs
      [util/set-timeout (constantly nil)
       slider/set-value! fail-if-called]
      (p/play player-state-atom)
      (assert-playing player-state-atom)
      ; index should not have changed
      (assert-index player-state-atom 0))))

(deftest test-pause
  (let [initial-state (assoc (create-player-state (range 5)) :playing true)
        player-state-atom (atom initial-state)]
    (p/pause player-state-atom)
    (refute-playing player-state-atom)))

(deftest test-back-at-beginning
  (let [initial-state (create-player-state (range 5))
        player-state-atom (atom initial-state)]
    (cljs.core/with-redefs
      [slider/set-value! fail-if-called]
      (p/back player-state-atom)
      ;still at beginning
      (assert-index player-state-atom 0))))

(deftest test-back
  (let [initial-state (assoc (create-player-state (range 5)) :index 3)
        player-state-atom (atom initial-state)]
    (cljs.core/with-redefs
      [slider/set-value! (constantly nil)]
      (p/back player-state-atom)
      (assert-index player-state-atom 2))))

(deftest test-forward-at-end
  (let [initial-state (assoc (create-player-state (range 5)) :index 4)
        player-state-atom (atom initial-state)]
    (cljs.core/with-redefs
      [slider/set-value! fail-if-called]
      (p/forward player-state-atom)
      ;still at end
      (assert-index player-state-atom 4))))

(deftest test-forward
  (let [initial-state (assoc (create-player-state (range 5)) :index 2)
        player-state-atom (atom initial-state)]
    (cljs.core/with-redefs
      [slider/set-value! (constantly nil)]
      (p/forward player-state-atom)
      (assert-index player-state-atom 3))))

(deftest test-jump-to-last
  (let [initial-state (assoc (create-player-state (range 5)) :index 2)
        player-state-atom (atom initial-state)]
    (cljs.core/with-redefs
      [slider/set-value! (constantly nil)]
      (p/jump-to-last player-state-atom)
      (assert-index player-state-atom 4))))

(deftest test-jump-to-last-at-end
  (let [initial-state (assoc (create-player-state (range 5)) :index 4)
        player-state-atom (atom initial-state)]
    (cljs.core/with-redefs
      [slider/set-value! fail-if-called]
      (p/jump-to-last player-state-atom)
      (assert-index player-state-atom 4))))

(deftest test-jump-to-first
  (let [initial-state (assoc (create-player-state (range 5)) :index 2)
        player-state-atom (atom initial-state)]
    (cljs.core/with-redefs
      [slider/set-value! (constantly nil)]
      (p/jump-to-first player-state-atom)
      (assert-index player-state-atom 0))))

(deftest test-jump-to-first-at-beginning
  (let [player-state-atom (atom (create-player-state (range 5)))]
    (cljs.core/with-redefs
      [slider/set-value! fail-if-called]
      (p/jump-to-first player-state-atom)
      (assert-index player-state-atom 0))))

