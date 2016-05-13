(ns sounds.synths.core
  (:use [overtone.live]))

(defcgen varlag
  "Variable shaped lag"
  [in     {:default 0 :doc "Input to lag"}
   time   {:default 0.1 :doc "Lag time in seconds"}
   curvature {:default 0 :doc "Control curvature if shape input is 5 (default). 0 means linear, positive and negative numbers curve the segment up and down."}
   shape  {:default 5 :doc "Shape of curve. 0: step, 1: linear, 2: exponential, 3: sine, 4: welch, 5: custom (use curvature param), 6: squared, 7: cubed, 8: hold"}

   ]
  "Similar to Lag but with other curve shapes than exponential. A change on the input will take the specified time to reach the new value. Useful for smoothing out control signals."
  (:kr
   (let [gate (+ (+ (impulse:kr 0 0) (> (abs (hpz1 in)) 0))
                 (> (abs (hpz1 time)) 0) )]
     (env-gen [in 1 -99 -99 in time shape curvature] gate))))

(defcgen buffered-coin-gate
  "Deterministic coingate using random buffer"
  [buf {:doc "pre-allocated buffer containing random values between 0 and 1"}
   seed {:default 0, :doc "Offset into pre-allocated buffer. Acts as the seed"}
   prob {:default 1, :doc "Determines the possibility that the trigger is passed through as a value between 0 and 1"}
   trig {:doc "Incoming trigger signal"} ]
  ""
  (:kr (let [phase (+ seed (pulse-count trig))
             v     (buf-rd:kr 1 buf phase 1)
             res   (< v prob)
             ]
         res)))

(defn shaped-adsr
  "Non gated adsr envelope with shape"
  ([attack
    decay
    sustain
    release
    attack_level
    decay_level
    sustain_level
    env_curve]
   (shaped-adsr attack decay sustain release attack_level decay_level sustain_level env_curve 0))
  ([attack
    decay
    sustain
    release
    attack_level
    decay_level
    sustain_level
    env_curve
    min]
  [min 4 -99 -99
   attack_level  attack  env_curve 0
   decay_level   decay   env_curve 0
   sustain_level sustain env_curve 0
   min           release env_curve 0] ))
