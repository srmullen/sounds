(ns sounds.beats
  (:use [overtone.live])
  (:require [leipzig.live :as live]
            [leipzig.melody :as m]))

(def kick (sample "samples/131336__kaonaya__kick-hiphop-1.wav"))

(defmethod live/play-note :kick [{amp :velocity}]
  (if amp
    (kick :amp amp)
    (kick)))

(def kicks (m/all :part :kick (m/rhythm [1])))
(def groove (atom kicks))
(comment
  ; start the jam
  (live/jam groove)

  ; kick up the beat
  (swap! groove (fn [current] (m/tempo (m/bpm 180) current)))

  ; increase the volume
  (swap! groove (fn [current] (m/all :velocity 2 current)))

  (swap! groove (fn [current] (m/all :part :kick (m/rhythm [2 1/2]))))
  )
