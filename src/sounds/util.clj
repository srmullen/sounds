(ns sounds.util
  (:use [overtone.live]))

(defn note->hz [music-note]
  (midi->hz (note music-note)))
