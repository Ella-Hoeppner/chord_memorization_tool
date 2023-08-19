(ns chord.keys
  (:require [hollow.util :as u]
            [clojure.set :refer [subset?]]))

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

(def major-note-differences '(4 3))
(def minor-note-differences '(3 4))


(defn generic-chord-validator [chord-note-differences]
  (let [note-offsets (reduce #(conj %1 (+ %2 (last %1)))
                             [0]
                             chord-note-differences)
        chord-size (count note-offsets)]
    (fn [notes]
      (let [note-count (count notes)]
        (if (and (<= note-count chord-size)
                 (some (fn [i]
                         (subset? (set (map #(- % (apply min notes)) notes))
                                  (set
                                   (map #(- % (nth note-offsets i))
                                        (drop i note-offsets)))))
                       (range (dec chord-size))))
          (or (= note-count chord-size) (set notes))
          #{})))))

(defn specific-chord-validator [chord-note-differences]
  (fn [base-note]
    (fn [notes]
      (let [offset (key->offset base-note)
            offset-notes (map #(- % offset) notes)
            proper-notes (filter (comp (set (reduce #(conj %1 (+ %2 (last %1)))
                                                    [0]
                                                    chord-note-differences))
                                       #(mod % 12))
                                 offset-notes)]
        (if (apply = (map #(quot % 12) proper-notes))
          (let [valid-notes (set (map (partial + offset) proper-notes))]
            (or (and (= notes valid-notes)
                     (= (count valid-notes) 3))
                valid-notes))
          #{})))))

(def generic-major-chord-validator
  (generic-chord-validator major-note-differences))
(def generic-minor-chord-validator
  (generic-chord-validator minor-note-differences))

(def major-chord-validator 
  (specific-chord-validator major-note-differences))
(def minor-chord-validator
  (specific-chord-validator minor-note-differences))
