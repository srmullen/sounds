(ns sounds.core
  (:use [overtone.live])
  (:require [clojure.core.reducers :as r]))

(defsynth foo
  "sin"
  [freq 440]
  (sin-osc freq))

(demo 7 (lpf (mix (saw [50 (line 100 1600 5) 101 100.5]))
             (lin-lin (lf-tri (line 2 20 5)) -1 1 400 4000)))

(definst sin-wave [freq 400 attack 0.01 sustain 0.4 release 0.1 vol 0.4]
  (* (env-gen (lin attack sustain release) 1 1 0 1 FREE)
     (sin-osc freq)
     vol))

(definst trem [freq 440 depth 10 rate 6 length 3]
  (* 0.3
     (line:kr 0 1 length FREE)
     (saw (+ freq (* depth (sin-osc:kr rate))))))

(definst full-sin [freq 440 vol 0.4 length 1]
  (* vol
     (line:kr 1 1 length FREE)
     (+ (sin-osc freq)
        (* 0.5 (sin-osc (+ freq (/ freq 2))))
        (* 0.25(sin-osc (+ freq (/ freq 3))))
        (* 0.125 (sin-osc (+ freq (/ freq 4))))
        (* 0.1 (sin-osc (+ freq (/ freq 5)))))))

(definst saw-wave [freq 440 attack 0.01 sustain 0.4 release 0.1 vol 0.4]
  (* (env-gen (lin attack sustain release) 1 1 0 1 FREE)
     (saw freq)
     vol))

(definst square-wave [freq 440 attack 0.01 sustain 0.4 release 0.1 vol 0.4]
  (* (env-gen (lin attack sustain release) 1 1 0 1 FREE)
     (square freq)
     vol))

(definst noisy [freq 440 attack 0.01 sustain 0.4 release 0.1 vol 0.4]
  (* (env-gen (lin attack sustain release) 1 1 0 1 FREE)
     (pink-noise)
     vol))

(definst triangle-wave [freq 440 attack 0.01 sustain 0.4 release 0.1 vol 0.4]
  (* (env-gen (lin attack sustain release) 1 1 0 1 FREE)
     (lf-tri freq)
     vol))

(def kick (sample (freesound-path 2086)))

(def one-twenty-bpm (metronome 120))

(defn looper [nome sound]
  (let [beat (nome)]
    (at (nome beat) (sound))
    (apply-by (nome (inc beat)) looper nome sound [])))

(defn note->hz [music-note]
  (midi->hz (note music-note)))

(defn play-seq
  "Play the sequence of frequencies s with the given instrument i"
  [s i]
  (let [t (now)
        dur-ms 1000
        time-multipliers (range (count s))]
    (doseq [[time fr]
            (zipmap (map #(* dur-ms %) time-multipliers) s)]
      (at (+ t time) (i :freq fr :sustain 1.5)))))


(defn sequencer
  "@s a vector of vectors"
  [s]
  (let [t (now)]
    (map (fn [[ts i]] ) s)))


(looper nome #(sin-wave :sustain 0.01 :freq (rand 1000)))



