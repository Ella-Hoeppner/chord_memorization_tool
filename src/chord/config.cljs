(ns chord.config
  (:require [hollow.util :as u]))

(def u32-max (dec (Math/pow 2 32)))

(def white-key-width 0.05)
(def white-key-height (* white-key-width 6))
(def white-key-rounding 0.01)

(def black-key-width (* white-key-width 0.6))
(def black-key-height (* white-key-height 0.65))
(def black-key-rounding white-key-rounding)

(def key-spacing 0.005)
(def black-key-outline 0.0075)

(def keyboard-x-offset 4.3)

(def background-color [0 0 0])
