(ns sounds.aus-meines-herzens-grunde
  (:use [overtone.live]
        [sounds.common])
  (:require [leipzig.melody :refer [all bpm is phrase then times where with tempo]]
            [leipzig.live :as live]))

;; instruments
(defsynth lead [out-bus 0 freq 440 dur 1]
  (let [env (env-gen (adsr) :gate (line:kr 1 0 dur) :action FREE)]
    (out out-bus (pan2 (lpf (* env (square freq)))))))

;; Aus Meines Herzens Grunde
(def s1 (phrase [1/4 2/4 1/4 (dot 1/4) 1/8 1/4 (dot 1/4) 1/8 1/4 1/2 1/4 1/2 1/4 1/4 1/2 1/2]
                [:g4 :g4 :d5 :b4       :a4 :g4 :g4       :a4 :b4 :a4 :b4 :d5 :c5 :b4 :a4 :g4]))

(def a1 (phrase [1/4 1/4 1/4 1/4 1/2 1/4 1/8 1/8 1/8 1/8  1/4 1/2  1/4 1/4 1/4 1/4  1/2 1/4  1/2]
                [:d4 :d4 :e4 :d4 :d4 :b3 :e4 :d4 :e4 :f#4 :g4 :f#4 :g4 :d4 :e4 :f#4 :g4 :f#4 :d4]))

(def t1 (phrase [1/4 1/4 1/8 1/8 1/4 1/4 1/4  1/4 1/8 1/8 1/4 1/4 1/2 1/4 1/4 1/4 1/4 1/4 1/4 1/8 1/8 1/2]
                [:b3 :b3 :c4 :b3 :a3 :g3 :f#3 :g3 :c4 :b3 :c4 :d4 :d4 :d4 :a3 :b3 :c4 :d4 :e4 :d4 :c4 :b3]))

(def b1 (phrase [1/4 1/4 1/4 1/4  1/4 1/4 1/4 1/4 1/8 1/8 1/4 1/2 1/4 1/4  1/4 1/4 1/4 1/4 1/4 1/2]
                [:g2 :g3 :e3 :f#3 :g3 :d3 :e3 :c3 :b2 :a2 :g2 :d3 :g2 :f#2 :g2 :a2 :b2 :c3 :d3 :g2]))


(comment
  (live/play (->> (with s1 a1 t1 b1)
                  (tempo (bpm 20))
                  (all :part :lead)))
  )
