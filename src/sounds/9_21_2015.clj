(ns sounds.9-21-2015
  (:use [overtone.live]
        [sounds.percussion])
  (:require [sounds.instruments :as instruments]
            [sounds.interval :as interval]
            [leipzig.live :as live]
            [leipzig.scale :as scale]
            [leipzig.chord :as chord]
            [leipzig.melody :refer :all]))

(defmethod live/play-note :default [{midi :pitch seconds :duration velocity :velocity}]
  (when midi (instruments/sin-synth :freq (midi->hz midi)
                               :dur seconds
                               :rq 1
                               :amp (if velocity velocity 1))))


(def gamelan-voice-buf (load-sample "~/samples/gamelanvoice.wav"))
(def mocopat-buf (load-sample "~/samples/mocopat.wav"))

(defonce church-bus (audio-bus))

(def church (instruments/room :in-bus church-bus :out-bus 0 :mix 1 :room 0.5 :damp 1))

(defsynth gamelan-voice [bus 0 rate 1 loop 1 amp 1 attack 0 decay 0 sustain 1 release 0 dur 1]
  (let [env (env-gen (adsr attack decay sustain release) (line:kr 1 0 dur) 1 0 1 FREE)]
    (out bus (pan2 (* amp
                      env
                      (play-buf 1 gamelan-voice-buf :rate rate :loop loop))))))

(defsynth mocopat [bus 0 rate 1 loop 0 start-pos 0]
  (out bus (pan2 (play-buf 1 mocopat-buf :loop loop :rate rate :start-pos start-pos))))

(def cmaj (comp scale/C scale/major))

(def theme (phrase [1/8 1/8 1/8 3/8] [3 1 0 1]))

(def rate (bpm 60))

(defmethod live/play-note :kick [{duration :duration}]
  (kick))

(defmethod live/play-note :snare [{duration :duration freq :pitch}]
  (snare :freq freq))

(defmethod live/play-note :voice [{duration :duration degree :pitch}]
  (gamelan-voice :dur duration :rate (interval/chromatic-ratio degree)))


(comment
  (->> theme (where :pitch cmaj) (tempo (bpm 30)) live/play)
  (->> (rhythm (repeat 2)) (all :part :kick) live/play)
  )

(def groove (atom []))

(comment
  (let [every-other (rhythm [2 2])
       kicks (->> every-other (all :part :kick))
       one-beat (rhythm [1])
       snares (->> every-other (all :part :snare))]
    (->> (fn [a] (->> kicks (with (->> one-beat (then snares))))) (swap! groove) live/jam))
  )
