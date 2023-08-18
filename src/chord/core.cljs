(ns chord.core
  (:require [hollow.util :as u]
            [hollow.dom.canvas :refer [set-page-background-color
                                       maximize-gl-canvas
                                       canvas-resolution]]
            [hollow.webgl.shaders :refer [run-shaders!
                                          run-purefrag-shader!]]
            [hollow.webgl.core
             :refer [start-hollow!
                     hollow-state
                     hollow-context
                     merge-hollow-state!
                     update-hollow-state!]
             :refer-macros [with-context]]
            [chord.shaders :refer [keyboard-frag-glsl]]
            [chord.config :as c]))

(defn update-resolution! [{:keys [gl resolution]
                           :as state}]
  (with-context gl
    (maximize-gl-canvas {:max-pixel-ratio 2})
    (let [new-resolution (canvas-resolution)]
      (if (= new-resolution resolution)
        state
        (assoc state
               :resolution new-resolution)))))

(defn render! [{:keys [gl resolution]
                :as state}]
  (with-context gl
    (run-purefrag-shader! keyboard-frag-glsl
                          resolution
                          {"size" resolution
                           "white-keys-down?" (vec (repeat 7 false))
                           "black-keys-down?" (vec (repeat 7 false))}))
  state)

(defn update-page! [state]
  (-> state
      update-resolution!
      render!))

(defn init-page! [gl]
  (with-context gl
    (set-page-background-color (map (partial * 255) c/background-color))
    {:gl gl}))

(defn start-page! []
  (start-hollow! init-page! update-page! {:stencil? true}))

(defn pre-init []
  (js/window.addEventListener "load" start-page!)

  (js/eval "(function(){var script=document.createElement('script');script.onload=function(){var stats=new Stats();document.body.appendChild(stats.dom);requestAnimationFrame(function loop(){stats.update();requestAnimationFrame(loop)});};script.src='//mrdoob.github.io/stats.js/build/stats.min.js';document.head.appendChild(script);})()"))
