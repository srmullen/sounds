(ns sounds.angelica-bilta
  (:use [overtone.live])
  (:require [sounds.sequencers :as sq]
            [sounds.util :as util]))

(defsynth angelic-voice [bus 0
                         freq 440 attack 0.2 decay 0 sustain 1 release 0.3 gate 1
                         f1 440 f2 440 f3 440 f4 440 f5 440
                         q1 1 q2 1 q3 1 q4 1 q5 1
                         amp1 1 amp2 1 amp3 1 amp4 1 amp5 1]
  (let [env (env-gen (adsr attack decay sustain release) gate 1 0 1 FREE)
        raw (saw freq)
        frm1 (* (bpf raw f1 q1) amp1)
        frm2 (* (bpf raw f2 q2) amp2)
        frm3 (* (bpf raw f3 q3) amp3)
        frm4 (* (bpf raw f4 q4) amp4)
        frm5 (* (bpf raw f5 q5) amp5)
        mx (* env (+ frm1 frm2 frm3 frm4 frm5))]
    (out bus (pan2 mx))))

(defn soprano-a [freq]
  (angelic-voice :freq freq
                 :f1 800 :f2 1150 :f3 2900 :f4 3900 :f5 4950
                 :q1 0.1 :q2 0.1 :q3 0.1 :q4 0.1 :q5 0.1
                 :amp1 1 :amp2 0.8 :amp3 0.4 :amp4 0.6 :amp5 0.2))

(defsynth formant []
  )


;; church needs reverb
(defonce church (group))
(defonce vibratos (group))

(defsynth sin-wave [bus 0 freq 440 amp 0.5 attack 0 decay 0 sustain 1 release 0 gate 1]
  (let [env (env-gen (adsr attack decay sustain release) gate 1 0 1 FREE)]
    (out bus (pan2 (* amp
                      env
                      (sin-osc freq))))))

(def notes [[:a4 1] [:c5 1] [:b4 1] [:e4 1]
            [:a4 2] [:a4 1] [:e4 1] [:a4 2]
            [:a4 2] [:a4 1] [:e4 1] [:g#4 2]
            [:a4 2] [:a4 1] [:e4 1] [:f4 2]])

(def sin-player (synth->sequencer sin-wave))


(defsynth violin
  "violin inspired by Sound On Sound April-July 2003 articles."
  [pitch   {:default 60  :min 0   :max 127 :step 1}
   amp     {:default 1.0 :min 0.0 :max 1.0 :step 0.01}
   gate    {:default 1   :min 0   :max 1   :step 1}
   out-bus {:default 0   :min 0   :max 127 :step 1}
   vrate 6
   vdepth 0.02
   vdelay 1]
  (let [freq   (midicps pitch)
        freqp  (slew:kr freq 100.0 100.0)
        freqv  (vibrato :freq freqp :rate vrate :depth vdepth :delay vdelay)
        saw    (saw freqv)
        saw0   (* saw (env-gen (adsr 1.5 1.5 0.8 1.5) :gate gate :action FREE))
        saw1   (lpf saw0 4000)
        band1  (bpf saw1 300 (/ 3.5))
        band2  (bpf saw1 700 (/ 3.5))
        band3  (bpf saw1 3000 (/ 2))
        saw2   (+ band1 band2 band3)

        saw3   (hpf saw2 30)
        ]
        (out out-bus (pan2 (* amp saw3)))))
