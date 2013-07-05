(ns repl-connect
  (:require [clojure.browser.repl :as repl]))

; Starts a connection to a repl as indicated below. See https://github.com/emezeske/lein-cljsbuild/blob/0.3.2/doc/REPL.md
(repl/connect "http://localhost:9000/repl")
; To use the repl
; terminal1 - lein ring server
; terminal2 - lein trampoline cljsbuild repl-listen
; open browser to http://localhost:3000/
; Use terminal 2 to call namespace methods.
