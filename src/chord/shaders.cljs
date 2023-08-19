(ns chord.shaders
  (:require [clojure.walk :refer [prewalk]]
            [hollow.util :as u]
            [chord.config :as c]
            [kudzu.core :refer [kudzu->glsl]]
            [kudzu.tools :refer [unquotable]]))

(def kudzu-wrapper
  (partial kudzu->glsl
           {:precision '{float highp
                         int highp
                         usampler2D highp}}))

(unquotable
 (def keyboard-frag-glsl
   (kudzu-wrapper
    '{:outputs {frag-color vec4}
      :uniforms {size vec2
                 key-down? [bool "128"]}
      :functions
      {rouned-box-dist
       (float
        [pos vec2
         dimensions vec2
         roundings vec4]
        (= roundings.xy (if (> pos.x 0) roundings.xy roundings.zw))
        (= roundings.x (if (> pos.y 0) roundings.x roundings.y))
        (=vec2 q (- (+ (abs pos) roundings.x) dimensions))
        (+ (min (max q.x q.y) 0)
           (- (length (max q 0)) roundings.x)))}
      :main
      ((=vec2 pos (uni->bi (/ gl_FragCoord.xy size)))
       (*= pos.x (/ size.x size.y))
       (+= pos.x ~c/keyboard-x-offset)
       ~(let [spacing (+ c/white-key-width c/key-spacing)]
          '(do
             (=vec2 white-key-domain
                    (vec2 ~(let []
                             '(- (mod (+ pos.x ~spacing)
                                      (* 2 ~spacing))
                                 ~spacing))
                          pos.y))
             (=int white-key-index
                   (int (floor (/ (+ pos.x ~spacing)
                                  (* 2 ~spacing)))))
             (=int modded-white-key-index (% white-key-index "7"))
             (=int adjusted-white-key-index
                   (+ [["int" "0" "2" "4" "5" "7" "9" "11"]
                       modded-white-key-index]
                      (* "12"
                         (/ white-key-index "7"))))
             (=vec2 black-key-domain
                    (vec2 (- (mod pos.x
                                  (* 2 ~spacing))
                             ~spacing)
                          (- pos.y
                             ~(- c/white-key-height c/black-key-height))))
             (=int black-key-index
                   (int (floor (/ pos.x
                                  (* 2 ~spacing)))))
             (=int modded-black-key-index (% black-key-index "7"))
             (=int adjusted-black-key-index
                   (+ [["int" "1" "3" "0" "6" "8" "10" "0"]
                       modded-black-key-index]
                      (* "12"
                         (/ black-key-index "7"))))))
       (=float white-key-dist
               (if (&& (>= white-key-index "0")
                       (< adjusted-white-key-index "128"))
                 (rouned-box-dist white-key-domain
                                  (vec2 ~c/white-key-width
                                        ~c/white-key-height)
                                  (vec4 0
                                        ~c/white-key-rounding
                                        0
                                        ~c/white-key-rounding))
                 1000))
       (=float black-key-dist
               (if (&& (>= black-key-index "0")
                       (< adjusted-black-key-index "128")
                       (|| (== modded-black-key-index "0")
                           (== modded-black-key-index "1")
                           (== modded-black-key-index "3")
                           (== modded-black-key-index "4")
                           (== modded-black-key-index "5")))
                 (rouned-box-dist black-key-domain
                                  (vec2 ~c/black-key-width
                                        ~c/black-key-height)
                                  (vec4 0
                                        ~c/black-key-rounding
                                        0
                                        ~c/black-key-rounding))
                 1000))
       (= frag-color
          (vec4 (if (< black-key-dist 0)
                  (if [key-down? adjusted-black-key-index]
                    (vec3 1 0 0)
                    (vec3 0))
                  (if (|| (>= white-key-dist 0)
                          (< black-key-dist ~c/black-key-outline))
                    (vec3 0.5)
                    (if [key-down? adjusted-white-key-index]
                      (vec3 1 0 0)
                      (vec3 1))))
                1)))})))
