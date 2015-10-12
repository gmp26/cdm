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
    [:.round {:style {:background-color color
                      :width (pc percent)
                      :padding-bottom (pc percent)
                      :top (pc  half)
                      :left (pc  half)}}
     content]))


(rum/defc red-light [on]
  (if on
    (->> (centre-round "#ff6644" 80)
         (centre-round "#ffffcc" 20)
         (centre-round "#ff6600" 95)
         (centre-round "#ff0000" 95)
         (centre-round "#cc0000" 95)
         (centre-round "#880044" 95))
    (->> (centre-round "#882222" 80)
         (centre-round "#884422" 20)
         (centre-round "#884444" 95)
         (centre-round "#884400" 95)
         (centre-round "#880000" 95)
         (centre-round "#880000" 95))))

(rum/defc yellow-light [on]
  (if on
    (->> (centre-round "#ffff00" 80)
         (centre-round "#ffffff" 20)
         (centre-round "#ffffcc" 95)
         (centre-round "#ffff00" 95)
         (centre-round "#dddd00" 95)
         (centre-round "#aaaa44" 95))
    (->> (centre-round "#222288" 10)
         (centre-round "#224488" 90)
         (centre-round "#444488" 95)
         (centre-round "#004488" 95)
         (centre-round "#000088" 95)
         (centre-round "#000088" 95))))

(rum/defc green-light [on]
  (if on
    (->> (centre-round "#ccccff" 10)
         (centre-round "#44ffff" 90)
         (centre-round "#ccffff" 95)
         (centre-round "#00ffff" 95)
         (centre-round "#0000ff" 95)
         (centre-round "#000088" 95))
    (->> (centre-round "#222288" 10)
         (centre-round "#224488" 90)
         (centre-round "#444488" 95)
         (centre-round "#004488" 95)
         (centre-round "#000088" 95)
         (centre-round "#000088" 95))))

(rum/defc blue-light [on]
  (if on
    (->> (centre-round "#ccccff" 10)
         (centre-round "#44ffff" 90)
         (centre-round "#ccffff" 95)
         (centre-round "#00ffff" 95)
         (centre-round "#0000ff" 95)
         (centre-round "#000088" 95))
    (->> (centre-round "#222288" 10)
         (centre-round "#224488" 90)
         (centre-round "#444488" 95)
         (centre-round "#004488" 95)
         (centre-round "#000088" 95)
         (centre-round "#000088" 95))))
;;
;; Put the app/game in here
;;
(rum/defc game-container < rum/reactive []
  [:.col-md-12
   [:.box.row
    [:.col-md-6.light
     (red-light true)]
    [:.col-md-6.light
     (yellow-light true)]
    ]
   [:.box.row
    [:.col-md-6.light
     (green-light true)
     ]
    [:.col-md-6.light
     (blue-light true)
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
