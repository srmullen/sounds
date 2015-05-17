(ns sounds.synth
  (:use [overtone.live]))

(defsynth sins [bus 0 rate 20 end 20 dur 1]
  (out bus (pan2  (* (+ (sin-osc 220)
                        (sin-osc 250))
                     (sin-osc (line:kr 0 end dur FREE))))))

(defsynth imp [bus 0 freq 440 rate 10]
  (out bus (* (impulse rate) (sin-osc freq))))
