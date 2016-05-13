(ns sounds.shorts.61015
  (:use [overtone.live])
  (:require [leipzig.live :as live]
            [leipzig.melody :refer :all]
            [sounds.util :as util]))

(defsynth foo [bus 0 freq 440.0 attack 0.01 release 1]
  (out bus (pan2 (* (env-gen (perc attack release) :action FREE)
                    (sin-osc freq)))))

(defmethod live/play-note :default [{freq :pitch}]
  (when freq (foo :freq freq)))

(defn tuplet [nums pitches beats]
  (map #(phrase (take (* %1 %3) (repeat (/ 1 %1)))
                (repeat %2))
       nums pitches beats))

(defn play-tuplet [phrases tmpo]
  (->> (apply with phrases)
       (tempo (bpm tmpo))
       (live/play)))


(def c1 (tuplet [1 2 3 4 5] (map util/note->hz [:c5 :e5 :g5 :c6 :e6]) (repeat 2)))
(def d1 (tuplet [1 2 3 4 5] (map util/note->hz [:c5 :d5 :a5 :d6 :f6]) (repeat 2)))
(def g1 (tuplet [1 2 3 4 5] (map util/note->hz [:b4 :d5 :g5 :d6 :f6]) (repeat 2)))



(comment

  (play-tuplet c1 30)
  (play-tuplet d1 30)
  (play-tuplet g1 30)


  (live/play (with (->> bass
                        (times 2)
                        (tempo (bpm 174)))
                   p1))

  )
