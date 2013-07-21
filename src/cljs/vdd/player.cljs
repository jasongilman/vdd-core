(ns vdd.player
  (:require-macros [hiccups.core :as hiccups])
  (:require [hiccups.runtime :as hiccupsrt]
            [jayq.core :as jq]
            [vdd.ui.slider :as ui.slider])
  (:use [vdd.util :only [log]]))

; TODO This jams too much all in one file. We should try to abstract away some of it like the 
; button stuff and the slider.

(def ^{:doc "Defines the duration in ms between play framews"}
  duration 200)

(def player-control 
  (hiccups/html 
    [:div.player
     [:div.btn-toolbar 
      [:div.btn-group
       [:a.btn.first {:href "#"}[:i.icon-step-backward]]
       [:a.btn.back {:href "#"}[:i.icon-backward]]
       [:a.btn.play {:href "#"}[:i.icon-play]]
       [:a.btn.pause {:href "#"}[:i.icon-pause]]
       [:a.btn.forward {:href "#"}[:i.icon-forward]]
       [:a.btn.last {:href "#"}[:i.icon-step-forward]]]]
     [:div.slider]]))


(defn- go-to-index
  "Helper function to go to a specific index"
  [player-state-atom new-index-fn]
  (let [player-state @player-state-atom
        {items :items 
         data-handler :data-handler
         item-index :index} player-state
        new-index (new-index-fn item-index)]
    (when (and (>= new-index 0)
               (< new-index (count items))
               (not= new-index item-index))
      (let [item (nth items new-index)]
        (data-handler item)
        (ui.slider/set-value! (:slider player-state) new-index)
        (swap! player-state-atom assoc :index new-index)))))

(defn- play-next-frame 
  "Animates playing and looping through the visualization data"
  [player-state-atom]
  (let [{items :items
         playing :playing} @player-state-atom
        next-index-fn (fn [curr-index]
                        (if (= curr-index (-> items count dec))
                          0
                          (inc curr-index)))]
    (when playing
      (do
        (go-to-index player-state-atom next-index-fn)
        (js/setTimeout (partial play-next-frame player-state-atom) duration)))))

(defn- play 
  "Handles the play button press."
  [player-state-atom]
  (when (not (:playing @player-state-atom))
    (do 
      (swap! player-state-atom assoc :playing true)
      (play-next-frame player-state-atom))))

(defn- pause 
  "Handles the pause button press."
  [player-state-atom]
  (swap! player-state-atom assoc :playing false))

(defn- back 
  "Handles the back button press."
  [player-state-atom]
  (go-to-index player-state-atom dec))

(defn- forward 
  "Handles the forward step button press."
  [player-state-atom]
  (go-to-index player-state-atom inc))

(defn- jump-to-first
  "Handles the jump to first button press"
  [player-state-atom]
  (go-to-index player-state-atom (constantly 0)))

(defn- jump-to-last 
  "Handles the jump to last button press"
  [player-state-atom]
  (go-to-index player-state-atom (constantly 
                                   (-> @player-state-atom :items count dec))))
    
(defn- create-player-state 
  "Creates the initial state of the player control"
  ([slider] 
   (create-player-state slider [] (fn [_] nil)))
  ([slider items data-handler]
   {:items items
    :data-handler data-handler
    :index -1
    :playing false
    :slider slider}))

(defn- player-data-handler 
  "Callback function that will be returned when a player control is constructed. It will
  take the items to play and a data handler to accept each item"
  [player-state-atom items data-handler]
  (let [player-state @player-state-atom
        slider (:slider player-state)
        player-state (create-player-state slider items data-handler)]
    (reset! player-state-atom player-state)
    (jump-to-first player-state-atom)
    ; Set the max state of the slider
    (ui.slider/set-option! slider "max" (-> player-state :items count dec))
    
    (reset! player-state-atom player-state)
    (log "Player data set!")))

(defn- setup-button 
  "Adds a click handler to a button within the player
  Params:
    player - the player element on the page
    state-atom - an atom that references the current player state
    type - the button type. ie :pause
    handler-fn - the function that should be called when the button is clicked."
  [player state-atom type handler-fn]
  (let [btn (.find player (str "a." (name type)))]
    (.click btn (partial handler-fn state-atom))))

(defn- slide 
  "Handles the slider sliding to a new value."
  [state-atom new-index]
  ; The user took control of the slider so make sure we're not playing
  (pause state-atom)
  (go-to-index state-atom (constantly new-index)))
 
(defn- setup-slider
  [player state-atom]
  (let [update-value-fn (partial slide state-atom)]
    (ui.slider/create (.find player "div.slider") update-value-fn)))

(defn ^:export createPlayerFn
  "Creates a player component within the given element and returns a function
  that can be used to set the event data and handler function. Click handlers are assigned
  to all of the buttons in the player component created. The functions have been 
  partially applied to a shared atom that is used to set the state."
  [element]
  (log (str "Creating a player within " element))
  (let [player (jq/append element player-control)
        player-state-atom (atom nil)
        slider (setup-slider player player-state-atom)
        button-types-and-fns {:play play :pause pause :back back :forward forward
                              :first jump-to-first :last jump-to-last}]
    
    (reset! player-state-atom (create-player-state slider))
    ; Add click handlers to the buttons
    (doseq [[btn-type btn-fn] button-types-and-fns]
      (setup-button player player-state-atom btn-type btn-fn))
    
    ; Return a function that will allow setting the data and data-handler function
    (partial player-data-handler player-state-atom)))

