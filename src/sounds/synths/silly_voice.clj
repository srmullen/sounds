(ns sounds.synths.silly-voice
  (:use [overtone.live]))


(defcgen va [in {:default :none}]
  (:ar
   (mix (*
         (b-band-pass
          in
          [600 1040 2250 2450 2750]
          [0.1 0.067307692307692 0.048888888888889 0.048979591836735 0.047272727272727])
         [1 0.44668359215096 0.35481338923358 0.35481338923358 0.1])))
  (:default :ar))

(defcgen ve [in {:default :none}]
  (:ar
   (mix (*
     (b-band-pass
      in
      [400 1620 2400 2800 3100]
      [0.1 0.049382716049383 0.041666666666667 0.042857142857143 0.038709677419355])
     [1 0.25118864315096 0.35481338923358 0.25118864315096 0.12589254117942])))
  (:default :ar))

(defcgen vi [in {:default :none}]
  (:ar
   (mix
    (*
     (b-band-pass
      in
      [250 1750 2600 3050 3340]
      [0.24 0.051428571428571 0.038461538461538 0.039344262295082 0.035928143712575])
     [1 0.031622776601684 0.15848931924611 0.079432823472428 0.03981071705535])))
  (:default :ar))

(defcgen vo [in {:default :none}]
  (:ar
   (mix
    (*
     (b-band-pass
      in
      [400 750 2400 2600 2900]
      [0.1 0.10666666666667 0.041666666666667 0.046153846153846 0.041379310344828])
     [1 0.28183829312645 0.089125093813375 0.1 0.01])))
  (:default :ar))

(defcgen vu [in {:default :none}]
  (:ar
   (mix
    (*
     (b-band-pass
      in
      [350 600 2400 2675 2950]
      [0.11428571428571 0.13333333333333 0.041666666666667 0.044859813084112 0.040677966101695])
     [1 0.1 0.025118864315096 0.03981071705535 0.015848931924611])))
  (:default :ar))

(defsynth voice [freq 220
                 amp 0.5
                 vibrato-speed 6
                 vibrato-depth 4
                 att 0.01
                 rel 0.1
                 lag-time 1
                 gate 1
                 vowel 0]
  (let [env (env-gen (asr att 1 rel) gate :action FREE)
        snd [(dc 0) (dc 0)]
        temp-lag 0.2
        vibrato (*  vibrato-depth (sin-osc:kr (lag vibrato-speed 1)))
        in (var-saw (lag (+ vibrato (dc freq))))]
    (out 0 (pan2 (* amp (select vowel [(va in) (ve in) (vi in) (vo in) (vu in)]))))))q

(demo
 (let [env (env-gen (asr 0.01 1 0.1) 1 :action FREE)
       snd (pan2
            (mix
             (b-band-pass
              (var-saw 220)
              [600 1040 2250 2450 2750]
              [0.1 0.067307692307692 0.048888888888889 0.048979591836735 0.04727272727272])))
       companded (compander snd snd (dbamp -30) 1 0.5 0.01 0.1)
       verbed (free-verb2 companded companded)]
   (* env verbed)))

(defsynth amp-examp [attack 0.01 release 0.01 a 1]
  (let [amp (amplitude (* a (sound-in)) attack release)]
    (out 0 (* (saw [220 110]) amp))))

(defsynth amp-sin-freq [attack 0.01 release 0.01]
  (let [amp (amplitude (sound-in) attack release)
        freq-mul (sin-osc (* 4 (+ 1 amp)))]
    (out 0 (* 0.5 (sin-osc (* [110 550] freq-mul))))))

(demo
 (let [orig-sound (* (decay2:kr (* (impulse:kr 8 0)
                                   (+ 0.3 (* -0.3 (lf-saw:kr 0.3))))
                                0.001
                                0.3)
                     (mix (pulse [80 81] 0.3)))]))


(defsynth osound [freq 80 imp 8 attack-time 0.001 decay-time 0.3 recurrance 0.3]
  (out 0 (pan2 (* (decay2:kr (* (impulse:kr imp 0)
                                (+ 0.3 (* -0.3 (lf-saw:kr recurrance))))
                             attack-time
                             decay-time)
                  (mix (pulse [freq (* freq 1.0125)] 0.3))))))

(defcgen cosound [freq {:default 80} imp {:default 8}]
  (:ar (* (decay2:kr (* (impulse:kr imp 0)
                        (+ 0.3 (* -0.3 (lf-saw:kr 0.3))))
                     0.001
                     0.3)
          (mix (pulse [freq (* freq 1.0125)] 0.3)))))

(defsynth ngate []
      (let [orig-sound (cosound)
            thresh-val (mouse-x 0.1 0.5)
            sb-val     (mouse-y 1 10)
            noise-gate (compander :in orig-sound
                                  :control orig-sound
                                  :thresh thresh-val
                                  :slope-below (mouse-y 1 10)
                                  :slope-above 1
                                  :clamp-time 0.01
                                  :relax-time 0.1)
            trig       (impulse:kr 5)
            poll-x (poll trig thresh-val "thresh")
            poll-y (poll trig sb-val "slope-below")
            mix-val 1
            mixed (x-fade2 orig-sound noise-gate mix-val)]
        (out 0 [mixed mixed])))


(defsynth b-band-rhythm [freq-amp 10 rate 0.8 clip-level 1]
  (out 0 (pan2
          (clip2 (normalizer
                  (b-band-pass (white-noise)
                               (* freq-amp (square rate)))
                  :dur 0.01)
                 clip-level))))

(defsynth bass [freq 440 res 440 bwr 1 amp 0.5 lag-time 1]
  (out 0 (pan2
          (* amp
             (normalizer (resonz (var-saw (lag freq lag-time)) res bwr))))))
