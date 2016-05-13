(ns sounds.ugen-comps.generators.sin-osc
  (:use [overtone.live]
        [sounds.common])
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan close! thread alts! alts!! timeout]]))

(comment
  SinOsc is an Interpolating Sine Wavetable Oscillator.
  What do each of this things mean.
  Interpolating - This means new samples will be generated between the ones in the wavetable to smooth the sound.
  Sine - Obviously its a sinewave.
  Wavetable - The samples are procomputed and stored for quick lookup.
  )

                                        ; sin-osc has two arguements, freq and phase.
                                        ; SClang also provides mul and add arguments but they are not needed in overtone, because of overloading on * and + functions.


                                        ; The most basic sine wave synth
                                        ; the original sin

(defsynth s []
  (out 0 (sin-osc)))

;; sine waves can control panning
(defsynth pan-sin [rate 1 level 1]
  (out 0 (pan2 (sin-osc) (sin-osc rate) level)))

(comment
  (demo 10 (pan2 (* (sin-osc (line:kr 0 5000 10))
                    (sin-osc:kr rate)
                    0.5)))

  (demo 20 (pan2 (* (sin-osc 440)
                    (sin-osc:kr (line:kr 1 1000 20))
                    0.5))))

;; sine waves can control frequency
;; depth is the amplitude of the controling sine wave
(defsynth freq-sin [depth 440 rate 1 amp 0.5]
  (out 0 (pan2 (* amp
                  (sin-osc (* depth
                              (sin-osc:kr rate)))))))


;; needs two audio rate sine oscs to hear phase interference.
;; Changing the phase only affect the volume unless the frequencies are different.
;; If the frequencies and phases are different it creates an amplitude pulsing as the waves cancel eachother out.
(defsynth phase-sin [freq1 440 phase1 0 freq2 440 phase2 0 amp1 0.3 amp2 0.5]
  (out 0 (pan2 (mix [(* (sin-osc freq1 phase1) amp1)
                     (* (sin-osc freq2 phase2) amp2)]))))
