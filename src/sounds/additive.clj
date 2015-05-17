(ns sounds.additive
  (:use overtone.live))

(defsynth sin-add
  [freq 440
   amp 0.5
   tones 3]
  (out 0 (* amp
            (sin-osc :freq freq))))

(definst snare
  [attack 0.01 release 1 level 1 curve -4]
  (* (env-gen (perc attack release level curve) :action FREE)
     (white-noise)))

(defsynth trem [freq 440 rate 1 depth 1]
  (out 0 (pan2 (* (* depth (sin-osc rate))
                  (sin-osc freq)))))

(defsynth vibr [freq 440 rate 1 depth 1]
  (out 0 (pan2 (sin-osc (+ freq
                           (* (sin-osc rate)
                              depth))))))

(defsynth sawz [bus 0 freq 440 attack 0 sustain 0.3 release 0 amp 0.3]
  (out bus (* amp
              (env-gen (lin attack sustain release) 1 1 0 1 FREE)
              (saw freq))))

(defsynth allp [bus 0 max-dt 0.2 delay-time 0.2 decay-time 1.0]
  (out 0 (pan2 (allpass-c (in bus) max-dt delay-time decay-time))))

(defsynth ferb [in-bus 10 mix 0.33 room 0.5 damp 0.5]
  (out 0 (pan2 (free-verb (in in-bus) mix room damp))))


(defsynth vibrl [freq 440 rate 1 depth 1 len 1]
  (out 0 (pan2 (sin-osc (+ freq
                           (* (sin-osc (line:kr 0 20 len FREE))
                              depth))))))

(defonce tri-bus (audio-bus))
(defonce sin-bus (audio-bus))

(defsynth tri-synth [out-bus 0 freq 5]
  (out:kr out-bus (lf-tri:kr freq)))

(defsynth sin-synth [out-bus 0 freq 5]
  (out:kr out-bus (sin-osc:kr freq)))

(defonce main-g (group "get-on-the-bus main"))
(defonce early-g (group "early birds" :head main-g))
(defonce later-g (group "latecomers" :after early-g))

(def tri-synth-inst (tri-synth [:tail early-g] tri-bus))
(def sin-synth-inst (sin-synth [:tail early-g] sin-bus))

(defsynth modulated-vol-tri [vol-bus 0 freq 220]
  (out 0 (pan2 (* (in:kr vol-bus) (lf-tri freq)))))

(defsynth modulated-freq-tri [freq-bus 0 mid-freq 220 freq-amp 55]
  (let [freq (+ mid-freq (* (in:kr freq-bus) freq-amp))]
    (out 0 (pan2 (lf-tri freq)))))

(def mvt (modulated-vol-tri [:tail later-g] sin-bus))

(defsynth pling [out-bus 0
                 rate 0.3 amp 0.5]
  (out out-bus
       (* (decay (impulse rate) 0.25)
          (* amp (lf-cub 1200 0)))))

(defsynth reverb-demo [in-bus 10]
    (out 0 (pan2 (free-verb (in in-bus) 0.5 (mouse-y:kr 0.0 1) (mouse-x:kr 0.0 1)))))

(demo 10
      (let [trig (impulse:kr 15)
            freqs (dseq [440 880 220] INF)
            note-gen (demand:kr trig 0 freqs)
            src (sin-osc note-gen)]
                (* [0.1 0.1] src)))
