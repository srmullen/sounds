(ns sounds.5-16-2015
  (:use [overtone.live])
  (:require [sounds.util :as util]))



(let [n1 (util/note->hz :c4)
      n2 (util/note->hz :a5)
      notes [n1 n2]
      trig (impulse:kr (line:kr 0 300 5))]
  (run 5 (out 0 (pan2 (sin-osc (demand:kr trig 0 (dseq notes INF)))))))

(defn bf [dur note1 note2 env]
  (let [n1 (util/note->hz note1)
        n2 (util/note->hz note2)
        notes [n1 n2]
        trig (impulse:kr env)
        d dur]
    (run (out 0 (pan2 (sin-osc (demand:kr trig 0 (dseq notes INF))))))))

(def env-bus (audio-bus))

(defsynth imp-mod-line [out-bus 0 from 0 to 0 dur 1 done FREE]
  (out out-bus (line:kr from to dur done)))

(defsynth bfsynth [freq1 440 freq2 880 dur 1 impmod 0]
  (let [notes [freq1 freq2]
        trig (impulse:kr (in:kr impmod))]
    (out 0 (pan2 (sin-osc (demand:kr trig 0 (dseq notes INF)))))))

(defn bf [freq1 freq2 from to dur]
  (let [bus (audio-bus)]
    (imp-mod-line :out-bus bus :from from :to to :dur dur)
    (bfsynth :impmod bus)))
