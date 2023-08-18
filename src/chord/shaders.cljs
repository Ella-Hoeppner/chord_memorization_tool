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
      :uniforms {size vec2}
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
       (=vec2 white-key-domain
              (vec2 ~(let [spacing (+ c/white-key-width c/key-spacing)]
                       '(- (mod (+ pos.x ~spacing)
                                (* 2 ~spacing))
                           ~spacing))
                    pos.y))
       (=float white-key-dist
               (rouned-box-dist white-key-domain
                                (vec2 ~c/white-key-width
                                      ~c/white-key-height)
                                (vec4 0
                                      ~c/white-key-rounding
                                      0
                                      ~c/white-key-rounding)))
       (=vec2 black-key-domain
              (vec2 ~(let [spacing (+ c/white-key-width c/key-spacing)]
                       '(- (mod pos.x
                                (* 2 ~spacing))
                           ~spacing))
                    (- pos.y ~(- c/white-key-height c/black-key-height))))
       (=int black-key-domain-index
             ~(let [spacing (+ c/white-key-width c/key-spacing)]
                '(int (mod (/ pos.x
                              (* 2 ~spacing))
                           7))))
       (=float black-key-dist
               (if (|| (== black-key-domain-index "0")
                       (== black-key-domain-index "1")
                       (== black-key-domain-index "3")
                       (== black-key-domain-index "4")
                       (== black-key-domain-index "5"))
                 (rouned-box-dist black-key-domain
                                  (vec2 ~c/black-key-width
                                        ~c/black-key-height)
                                  (vec4 0
                                        ~c/black-key-rounding
                                        0
                                        ~c/black-key-rounding))
                 1000))
       (= frag-color
          (vec4 (vec3 (if (< black-key-dist 0)
                        0
                        (if (|| (>= white-key-dist 0)
                                (< black-key-dist ~c/black-key-outline))
                          0.5
                          1)))
                1)))})))
