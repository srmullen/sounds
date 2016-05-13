(ns sounds.common
  (:use [overtone.live]))

(defn note->hz [music-note]
  (midi->hz (note music-note)))

(defn rate->dur
  "Converts a rate-per-second to duration in seconds"
  [rate]
  (/ (/ 60 rate) 60))

(defn dot
  "dots a durations"
  [dur]
  (+ dur (/ dur 2)))
