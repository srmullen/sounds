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

(defsynth bozkurt13 [dur 1/8 freq 8]
  (let [
        r (drand [0 (drand (range 4/10 1 1/10))] INF)
        x (duty:kr dur 0 r)
        d (decay2 x 0.01 0.3)
        ;;s (pow (* d (saw freq)) 1.5)
        s (pow (* d (saw freq)) 1.5)
        f (brf s (+ (* 20 x) [45.1 45]) 0.1)]
    (out 0
         (tanh (leak-dc f)))))


(defsynth boz-drum []
  (let [
        r (drand [(drand (range 4/10 1 1/10))])
        x (duty:kr 1 0 r :action FREE)
        d (decay2 x 0.01 0.3)
        s (pow (* d (saw 8)) 1.5)
        f (brf s (+ (* 20 x) [45.1 45]) 0.1)]
    (out 0
         (tanh (leak-dc f)))))

(defsynth mousefm []
  (out 0 (pan2 (sin-osc (* (mouse-y 0 500) (sin-osc (mouse-x 0 2000)))))))

(defsynth bozkurt16 [rate 8]
  (out 0 (allpass-c (cosh (sin-osc 55))
                    0.4
                    (round (t-exp-rand 0.0002
                                       0.4
                                       (impulse:ar rate)) [0.003 0.004])
                    2)))

(demo (* (env-gen:ar (adsr 0.001 0.8 1 1) (dust:ar 1))
         (sin-osc 999)))

(demo 2
      (let [trig (impulse:kr 18)
            freqs (dseq [440 880 220] INF)
            note-gen (demand:kr trig 0 freqs)
            src (sin-osc note-gen)]
        (* [0.1 0.1] src)))

(demo
 (pan2 (clip2 (* (env-gen (perc) :action FREE)
                  (tanh (sin-osc 60)))
               0.5)))
