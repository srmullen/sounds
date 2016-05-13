(ns sounds.interval
  (:use [overtone.live]))

(defn note->hz [music-note]
  (midi->hz (note music-note)))

(def ratios [1 16/15 9/8 6/5 5/4 4/3 45/32 3/2 8/5 5/3 9/5 15/8])

(defn chromatic-ratio [n]
  (let [octaves (Math/floor (/ n 12))
        ratio (nth ratios (mod n 12))]
    (if (neg? n)
      (/ 1 ratio)
      (+ octaves ratio))))

(def intervals {:unison 1
                :octave 2
                :fourth 4/3
                :fifth 3/2
                :tritone 45/32
                :minor {:second 16/15
                        :third 6/5
                        :sixth 8/5
                        :seventh 9/5}
                :major {:second 9/8
                        :third 5/4
                        :sixth 5/3
                        :seventh 15/8}})
