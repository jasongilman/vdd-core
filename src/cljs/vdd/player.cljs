(ns vdd.player
  (:require-macros [hiccups.core :as hiccups])
  (:require [hiccups.runtime :as hiccupsrt]
            [jayq.core :as jq])
  (:use [vdd.util :only [log]]))

(def player-control 
  (hiccups/html [:div.btn-toolbar 
         [:div.btn-group
          [:a.btn.first {:href "#"}[:i.icon-step-backward]]
          [:a.btn.back {:href "#"}[:i.icon-backward]]
          [:a.btn.play {:href "#"}[:i.icon-play]]
          [:a.btn.pause {:href "#"}[:i.icon-pause]]
          [:a.btn.forward {:href "#"}[:i.icon-forward]]
          [:a.btn.last {:href "#"}[:i.icon-step-forward]]]]))

(defn- button-state-helper 
  "A helper function that will unwrap the state of the player state atom and invoke the 
  given button function."
  [player-state-atom button-fn]
  (let [player-state @player-state-atom
        items (:items player-state)
        data-handler (:data-handler player-state)
        item-index (:index player-state)
        new-state (button-fn player-state items data-handler item-index)]
    (when new-state (reset! player-state-atom new-state))))


(defn- play 
  "Handles the play button press."
  [player-state items data-handler item-index]
  )

(defn- pause 
  "Handles the pause button press."
  [player-state items data-handler item-index]
  )

(defn- go-to-index
  "Helper function to go to a specific index"
  [player-state items data-handler new-index]
    (let [item (nth items new-index)]
      (data-handler item)
      (assoc player-state :index new-index)))

(defn- back 
  "Handles the back button press."
  [player-state items data-handler item-index]
  (when (> item-index 0)
    (go-to-index player-state items data-handler (dec item-index))))

(defn- forward 
  "Handles the forward step button press."
  [player-state items data-handler item-index]
  (when (< item-index (-> items count dec))
    (go-to-index player-state items data-handler (inc item-index))))

(defn- jump-to-first
  "Handles the jump to first button press"
  [player-state items data-handler item-index]
  (when (> item-index 0)
    (go-to-index player-state items data-handler 0)))

(defn- jump-to-last 
  "Handles the jump to last button press"
  [player-state items data-handler item-index]
  (when (< item-index (-> items count dec))
    (go-to-index player-state items data-handler (-> items count dec))))
    
  

(defn- create-player-state 
  "Creates the initial state of the player control"
  ([] 
   (create-player-state [] (fn [_] nil)))
  ([items data-handler]
   {:items items
    :data-handler data-handler
    :index 0
    :playing true}))

(defn- player-data-handler 
  "Callback function that will be returned when a player control is constructed. It will
  take the items to play and a data handler to accept each item"
  [player-state-atom items data-handler]
  (reset! player-state-atom (create-player-state items data-handler))
  (when (first items) (data-handler (first items)))
  (log "Player data set!"))

(defn- setup-button [player state-atom type handler-fn]
  (let [btn (.find player (str "a." (name type)))
        btn-click-handler (partial button-state-helper state-atom handler-fn)]
    (.click btn btn-click-handler)))

(defn ^:export createPlayerFn
  "Creates a player component within the given element and returns a function
  that can be used to set the event data and handler function. Click handlers are assigned
  to all of the buttons in the player component created. The functions have been 
  partially applied to a shared atom that is used to set the state."
  [element]
  (log (str "Creating a player within " element))
  (let [player (jq/append element player-control)
        player-state-atom (atom (create-player-state))
        button-types-and-fns {:play play :pause pause :back back :forward forward
                              :first jump-to-first :last jump-to-last}]
    (doseq [[btn-type btn-fn] button-types-and-fns]
      (setup-button player player-state-atom btn-type btn-fn))
    (partial player-data-handler player-state-atom)))

