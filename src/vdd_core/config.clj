(ns vdd-core.config
  (:use [clojure.tools.logging :only [info]])
  (:require [clojure.java.io :as io]))

(defn read-conf
  "returns a parsed application config.clj from a resource directory"
  []
  (let [path (io/resource "config.clj")]
    (info "Reading configuration from" path)
    (read-string (slurp path))))
