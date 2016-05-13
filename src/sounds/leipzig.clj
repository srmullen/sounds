(ns sounds.leipzig
  (:require [sounds.instruments :as instruments]
            [overtone.live :as overtone]
            [leipzig.live :as live]
            [leipzig.scale :as scale]
            [leipzig.chord :as chord]
            [leipzig.melody :refer :all]))

(def bass
  (->> (phrase [1  1 2]
               [0 -3 0])
       (all :part :bass)))

(def roomy-bus (overtone/audio-bus))

(def room (instruments/room :in-bus roomy-bus :out-bus 0 :mix 1 :room 1 :damp 1))

(defmethod live/play-note :default [{midi :pitch seconds :duration}]
  (when midi (instruments/lead :bus 0 :freq (overtone/midi->hz midi) :dur seconds)))

(defmethod live/play-note :bass [{midi :pitch}]
  (when midi (instruments/sin-synth :freq (-> midi overtone/midi->hz (/ 2)) :dur 0.5)))

(defmethod live/play-note :chords [{midi :pitch}]
  (when midi (instruments/beep :freq (overtone/midi->hz midi))))

(def theme (phrase [1 2 1 2 1 2 1 2]
                   [0 1 2 nil 4 5 6 7]))


(defn play-phrase [phrase tmpo]
  (->> phrase
      (tempo (bpm tmpo))
      (where :pitch (comp scale/D scale/major))
      (live/play)))

(defn groove [part]
  (let [r (->> part
               (tempo (bpm 90))
               (where :pitch (comp scale/C scale/major))
               ref)]
    (live/jam r)
    r))

(comment
  (def chords
    (->> (phrase (repeat 1/2)
                 [nil chord/triad
                  nil (-> chord/seventh (chord/root 4) (chord/inversion 1) (dissoc :v))
                  nil chord/triad
                  nil chord/triad])
         (all :part :chords)))

  (->>
   (times 2 chords)
   (wherever :pitch :pitch scale/lower)
   (tempo (bpm 90))
   (where :pitch (comp scale/C scale/major))
   (live/play))
  )
