(ns chord.keys
  (:require [hollow.util :as u]
            [clojure.set :refer [intersection]]))

(def key->offset
  {"C" 0
   "C#" 1
   "Db" 1
   "D" 2
   "D#" 3
   "Eb" 3
   "E" 4
   "F" 5
   "F#" 6
   "Gb" 6
   "G" 7
   "G#" 8
   "Ab" 8
   "A" 9
   "A#" 10
   "Bb" 10
   "B" 11})

(defn major-chord-validator [base-note notes]
  (let [offset (key->offset base-note)
        offset-notes (map #(- % offset) notes)
        proper-notes (filter (comp #{0 4 7} #(mod % 12)) offset-notes)]
    (if (apply = (map #(quot % 12) proper-notes))
      (set (map (partial + offset) proper-notes))
      #{})))