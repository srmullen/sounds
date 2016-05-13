(ns sounds.ugen-comps.partials
  (:use [overtone.live]))

(def partial-atom (atom {}))

(def dopartial (create-sin-fn (partial modulated-vol-sin [:tail later-g]) partial-atom))

(defn create-partial [root rate partial]
  (let [root-freq (note->hz root)
        freq (* root-freq partial)
        amp (/ 0.5 partial)]
    (dopartial (keyword (str (name root) "p" partial)) :freq freq :amp amp :rate rate)))

(defn create-partials
  "Given a root frequency, creates the partials in the range from low to high"
  ([root low high]
     (let [partials (range (+ 1 low) (+ 1 high))
           rate 1]
       (map (partial create-partial root rate) partials)))

  ([root low high rate]
     (let [partials (range (+ 1 low) (+ 1 high))]
       (map (partial create-partial root rate) partials))))
