(ns chord.keys
  (:require [hollow.util :as u]
            [clojure.set :refer [intersection]]))

(def key->offset
  {"C" 0
   "D" 2
   "E" 4
   "F" 5
   "G" 7
   "A" 9
   "B" 11})

(defn major-chord-validator [base-note notes]
  (let [offset (key->offset base-note)
        offset-notes (map #(- % offset) notes)
        proper-notes (filter (comp #{0 4 7} #(mod % 12)) offset-notes)]
    (u/pretty-log [notes offset-notes proper-notes])
    (if (apply = (map #(quot % 12) proper-notes))
      (set (map (partial + offset) proper-notes))
      #{})))