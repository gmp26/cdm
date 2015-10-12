(ns ^:figwheel-always cdm.core
    (:require [rum.core :as rum]
              [cljs.reader :as reader]
              [clojure.set :refer (intersection)]
              [cljsjs.react]
              [jayq.core :refer ($)]
              )
    (:require-macros [jayq.macros :refer [ready]])
)

(enable-console-print!)

;;;
;; define game state once so it doesn't re-initialise on reload.
;; figwheel counter is a placeholder for any state affected by figwheel live reload events
;;;
(defonce game (atom {:title "Charlie's Delightful Machine"
                     :__figwheel_counter 0}))

(defn el [id] (.getElementById js/document id))


(rum/defc home < rum/reactive []
  [:div.row
])

(defn pc [n] (str (.toFixed n 2) "%"))

(rum/defc centre-round [color percent & content]
  (let [half (/ (- 100 percent) 2)]
    [:.round {:style {:box-shadow (str "inset 5em 1em rgba(255,255,0,0.3)")
                      :background-color color
                      :width (pc percent)
                      :padding-bottom (pc percent)
                      :top (pc  half)
                      :left (pc  half)}}
     content]))

(rum/defc centre-round-off [color percent & content]
  (let [half (/ (- 100 percent) 2)]
    [:.round {:style {:box-shadow (str "inset 1em 5em rgba(0,0,0,0.3)")
                      :background-color color
                      :width (pc percent)
                      :padding-bottom (pc percent)
                      :top (pc  half)
                      :left (pc  half)}}
     content]))


(rum/defc red-light [on]
  (if on
    (->> (centre-round "#ff6644" 80)
         (centre-round "#ffffcc" 20)
         (centre-round "#ffbbdd" 95)
         (centre-round "#ff0000" 95)
         (centre-round "#cc0000" 95)
         (centre-round "#880044" 95))
    (->> (centre-round-off "#882222" 80)
         (centre-round-off "#884422" 20)
         (centre-round-off "#AA4444" 95)
         (centre-round-off "#884400" 95)
         (centre-round-off "#880000" 95)
         (centre-round-off "#880000" 95))))

(rum/defc yellow-light [on]
  (if on
    (->> (centre-round "#ffff00" 80)
         (centre-round "#ffffff" 20)
         (centre-round "#ffffcc" 95)
         (centre-round "#ffff00" 95)
         (centre-round "#dddd00" 95)
         (centre-round "#aaaa44" 95))
    (->> (centre-round-off "#888800" 80)
         (centre-round-off "#668844" 20)
         (centre-round-off "#aa8800" 95)
         (centre-round-off "#887700" 95)
         (centre-round-off "#666600" 95)
         (centre-round-off "#444400" 95))))

(rum/defc green-light [on]
  (if on
    (->> (centre-round "#ccffcc" 80)
         (centre-round "#44ffff" 20)
         (centre-round "#ccffee" 95)
         (centre-round "#00ffbb" 95)
         (centre-round "#00ff00" 95)
         (centre-round "#008800" 95))
    (->> (centre-round-off "#228822" 80)
         (centre-round-off "#228844" 20)
         (centre-round-off "#448844" 95)
         (centre-round-off "#008844" 95)
         (centre-round-off "#008800" 95)
         (centre-round-off "#00aa00" 95))))

(rum/defc blue-light [on]
  (if on
    (->> (centre-round "#ccccff" 80)
         (centre-round "#44ffff" 20)
         (centre-round "#cceeff" 95)
         (centre-round "#00ffff" 95)
         (centre-round "#0000ff" 95)
         (centre-round "#000088" 95))
    (->> (centre-round-off "#222288" 80)
         (centre-round-off "#224488" 20)
         (centre-round-off "#444488" 95)
         (centre-round-off "#004488" 95)
         (centre-round-off "#000088" 95)
         (centre-round-off "#000088" 95))))
;;
;; Put the app/game in here
;;
(rum/defc game-container < rum/reactive []
  [:.col-md-12
   [:.box.row
    [:.col-md-6.light
     (red-light false)]
    [:.col-md-6.light
     (yellow-light false)]
    ]
   [:.box.row
    [:.col-md-6.light
     (green-light false)
     ]
    [:.col-md-6.light
     (blue-light false)
     ]
    ]
   ])

;;
;; mount main component on html game element
;;
(rum/mount (game-container) (el "game"))

;;
;; optionally do something on game reload
;;
(defn on-js-reload []
  (swap! game update-in [:__figwheel_counter] inc))


(defn linear?
  "returns true if n = ka + b for some integer k"
  [n a b]
  (integer? (/ (- n b) a))
  )

(defn int-in-range
  "return a function that generates an integer within [a,b)"
  [a b]
  #(+ a (rand-int (- b a)))
  )


(defrecord Lights [on-class  on-test])

(def lights
  [(Lights. "yellow" linear?)])

(defn restart
  "generate a new set of rules on reload"
  []
  (swap! game #(assoc %
                     :a (map (int-in-range 2 12) (range (count lights)))))
  )
