(ns sounds.4-1-2016
  (:use [overtone.live]
        [sounds.common])
  (:require [leipzig.melody :refer [all bpm is phrase then times where with tempo]]
            [leipzig.live :as live]))

(defonce main-g (group "main group"))
(defonce early-g (group "1" :head main-g))
(defonce later-g (group "2" :after early-g))

(defsynth tom [out-bus 0 freq 50]
  (let [env (env-gen (perc) :action FREE)
        src (clip2 (* env (tanh (sin-osc freq))) 0.5)]
    (out out-bus (pan2 src))))

(defsynth bdrum [out-bus 0 attack 0.01 release 0.4 level 1 curve -4]
  (let [env (env-gen (perc attack release level curve) :action FREE)
        src (mix (ringz (white-noise) [50 60 100]))]
    (out out-bus (pan2 (clip2 (* env src) 0.9)))))

(defsynth snare [out-bus 0 attack 0.1 release 0.2 level 1 curve -4]
  (let [env (env-gen (perc attack release level curve) :action FREE)
        src (white-noise)]
    (out out-bus (pan2 (* env src)))))

(defsynth lead [out-bus 0 freq 440 dur 1]
  (let [env (env-gen (adsr) :gate (line:kr 1 0 dur) :action FREE)]
    (out out-bus (pan2 (lpf (* env (square freq)))))))

(defmethod live/play-note :tom [{pitch :pitch}]
  (tom :freq pitch))

(defmethod live/play-note :snare [{attack :pitch}]
  (when attack (snare :attack attack)))

(defmethod live/play-note :bdrum [{attack :pitch}]
  (when attack (bdrum :release 0.2)))

(defmethod live/play-note :lead [{note :pitch dur :duration}]
  (lead :freq (note->hz note) :dur dur))


(def tphrase1 [60 61 60 61 60 61 70])
(def tphrase2 [60 61 60 62 60 63 70 60 59 60 58 60 57 50])

(def toms (->> (phrase (repeat 1/4)
                       tphrase2)
               (tempo (bpm 100))
               (all :part :tom)))

(def bdrum-pattern (phrase (repeat 1/4)
                           [1 nil 1 nil]))

(def bdrum-phrase (->> bdrum-pattern
                       (all :part :bdrum)))

(def snare-phrase (->> (phrase (repeat 1/8)
                               [nil nil nil 0.01
                                nil 0.01 nil 0.01])
                       (all :part :snare)))

(def drums (->> (with bdrum-phrase snare-phrase)
                (tempo (bpm 80/4))))

(comment
  (overtone.studio.scope/scope 0)

  (live/jam (var toms))

  (live/jam (var bdrum-phrase))
  (live/jam (var snare-phrase))
  (live/jam (var drums))
  )
