(ns ^:figwheel-always cdm.core
    (:require [rum.core :as rum]
              [cljs.reader :as reader]
              [clojure.set :refer (intersection)]
              [cljsjs.react]
              ;[jayq.core :refer ($)]
              )
    ;(:require-macros [jayq.macros :refer [ready]])
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

(rum/defc centre-round [switched-on color percent & content]
  (let [half (/ (- 100 percent) 2)]
    [:.round {:class (name switched-on)
              :style {:background-color color
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

(comment
  (def light-shades
    {
     :red [{:on "#ff0000" :off "#882222" :pc 80}
           {:on "#ffaaff" :off "#884422" :pc 20}
           {:on "#ff7777" :off "#AA4444" :pc 95}
           {:on "#ff0000" :off "#884400" :pc 95}
           {:on "#cc0000" :off "#880000" :pc 95}
           {:on "#880044" :off "#880000" :pc 95}]
     :yellow [{:on "#ff0000" :off "#882222" :pc 80}
              {:on "#ffaaff" :off "#884422" :pc 20}
              {:on "#ff7777" :off "#AA4444" :pc 95}
              {:on "#ff0000" :off "#884400" :pc 95}
              {:on "#cc0000" :off "#880000" :pc 95}
              {:on "#880044" :off "#880000" :pc 95}]
     :green [{:on "#ff0000" :off "#882222" :pc 80}
             {:on "#ffaaff" :off "#884422" :pc 20}
             {:on "#ff7777" :off "#AA4444" :pc 95}
             {:on "#ff0000" :off "#884400" :pc 95}
             {:on "#cc0000" :off "#880000" :pc 95}
             {:on "#880044" :off "#880000" :pc 95}]
     :blue [{:on "#ff0000" :off "#882222" :pc 80}
            {:on "#ffaaff" :off "#884422" :pc 20}
            {:on "#ff7777" :off "#AA4444" :pc 95}
            {:on "#ff0000" :off "#884400" :pc 95}
            {:on "#cc0000" :off "#880000" :pc 95}
            {:on "#880044" :off "#880000" :pc 95}]
     })

  (rum/defc red-light [switch]
    (let [shade-data #(nth (:red light-shades) %)
          shades #(switch (shade-data %))
          pc #(pc (shade-data %))
          disc #(centre-round switch %1 %2)]

      (reduce (:red-light-shades))
      )))

(rum/defc red-light [switch]
  (if (= switch :on)
    (->> (centre-round :on  "#ff0000" 80)
         (centre-round :on  "#ffaaff" 20)
         (centre-round :on  "#ff7777" 95)
         (centre-round :on  "#ff0000" 95)
         (centre-round :on  "#cc0000" 95)
         (centre-round :on  "#880044" 85))
    (->> (centre-round :off "#882222" 80)
         (centre-round :off "#884422" 20)
         (centre-round :off "#AA4444" 95)
         (centre-round :off "#884400" 95)
         (centre-round :off "#880000" 95)
         (centre-round :off "#880000" 85))))

(rum/defc yellow-light [switch]
  (if (= switch :on)
    (->> (centre-round :on  "#ffff00" 80)
         (centre-round :on  "#ffffff" 20)
         (centre-round :on  "#ffffcc" 95)
         (centre-round :on  "#ffff00" 95)
         (centre-round :on  "#dddd00" 95)
         (centre-round :on  "#aaaa44" 85))
    (->> (centre-round :off "#888800" 80)
         (centre-round :off "#668844" 20)
         (centre-round :off "#aa8800" 95)
         (centre-round :off "#887700" 95)
         (centre-round :off "#666600" 95)
         (centre-round :off "#444400" 85))))

(rum/defc green-light [switch]
  (if (= switch :on)
    (->> (centre-round :on  "#55ff55" 80)
         (centre-round :on  "#bbffff" 20)
         (centre-round :on  "#aaffaa" 95)
         (centre-round :on  "#00ff00" 95)
         (centre-round :on  "#00cc00" 95)
         (centre-round :on  "#008800" 85))
    (->> (centre-round :off "#228822" 80)
         (centre-round :off "#228844" 20)
         (centre-round :off "#448844" 95)
         (centre-round :off "#008844" 95)
         (centre-round :off "#008800" 95)
         (centre-round :off "#00aa00" 85))))

(rum/defc blue-light [switch]
  (if (= switch :on)
    (->> (centre-round :on  "#22bbff" 80)
         (centre-round :on  "#aaffff" 20)
         (centre-round :on  "#88eeff" 95)
         (centre-round :on  "#00aaff" 95)
         (centre-round :on  "#0000ff" 95)
         (centre-round :on  "#000088" 85))
    (->> (centre-round :off "#222288" 80)
         (centre-round :off "#224488" 20)
         (centre-round :off "#444488" 95)
         (centre-round :off "#004488" 95)
         (centre-round :off "#000088" 95)
         (centre-round :off "#000088" 85))))
;;
;; Put the app/game in here
;;
(rum/defc game-container < rum/reactive []
  [:.col-md-12
   [:.box.row
    [:.col-md-6.light
     (red-light :on)]
    [:.col-md-6.light
     (yellow-light :on)]
    ]
   [:.box.row
    [:.col-md-6.light
     (green-light :off)
     ]
    [:.col-md-6.light
     (blue-light :on)
     ]
    ]
   ])

;;
;; mount main component on html game element
;;

(if-let [node (el "game")]
  (rum/mount (game-container) node))

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
