(ns sounds.a-child-is-born
  (:use [overtone.live]
        [sounds.common])
  (:require [leipzig.melody
             :refer [accelerando all bpm is phrase then times where with tempo]]
            [leipzig.chord :as chord]
            [leipzig.live :as live]))

(defsynth lead [freq 440 attack 0.01 sustain 1 release 0.2 dur 1]
  (let [env (env-gen (asr attack sustain release) :gate (line:kr 1 0 dur) :action FREE)]
    (out 0
         (pan2 (* env (lpf (mix (var-saw [freq (* freq
                                                  (line:kr 1.5 1.006 (/ dur 4)))]))
                           (* 20 freq)))))))

(defsynth bass [freq 40 dur 1]
  (let [env (env-gen (asr) :gate (line:kr 1 0 dur) :action FREE)]
    (out 0 (pan2 (* env (sin-osc freq))))))

(defsynth chord-drone [f1 220 f2 440 f3 880 gate 1]
  (let [src (mix (b-band-pass (white-noise)
                              [(lag f1) (lag f2) (lag f3)]
                              [0.01 0.01 0.01]))
        env (env-gen (asr) :gate gate :action FREE)]
    (out 0 (pan2 (normalizer src)))))

;; 3/4 time, key of Bmaj
;; A section, B section, turn-around, coda

(defmethod live/play-note :lead [{pitch :pitch dur :duration}]
  (when pitch (lead (note->hz pitch) :dur dur)))

(defmethod live/play-note :bass [{pitch :pitch dur :duration}]
  (when pitch (bass (note->hz pitch) :dur dur)))

(def basic-rhythm [3/4 1/4  1/4  1/4])

(def p1 (phrase basic-rhythm
                [:d4 :eb4 :f4 :bb4]))

(def p2 (phrase [3/4 3/4]
                [:d5 :c5]))

(def p2acc (phrase basic-rhythm
                   [nil :f4 :eb4 :f4]))

(def p3 (phrase [3/4 1/4 1/4 1/4]
                [:d4 :e4 :g4 :c5]))

(def p4 (phrase [3/4      5/8        1/8]
                [:d5 [:d4 :f4] [:c4 :eb4]]))

(def p5 (phrase [3/4 1/4  1/4 1/4]
                [:d4 :f4 :bb4 :d5]))

(def p6 (phrase [3/4  1/2 1/4]
                [:f5 :eb5 :gb4]))

(def p7 (phrase [3/4  1/4 1/4  1/4]
                [:f4 :eb4 :f4 :bb4]))

(def lead-turn-around (phrase [3/4 3/4]
                         [:d5 :f4]))

(def lead-coda (phrase [3/4 3/4 3/4 3/4 3/4]
                  [:d5 :a4 :c5 :c5 :c5]))

(def bass-a (phrase (repeat 3/4)
                       ;; A section
                       [:bb3 :bb3 :bb3 :bb3
                        :bb3 :bb3 :a3 :d3
                        :g3 :d3 :g3 :d3
                        :g3 :c3 :f3 :f3]))

(def bass-b (phrase [ 3/4  3/4  3/4  3/4  3/4 3/4  3/4  1/2 1/4 3/4  3/4 3/4 3/4]
                    [:bb3 :bb3 :bb3 :bb3 :bb3 :d3 :eb3 :ab3 :c3 :f3 :gb3 :g3 :c3]))

(def bass-turn-around (phrase [3/4 3/4]
                              [:f3 :f3]))

(def bass-coda (phrase [3/4 3/4  3/4  3/4  3/4]
                       [:f3 :f3 :bb3 :eb3 :bb3]))

(def lead-body
  (->> (times 3 p1)
       (then (with p2acc p2))
       (then (times 2 p1))
       (then p3)
       (then p4)
       (then (times 2 p1))
       (then p5)
       (then p6)
       (then p7)
       (then p3)))

(def body-bass
  (->> bass-a
       (then bass-b)))

(def lead-full
  (->> lead-body
       (then lead-turn-around)
       (then lead-body)
       (then lead-coda)))

(def bass-full
  (->> body-bass
       (then bass-turn-around)
       (then body-bass)
       (then bass-coda)))


(comment
  ;; just lead
  (->> lead-body
       (then lead-turn-around)
       (then lead-body)
       (then lead-coda)
       (all :part :lead)
       (tempo (bpm 90/4))
       (live/play))

  ;; just bass body
  (->> body-bass
       (then )
       (all :part :bass)
       (tempo (bpm 90/4))
       (live/play))

  ;; bass and lead together
  (->> (with (all :part :lead lead-full)
             (all :part :bass bass-full))
       (tempo (bpm 90/4))
       (live/play))
  )
