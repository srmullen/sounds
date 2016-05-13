(ns sounds.ugen-comps.amp-sin
  (:use [overtone.live]
        [sounds.common]))

(defsynth sin-synth [out-bus 0 freq 5]
  (out:kr out-bus (sin-osc:kr freq)))

;; Groups
(defonce main-g (group "main bus"))
(defonce early-g (group "1" :head main-g))
(defonce later-g (group "2" :after early-g))

(defonce bus1 (audio-bus))
(defonce bus2 (audio-bus))
(defonce bus3 (audio-bus))
(defonce bus4 (audio-bus))
(defonce bus5 (audio-bus))

(comment
  ;; create the volume controling synth
  (def rate1 (sin-synth [:tail early-g] bus1))
  (def rate2 (sin-synth [:tail early-g] bus2))
  (def rate3 (sin-synth [:tail early-g] bus3))
  (def rate4 (sin-synth [:tail early-g] bus4))
  (def rate5 (sin-synth [:tail early-g] bus5))

  ;; create the audio rate synth
  (def mvt (modulated-vol-sin [:tail later-g] sin-bus))
  )


(defsynth modulated-vol-sin [vol-bus 0 freq 220 amp 0.5]
  (out 0 (pan2 (* (in:kr vol-bus) (sin-osc freq) amp))))

(def sin-atom (atom {}))

(defn stop-sins
  "Kill all synths in the atom and empty it"
  [atm]
  (try (apply kill (vals @atm)) (catch Exception e (println "caught exception")))
  (swap! atm (fn [synths] {})))

(defn create-sin-fn [inst atm]
  (fn [synth-name & args]
    (if-let [synth (synth-name @atm)]
      ;; if the synth already exists update its params
      (apply (partial ctl synth) args)
      ;; create the synth if it doesn't exist
      (let [synth (apply inst args)]
        (swap! atm
               (fn [synths]
                 (assoc synths synth-name synth)))
        synth))))

(def dosin (create-sin-fn (partial modulated-vol-sin [:tail later-g]) sin-atom))

(defn cmaj7 [bus]
  (dosin :1 :freq (note->hz :c3) :vol-bus bus)
  (dosin :2 :freq (note->hz :e4) :vol-bus bus)
  (dosin :3 :freq (note->hz :g5) :vol-bus bus)
  (dosin :4 :freq (note->hz :b4) :vol-bus bus)
  (dosin :5 :freq (note->hz :d5) :vol-bus bus))

(defn g7 [bus]
  (dosin :1 :freq (note->hz :d3) :vol-bus bus)
  (dosin :2 :freq (note->hz :f4) :vol-bus bus)
  (dosin :3 :freq (note->hz :g5) :vol-bus bus)
  (dosin :4 :freq (note->hz :b4) :vol-bus bus)
  (dosin :5 :freq (note->hz :a4) :vol-bus bus))

(defn play-sin [synth-name note bus]
  (dosin synth-name :freq (note->hz note) :vol-bus bus))

(defn phrase [midi rate bus]
  (let [start-time (now)
        dur (/ (* 1000 (rate->dur rate)) 2)
        mod (sin-synth [:tail early-g] bus rate)]
    (dosin :1 :freq (midi->hz midi) :vol-bus bus)
    (at (+ start-time dur) (dosin :1 :freq (midi->hz (+ midi 2))))
    (at (+ start-time (* 2 dur)) (dosin :1 :freq (midi->hz(+ midi 4))))
    (at (+ start-time (* 3 dur)) (dosin :1 :freq (midi->hz (+ midi 5))))
    (at (+ start-time (* 4 dur)) (dosin :1 :freq (midi->hz (+ midi 7))))
    (at (+ start-time (* 5 dur)) (do (stop-sins sin-atom)))
    mod))

(defn composition []
  (let [start-time (now)
        r1 0.05
        d1 (/ (* 1000 (rate->dur r1)) 2)
        s1 (sin-synth [:tail early-g] bus1 r1)
        s2 (sin-synth [:tail early-g] bus2 1)
        s3 (sin-synth [:tail early-g] bus3 20)
        s4 (sin-synth [:tail early-g] bus4 100)
        s5 (sin-synth [:tail early-g] bus5 200)]
    (dosin :1 :freq (note->hz :c4) :vol-bus bus1)
    (at (+ start-time d1) (dosin :1 :freq (note->hz :d4)))
    (at (+ start-time (* 2 d1)) (dosin :1 :freq (note->hz :e4)))
    (at (+ start-time (* 3 d1)) (dosin :1 :freq (note->hz :f4)))
    (at (+ start-time (* 4 d1)) (dosin :1 :freq (note->hz :g4)))
    (at (+ start-time (* 5 d1))
        (do
          (stop-sins sin-atom)
          (stop)))))
