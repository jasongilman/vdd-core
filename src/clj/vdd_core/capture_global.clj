(ns vdd-core.capture-global)

; This is global mutable state. Not ideal. 
; This could be refactored later to allow storage of captured data 
; for various purposes
(def ^:private _captured (atom []))

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
  (println "Capturing " data)
  (swap! _captured concat [data]))