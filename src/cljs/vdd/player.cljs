(ns vdd.player
  (:require-macros [hiccups.core :as hiccups])
  (:require [hiccups.runtime :as hiccupsrt]
            [jayq.core :as jq])
  (:use [vdd.util :only [log]]))

(def player-control 
  (hiccups/html [:div.btn-toolbar 
         [:div.btn-group
          [:a.btn.back {:href "#"}[:i.icon-backward]]
          [:a.btn.play {:href "#"}[:i.icon-play]]
          [:a.btn.pause {:href "#"}[:i.icon-pause]]
          [:a.btn.forward {:href "#"}[:i.icon-forward]]]]))

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

(defn- back 
  "Handles the back button press."
  [player-state items data-handler item-index]
  (when (> item-index 0)
    (let [new-index (dec item-index)
          item (nth items new-index)]
      (data-handler item)
      (assoc player-state :index new-index))))

(defn- forward 
  "Handles the forware button press."
  [player-state items data-handler item-index]
  (when (< item-index (dec (count items)))
    (let [new-index (inc item-index)
          item (nth items new-index)]
      (data-handler item)
      (assoc player-state :index new-index))))

(defn- player-data-handler 
  "Callback function that will be returned when a player control is constructed. It will
  take the items to play and a data handler to accept each item"
  [player-state-atom items data-handler]
  (reset! player-state-atom (create-player-state items data-handler))
  (log "Player data set!"))

(defn- create-player-state 
  "Creates the initial state of the player control"
  ([] 
   (create-player-state [] (fn [_] nil)))
  ([items data-handler]
   {:items items
    :data-handler data-handler
    :index -1
    :playing true}))

(defn ^:export createPlayerFn
  "Creates a player component within the given element and returns a function
  that can be used to set the event data and handler function. Click handlers are assigned
  to all of the buttons in the player component created. The functions have been 
  partially applied to a shared atom that is used to set the state."
  [element]
  (log (str "Creating a player within " element))
  (let [player (jq/append element player-control)
        play-btn (.find player "a.play")
        pause-btn (.find player "a.pause")
        back-btn (.find player "a.back")
        forward-btn (.find player "a.forward")
        player-state-atom (atom (create-player-state))]
    
    (add-watch player-state-atom :log 
               (fn [k a o n]
                 (log (str "Player state set to " n))))
    
    (.click play-btn    (partial button-state-helper player-state-atom play))
    (.click pause-btn   (partial button-state-helper player-state-atom pause))
    (.click back-btn    (partial button-state-helper player-state-atom back))
    (.click forward-btn (partial button-state-helper player-state-atom forward))
    (partial player-data-handler player-state-atom)))

