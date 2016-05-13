(ns sounds.shorts.time-clash
  (:use [overtone.live])
  (:require [leipzig.live :as live]
            [leipzig.melody :refer :all]
            [leipzig.scale :as scale]
            [leipzig.chord :as chord]
            [sounds.util :as util]))

(defsynth down [freq 440]
  (out 0 (pan2 (* (env-gen (perc)) (sin-osc freq)))))

(defsynth up [freq 440]
  (out 0 (pan2 (* (env-gen (perc)) (saw freq)))))

(defn sig [n d]
  (phrase (repeat n (/ 1 d)) (repeat 440)))
