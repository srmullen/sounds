(ns sounds.reducers
  (require [clojure.core.reducers :as r]))

(defn benchmark [f N times]
  (let [nums (vec (range N))
        start (java.lang.System/currentTimeMillis)]
    (dotimes [n times]
      (f nums))
    (- (java.lang.System/currentTimeMillis) start)))


(defn eager-map [& args]
  (doall (apply map args)))

(defn eager-filter [& args]
  (doall (apply filter args)))

(defn eager-test [nums]
  (eager-filter even? (eager-map inc nums)))

(defn lazy-test [nums]
  (doall (filter even? (map inc nums))))

(defn reducer-test [nums]
  (into [] (r/filter even? (r/map inc nums))))

(defn old-reduce [nums]
  (reduce + (map inc (map inc (map inc nums)))))

(defn new-reduce [nums]
  (reduce + (r/map inc (r/map inc (r/map inc nums)))))

(defn new-fold [nums]
  (r/fold + (r/map inc (r/map inc (r/map inc nums)))))

(println "Eager Test: " (benchmark eager-test 1000000 10) "ms")
(println "Lazy Test: " (benchmark lazy-test 1000000 10) "ms")
(println "Reducer Test: " (benchmark reducer-test 1000000 10) "ms")
(println "Old reduce Test: " (benchmark old-reduce 1000000 10) "ms")
(println "new Reduce Test: " (benchmark new-reduce 1000000 10) "ms")
(println "new fold Test: " (benchmark new-fold 1000000 10) "ms")

