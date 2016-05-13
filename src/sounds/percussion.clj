(ns sounds.percussion
  (:use [overtone.live]))

(defsynth kick [bus 0 amp 1 freq 65 decay 0.6]
  (let [env (env-gen (perc 0 decay) :action FREE)
        w (white-noise)
        sub (sin-osc freq)]
    (out bus (pan2 (* amp env (+ (lpf w)
                                 sub))))))

(defsynth snare [bus 0 amp 1 decay 0.6 freq 440]
  (let [env (env-gen (perc 0 decay))
        src (white-noise)]
    (out bus (pan2 (bpf (* amp src env) freq)))))
