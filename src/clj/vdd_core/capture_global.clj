(ns vdd-core.capture-global
  (:require [taoensso.timbre :as timbre :refer (debug)]
            [vdd-core.core]))


; This is global mutable state. Not ideal. 
; This could be refactored later to allow storage of captured data 
; for various purposes
(def ^:private _captured (atom []))

(def enabled (atom false))

(defn enable
  "Enables capturing of data. This should only be done in development mode.
  Initially enabled is false so no data will be captured."
  ([]
   (enable true))
  ([do-enable]
   (reset! enabled do-enable)))

(defn captured 
  "Returns the data that was captured."
  []
  @_captured)

(defn reset-captured! 
  "Resets the internal captured state"
  []
  (reset! _captured []))

(defn capture!
  "Captures data for visualization."
  [data]
  (when @enabled
    (debug "Capturing " data)
    (swap! _captured concat [data])))