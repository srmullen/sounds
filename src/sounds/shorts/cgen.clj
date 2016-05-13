(ns sounds.shorts.cgen
  (:use [overtone.live]))

(defcgen kick-drum
  [bpm {:default 120 :doc "tempo of kick in beats per minute"}
   pattern {:default [1 0] :doc "sequence pattern of beats"}]
  (:ar
   (let [kick-env (decay (t2a (demand (impulse:kr (/ bpm 30)) 0 (dseq pattern INF))) 0.7)
         kick (* (* kick-env 7) (sin-osc (+ 40 (* kick-env kick-env kick-env 200))))]
     (clip2 kick 1))))

(defcgen snare-drum
  [bpm {:default 120 :doc "tempo of snare in beats per minute"}]
  (:ar
   (let [snare (* 3 (pink-noise) (apply + (* (decay (impulse (/ bpm 240) 0.5) [0.4 2]) [1 0.05])))
         snare (+ snare (bpf (* 4 snare) 2000))]
     (clip2 snare 1))))

(defcgen wobble
  [src {:doc "input source"}
   wobble-factor {:doc "num wobbles per second"}]
  (:ar
   (let [sweep (lin-exp (lf-tri wobble-factor) -1 1 40 3000)
         wob (lpf src sweep)
         wob (* 0.8 (normalizer wob))
         wob (+ wob (bpf wob 1500 2))]
     (+ wob (* 0.2 (g-verb wob 9 0.7 0.7))))))

(definst dubstep [bpm 120 wobble-factor 1 note 50]
  (let [freq (midicps (lag note 0.25))
        bass (apply + (saw (* freq [0.99 1.01])))
        bass (wobble bass wobble-factor)
        kick (kick-drum bpm :pattern [1 0 0 0 0 0 1 0 1 0 0 1 0 0 0 0])
        snare (snare-drum bpm)]
    (clip2 (+ bass kick snare) 1)))
