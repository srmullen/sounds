(ns sounds.ugen-comps.generators.amp-sin-record
  (:use [sounds.ugen-comps.generators.sin-osc]))


(defn start-recording [path synth]
  (stop) ;; stop any curently running synths and recordings
  (recording-start path)
  (synth))

(defn stop-recording []
  (stop)
  (recording-stop))

; (start-recording "~/audio/amp_sin_200_300.wav" #(amp-sin 200 300))
; (start-recording "~/audio/amp_sin_200_50.wav" #(amp-sin 200 50))
; (start-recording "~/audio/amp_sin_200_20.wav" #(amp-sin 200 20))
