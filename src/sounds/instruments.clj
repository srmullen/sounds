(ns sounds.instruments
  (:use [overtone.live]))

(defsynth beep [bus 0 freq 440.0 dur 1.0 amp 0.5]
  (out bus (-> freq
               pulse
               (* (env-gen (perc 0.05 dur) :action FREE))
               (pan2))))

(defsynth sin-synth [bus 0 freq 440.0 dur 1.0]
  (out bus (* (env-gen (perc 0.05 dur) :action FREE)
              (sin-osc freq))))

(defsynth room [in-bus 0 out-bus 0 mix 0.33 room 0.5 damp 0.5]
  (out out-bus (pan2 (free-verb (in in-bus) mix room damp))))

(defsynth lead [bus 0 freq 440.0 dur 1.0 amp 0.5]
  (let [time (line:kr 0 amp dur FREE)]
    (out bus (-> freq
                 pulse
                 (* time)
                 (pan2)))))

(defsynth voice [bus 0 freq 440 rq 1.0 attack 0.1 decay 0.3 sustain 0.88 release 0.4 dur 1]
  (let [env (env-gen (adsr attack decay sustain release) (line:kr 1 0 dur) 1 0 1 FREE)
        oct (* 2 freq)
        twelfth (* 3 freq)
        oscs [(bpf (saw freq) freq rq)
              (bpf (saw oct) oct rq)
              (bpf (saw twelfth) twelfth rq)]]
    (out bus (pan2 (* env (mix oscs))))))

(defsynth port [freq 440.0 slew-in 100.0 slew-out 100.0 rate 6 depth 0.2]
  (out 0 (pan2 (vibrato (sin-osc (slew:kr freq slew-in slew-out)) rate depth))))

(defsynth szsz [freq 440 amp 1 dur 1]
  (let [sqr1 (pulse freq)
        sqr2 (pulse freq 1)
        tri (lf-tri freq)
        env (line:kr 0 amp dur FREE)]
    (out 0 (pan2 (lpf (mix [sqr1 sqr2 tri]) (line:kr 20000 300 0.09))))))

(defsynth qkick [bus 0 amp 2 high 200]
  (let [env (env-gen (perc 0 0.1))
        noise (white-noise)
        filter (bpf noise high)]
   (out bus (pan2 (* amp env filter)))))
