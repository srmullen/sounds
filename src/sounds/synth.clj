(ns sounds.synth
  (:use [overtone.live]))

(defsynth sin-square [freq 440 sq 440]
  (out 0 (* 0.5 (+ (square 0.5 sq)) (sin-osc freq)))
  (out 1 (* 0.5 (+ (square 0.5 sq)) (sin-osc freq))))

(defsynth just-sins [freq 440]
  (out 0 (* 0.5 (sin-osc freq)))
  (out 1 (* 0.5 (sin-osc freq))))

(definst sin-wave []
  (sin-osc 440))

(defsynth pedestrian-crossing
  [outbus 0 freq 2500 pulse 5 vol 0.2]
  (out outbus
       (pan2 (* vol (sin-osc freq) (lf-pulse pulse)))))

(definst trancy-waves [vol 0.2]
  (* vol
     (+ (sin-osc 200) (saw 200) (saw 203) (sin-osc 400))))

(defsynth roaming-sines
  []
  (let [freqs (take 5 (repeatedly #(ranged-rand 40 2000)))
        ampmod [( mouse-x 0 1) (mouse-y 0 1)]
        snd (splay (* 0.5 (sin-osc freqs)))
        snd (* (sin-osc ampmod) snd)]
    (out 0 snd)))

(defsynth scratch-pedulum []
  (let [kon (sin-osc:kr (* 10 (mouse-x)))
        k2 (sin-osc:kr (* 5 (mouse-x)))
        lpk (lin-lin:kr kon -1 1 0 1000)
        ;; foo (poll:kr (impulse:kr 20) lpk)
        src (lpf (white-noise) lpk)
        src (pan2 src k2)
        bak (* 0.5 (lpf (white-noise)))]
    (out 0 (+ src [bak bak]))))

(defsynth trigger-finger []
  (send-trig:kr (impulse:kr 0.2) 200 (num-output-buses)))

;; (on-event "/tr" #(println "trigger: " %) ::trigger-test)

(defsynth dtest []
  (send-trig:kr (impulse:kr 2) 1 (demand:kr (impulse:kr 0.5) 1 (dwhite))))

(definst sizzle
  [amp 0.4 depth 10 freq 220 lfo 8]
  (* amp (saw (+ freq (* depth (sin-osc:kr lfo))))))

(defsynth line-two [bus 0]
  (let [sig (lf-pulse 1/6 0 0.25)]
    (out 0 (* 0.5 (sin-osc [480 440]) (lag sig)))))

(definst busy-signal [pulse-rate 2 lag-time 0.1]
  (let [on-off (lag (lf-pulse pulse-rate) lag-time)]
    (* 0.2
       (apply + (* (sin-osc [480 620]) on-off)))))

;; need to make a call?
(def DTMF-TONES {1 [697 1209]
                 2 [770 1209]
                 3 [852 1209]
                 4 [697 1336]
                 5 [770 1336]
                 6 [852 1336]
                 7 [697 1477]
                 8 [770 1477]
                 9 [852 1477]
                 \* [697 1633]
                 0 [770 1633]
                 \# [852 1633]})

(definst dtmf [freq-a 770 freq-b 1633 gate 1]
  (let [sig (* 0.2 (+ (sin-osc freq-a) (sin-osc freq-b)))
        env (env-gen (asr 0.001 1 0.001) gate 1 0 1 FREE)]
    (* sig env)))

(defn dial-number [num-seq]
  (loop [t (now)
         nums num-seq]
    (when nums
      (let [t-on (+ t 160 (rand-int 200))
            t-off (+ t-on (rand-int 80))
            [a b] (get DTMF-TONES (first nums))]
        (at t-on (dtmf a b))
        (at t-off (ctl dtmf :gate 0))
        (recur t-off (next nums))))))

(defsynth bizzle [out-bus 10 amp 0.5]
  (out out-bus
       (* amp
          (+ (* (decay2 (* (impulse 10 0)
                           (+ (* (lf-saw:kr 0.3 0) -0.3) 0.3))
                        0.001)
                0.3)
             (apply + (pulse [80 81]))))))

(defsynth compressor-demo [in-bus 10]
  (let [source (in in-bus)]
    (out 0 (pan2 (compander
                  source
                  source
                  (mouse-y:kr 0.0 1) 1 0.5 0.01 0.01)))))

;; (def b (audio-bus))
;; (def b-s (bizzle b))
;; (compressor-demo [:after b-s] b)

(defsynth pling [out-bus 0
                 rate 0.3
                 amp 0.5]
  (out out-bus
       (* (decay (impulse rate) 1/4)
          (* amp (lf-cub 1200 0)))))

(defsynth reverb-demo [in-bus 10]
  (out 0 (pan2 (free-verb (in in-bus)
                          0.5
                          (mouse-y:kr 0.0 1)
                          (mouse-x:kr 0.0 1)))))

(defsynth echo-demo [in-bus 10]
  (let [source (in in-bus)
        echo (comb-n source
                     0.5
                     (mouse-x:kr 0 1)
                     (mouse-y:kr 0 1))]
    (out 0 (pan2 (+ echo (in in-bus) 0)))))

;; fetch a spoken countdown from freesound.org
(def count-down (sample (freesound-path 71128)))

(defsynth shroeder-reverb-countdown
  [rate 1]
  (let [input (pan2 (play-buf 1 count-down rate :action FREE) -0.5)
        delrd (local-in 4)
        output (+ input [(first delrd) (second delrd)])
        sig [(+ (first output) (second output)) (- (first output) (second output))
             (+ (nth delrd 2) (nth delrd 3)) (- (nth delrd 2) (nth delrd 3))]
        sig [(+ (nth sig 0) (nth sig 2)) (+ (nth sig 1) (nth sig 3))
             (- (nth sig 0) (nth sig 2)) (- (nth sig 0) (nth sig 2))]
        sig (* sig [0.4 0.37 0.333 0.3])
        deltimes (- (* [101 143 165 177] 0.001) (control-dur))
        lout (local-out (delay-c sig deltimes deltimes))]
    (out 0 output)))

;; (shroeder-reverb-countdown :rate 0.8 :dec 0.8 :del 10)

(defsynth schroeder-reverb-mic
  [rate 1 dec 1 del 10 out-bus 0]
  (let [input    (pan2 (allpass-c (sound-in) 10  dec del))
        delrd    (local-in 4)
        output   (+ input [(first delrd) (second delrd)])
        sig      [(+ (first output) (second output)) (- (first output) (second output))
                  (+ (nth delrd 2) (nth delrd 3)) (- (nth delrd 2) (nth delrd 3))]
        sig      [(+ (nth sig 0) (nth sig 2)) (+ (nth sig 1) (nth sig 3))
                  (- (nth sig 0) (nth sig 2)) (- (nth sig 0) (nth sig 2))]
        sig      (* sig [0.4 0.37 0.333 0.3])
        deltimes (- (* [101 143 165 177] 0.001) (control-dur))
        lout     (local-out (delay-c sig deltimes deltimes))]
    (out out-bus output)))

;; (schroeder-reverb-mic :rate 0.8 :dec 0.8 :del 10)


;; BUSSES ;;

(defonce tri-bus (audio-bus))
(defonce sin-bus (audio-bus))

(defsynth tri-synth [out-bus 0 freq 5]
  (out:kr out-bus (lf-tri:kr freq)))

(defsynth sin-synth [out-bus 0 freq 5]
  (out:kr out-bus (sin-osc:kr freq)))

;; the way to gain controll over order of execution within
;; the synthesis tree is to use groups
(defonce main-g (group "get-on-the-bus main"))
(defonce early-g (group "early-birds" :head main-g))
(defonce late-g (group "latecomerts" :after early-g))

;; create some source synths that will send signals on the
;; busses. Put them in the early group to ensure their
;; signals get sent first.
(def tri-synth-inst (tri-synth [:tail early-g] tri-bus))
(def sin-synth-inst (sin-synth [:tail early-g] sin-bus))

;; define a synth that will use the signal from the bus
;; to make some sound
(defsynth modulated-vol-tri [vol-bus 0 freq 220]
  (out 0 (pan2 (* (in:kr vol-bus) (lf-tri freq)))))

(defsynth modulated-tri-freq [freq-bus 0 mid-freq 220 freq-amp 55]
  (let [freq (+ mid-freq (* (in:kr freq-bus) freq-amp))]
    (out 0 (pan2 (lf-tri freq)))))

(def mvt (modulated-vol-tri [:tail late-g] sin-bus))
(def mvt (modulated-tri-freq [:tail late-g] sin-bus))

;; demand ugens
(demo 2
      (let [trig (impulse:kr 8)
            freqs (dseq [440 880 220] INF)
            note-gen (demand:kr trig 0 freqs)
            src (sin-osc note-gen)]
        (* [0.1 0.1] src)))

(demo 10
      (let [trig (impulse:kr 2.5)
            n 15
            freqs (dser [440 880 660 1760] n)
            note-gen (demand:kr trig 0 freqs)
            src (sin-osc note-gen)]
        (pan2 (* 0.1 src))))

(run (poll:kr (impulse:kr 10) (line:kr 0 1 1) "polled-val:"))

(defsynth tb-303
  "A clone of the sound of a Roland TB-303 bass synthesizer."
  [note     30        ; midi note value input
   wave     0         ; 0=saw, 1=square
   cutoff   100       ; bottom rlpf frequency
   env      1000      ; + cutoff is top of rlpf frequency
   res      0.2       ; rlpf resonance
   sus      0         ; sustain level
   dec      1.0       ; decay
   amp      1.0       ; output amplitude
   gate     0         ; on/off control
   action   NO-ACTION ; keep or FREE the synth when done playing
   position 0         ; position in stereo field
   out-bus  0]
  (let [freq-val   (midicps note)
        amp-env    (env-gen (envelope [10e-10, 1, 1, 10e-10]
                                      [0.01, sus, dec]
                                      :exp)
                            :gate gate :action action)
        filter-env (env-gen (envelope [10e-10, 1, 10e-10]
                                      [0.01, dec]
                                      :exp)
                            :gate gate :action action)
        waves      [(* (saw freq-val) amp-env)
                    (* (pulse freq-val 0.5) amp-env)]
        tb303      (rlpf (select wave waves)
                         (+ cutoff (* filter-env env)) res)]
        (out out-bus (* amp (pan2 tb303 position)))))
