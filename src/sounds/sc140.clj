(ns sounds.sc140
  (:use [overtone.live]))

(defn virgo1 []
  (let [synth (defsynth v1 []
                (let [a (comb-n (distort (bpf (+ (* (saw [32 33]) 0.2)
                                                 (* 7.5
                                                    (local-in 2)))
                                              (* 300 (pow 2 (* 4 (lf-noise0:kr (/ 4 3)))))
                                              0.1)) 2 2 40)]
                  (out 0 (pan2 a))))]
    (synth)))

(defn lf-saw02 []
  (let [synth (defsynth s []
                (let [s (* (env-gen:kr (envelope [1 1 0] [120 10]) 1 1 0 1 FREE)
                           (ringz (impulse [2 1 4] [0.1 0.11 0.12]) [0.1 0.1 0.5]))]
                  (out 0 (pan2 s))))]
    (synth)))
