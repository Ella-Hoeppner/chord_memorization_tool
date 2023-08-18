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
                 black-keys-down? [bool "7"]
                 white-keys-down? [bool "7"]}
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
       ~(let [spacing (+ c/white-key-width c/key-spacing)]
          '(do
             (=vec2 white-key-domain
                    (vec2 ~(let []
                             '(- (mod (+ pos.x ~spacing)
                                      (* 2 ~spacing))
                                 ~spacing))
                          pos.y))
             (=int white-key-index
                   (int (mod (/ (+ pos.x ~spacing)
                                (* 2 ~spacing))
                             7)))
             (=vec2 black-key-domain
                    (vec2 (- (mod pos.x
                                  (* 2 ~spacing))
                             ~spacing)
                          (- pos.y
                             ~(- c/white-key-height c/black-key-height))))
             (=int black-key-index
                   (int (mod (/ pos.x
                                (* 2 ~spacing))
                             7)))))
       (=float white-key-dist
               (rouned-box-dist white-key-domain
                                (vec2 ~c/white-key-width
                                      ~c/white-key-height)
                                (vec4 0
                                      ~c/white-key-rounding
                                      0
                                      ~c/white-key-rounding)))
       (=float black-key-dist
               (if (|| (== black-key-index "0")
                       (== black-key-index "1")
                       (== black-key-index "3")
                       (== black-key-index "4")
                       (== black-key-index "5"))
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
                  (if [black-keys-down? black-key-index]
                    (vec3 1 0 0)
                    (vec3 0))
                  (if (|| (>= white-key-dist 0)
                          (< black-key-dist ~c/black-key-outline))
                    (vec3 0.5)
                    (if [white-keys-down? white-key-index]
                      (vec3 1 0 0)
                      (vec3 1))))
                1)))})))
