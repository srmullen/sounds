(ns sounds.parnassus.first-species
  (:require [sounds.instruments :as instruments]
            [overtone.live :as overtone]
            [leipzig.live :as live]
            [leipzig.scale :as scale]
            [leipzig.chord :as chord]
            [leipzig.melody :refer :all]))

(defonce church-bus (overtone/audio-bus))

(def church (instruments/room :in-bus church-bus :out-bus 0 :mix 1 :room 0.5 :damp 1))

(defmethod live/play-note :default [{midi :pitch seconds :duration velocity :velocity}]
  (when midi (instruments/lead [:before church]
                               :bus church-bus
                               :freq (overtone/midi->hz midi)
                               :dur seconds
                               :rq 1
                               :amp (if velocity velocity 1))))

(defn talea [length]
  (conj (vec (take (- length 1) (repeat 1))) 2))

(def talea1 (talea 11))
(def talea2 (talea 10))
(def talea3 (talea 12))

(def cantus-firmus1 (phrase talea1
                            [0 2 1 0 3 2 4 3 2 1 0]))

(def cp1-upper (phrase talea1
                       [4 4 3 4 5 6 6 5 7 6.5 7]))

(def cp1-lower (phrase talea1
                       [0 0 4 2 1 0 2 6 7 6.5 7]))

(def cantus-firmus2 (phrase talea2
                            [0 -2 -1 -2 -4 3 2 0 1 0]))

(def cp2-upper (phrase talea2
                       [4 5 1 2 3 5 4 7 6 7]))

(def cp2-lower (phrase talea2
                       [0 3 -1 0 1 1 5 5 6 7]))

(def cantus-firmus3 (phrase talea3
                            [0 1 2 0 -2 -1 0 4 2 0 1 0]))

(def cp3-upper (phrase talea3
                       [0 -1 -3 0 0 1 2 1 -3 0 -1 0]))

(def cp3-lower (phrase talea3
                       [0 -1 0 2 2.5 1 2 -1 0 -2 -1 0]))

(def cantus-firmus4 (phrase (talea 14)
                            [0 3 2 0 3 5 4 7 5 3 4 2 1 0]))

(def cp4-upper (phrase (talea 14)
                       [0 -2 -3 0 0 0 1 2 0 3 1 0 -0.5 0]))

(def cp4-lower (phrase (talea 14)
                       [0 1 0 -2 -2 -4 0 2 3 1 -0.5 0 -0.5 0]))

(def cantus-firmus5 (phrase (talea 12)
                            [0 2 1 3 2 4 5 4 3 2 1 0]))

(def cp5-upper (phrase (talea 12)
                       [0 -3 -1 -2 -3 2 0 1 1 0 -0.5 0]))

(def cp5-lower (phrase (talea 12)
                       [0 0 -1 -2 -3 -3 -4 -5 -1 0 -0.5 0]))

(def d-dorian (comp scale/D scale/dorian))
(def e-phrygian (comp scale/E scale/phrygian))
(def f-lydian (comp scale/F scale/lydian))
(def g-mixolydian (comp scale/G scale/mixolydian))
(def a-aeolian (comp scale/A scale/aeolian))
(def c-ionian (comp scale/C scale/ionian))

(defn play-phrase [phrase scale]
  (->> phrase (where :pitch scale) (tempo (bpm 60)) (live/play)))

(defn two-voices
  ([voice1 scale1 voice2 scale2]
     (->> (with (->> voice1 (where :pitch scale1))
                (->> voice2 (where :pitch scale2)))
          (tempo (bpm 60))
          (live/play)))
  ([voice1 voice2 scale]
     (->> (with (->> voice1 (where :pitch scale))
                (->> voice2 (where :pitch scale)))
          (tempo (bpm 60))
          (live/play))))

(comment

  (two-voices cp1-upper cantus-firmus1 d-dorian)

  (two-voices cp1-lower (comp scale/lower d-dorian) cantus-firmus1 d-dorian)

  (two-voices cp2-upper cantus-firmus2 e-phrygian)

  (two-voices cp2-lower (comp scale/lower e-phrygian) cantus-firmus2 e-phrygian)

  (two-voices cp3-upper f-lydian cantus-firmus3 (comp scale/lower f-lydian))

  (two-voices cp3-lower cantus-firmus3 (comp scale/lower f-lydian))

  ; this doesn't sound right
  (two-voices cp4-upper g-mixolydian cantus-firmus4 (comp scale/lower g-mixolydian))

  (two-voices cp4-lower (comp scale/lower g-mixolydian) cantus-firmus4 g-mixolydian)

  (two-voices cp5-upper a-aeolian cantus-firmus5 a-aeolian)

  (two-voices cp5-lower (comp scale/lower a-aeolian) cantus-firmus5 a-aeolian)
  )
