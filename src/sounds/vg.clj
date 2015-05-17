(ns sounds.vg
  (:use [overtone.live]))

(defn note->hz [music-note]
  (midi->hz (note music-note)))

(defsynth snare [dur 0.1 amp 1]
  (let [env (env-gen (adsr 0.02 0.01 0.5 0.1) (line:kr 1 0 dur) 1 0 1 FREE)]
    (out 0 (pan2 (* env
                    amp
                    (white-noise))))))

(defsynth kick [dur 0.1 amp 1]
  (let [env (env-gen (adsr 0.02 0.01 0.5 0.1) (line:kr 1 0 dur) 1 0 1 FREE)]
    (out 0 (pan2 (* env
                    amp
                    (lpf (white-noise)))))))

(defsynth skick [dur 0.2 amp 1]
  (let [env (env-gen (adsr 0 0 1 0) (line:kr 1 0 dur) 1 0 1 FREE)]
    (out 0 (pan2 (* env
                    (sin-osc 100)
                    amp)))))

(defsynth mel [freq 440 attack 0 decay 0 sustain 1 release 0 gate 1]
  (let [env (env-gen (adsr attack decay sustain release) gate 1 0 1 FREE)]
    (out 0 (pan2 (lpf (pulse freq) 2000)))))

(defn play-one
  [metronome beat instrument [pitch dur]]
  (let [end (+ beat dur)]
    (if pitch
      (let [id (at (metronome beat) (instrument (note->hz pitch)))]
        (do
          (println id)
          (at (metronome end) (ctl id :gate 0)))))
    end))

(defn play
  ([metronome inst score]
     (play metronome (metronome) inst score))
  ([metronome beat instrument score]
     (let [cur-note (first score)]
       (when cur-note
         (let [next-beat (play-one metronome beat instrument cur-note)]
           (apply-at (metronome next-beat) play metronome next-beat instrument
                     (next score) []))))))
