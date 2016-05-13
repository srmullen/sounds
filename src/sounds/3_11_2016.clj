(ns sounds.3-11-2016
  (:use [overtone.live])
  (:require [sounds.synths.core :as core]))

(defsynth foo [chan 0 freq 440]
  (out chan (decay
             (sin-osc [freq (+ freq (/ 10 freq))]))))

(defsynth foo [chan 0 freq 440]
  (out chan (* (decay) (sin-osc [freq (+ freq (/ 10 freq))]))))

(definst sin-wave [freq 440 attack 0.01 decay 0.3 sustain 0.4 release 0.1 vol 0.4]
  (* (env-gen (lin attack sustain release) 1 1 0 1 FREE)
     (sin-osc freq)
     vol))

(defsynth saw-sine [freq 440]
  (let [freqs (map #(* freq %)
                   (range 1 (+ 9)))
        env (env-gen (perc))]
    (out 0 (pan2 (* env
                    (mix (map sin-osc freqs)))))))

(defsynth sins
  [p1 220 a1 0.3 p2 440 a2 0.3 p3 660 a3 0.3]
  (let [oscs (mix [(* (sin-osc p1) a1)
                   (* (sin-osc p2) a2)
                   (* (sin-osc p3) a3)])]
    (out 0 (pan2 oscs))))
