(ns sounds.sequencers
  (:use [overtone.live])
  (:require [sounds.util]))

(defn synth->sequencer [synth]
  (fn anonsynth [notes]
    (when (first notes)
      (let [note (first notes)
            freq (note->hz (note 0))
            dur  (note 1)
            inst (synth :freq freq :dur dur)]
        (apply-at (+ (now) (* 1000 dur)) anonsynth [(rest notes)])))))


(defn play-one
  [metronome beat instrument [bus pitch dur amp]]
  (let [end (+ beat dur)]
    (if pitch
      (let [id (at (metronome beat) (instrument :bus bus :freq (note->hz  pitch) :amp amp))]
        (at (metronome end) (ctl id :gate 0))))
    end))

(defn play
  ([metronome inst score]
     (play metronome (metronome) inst score))
  ([metronome beat instrument score]
     (let [cur-note (first score)]
       (when cur-note
         (let [next-beat (play-one metronome beat instrument cur-note)]
           (println next-beat)
           (apply-at (metronome next-beat) play metronome next-beat instrument
                     (next score) []))))))
