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


(rum/defc responsive-black-square [& content]
  [:.black-square
   content])

(rum/defc light [light-class on-class]
  [:div
   [:div {:class light-class}
    [:div {:class (str on-class " " light-class)}]]
   [:span.etching {:class (if (= on-class "on") "etch-on" "etch-off")} "40W ECO"]])

(rum/defc coloured-light [class state]
  (let [spec (str class " lamp")]
    (responsive-black-square
     (light spec state)))
  )

(rum/defc four-lights [[a b c d]]
  [:div.full-square
   [:.quarter-square.top.left
    (coloured-light (:class a) (:state a))]
   [:.quarter-square.top.right
    (coloured-light (:class b) (:state b))]
   [:.quarter-square.bottom.left
    (coloured-light (:class c) (:state c))]
   [:.quarter-square.bottom.right
    (coloured-light (:class d) (:state d))]])

;;
;; Put the app/game in here
;;
(rum/defc game-container < rum/reactive []
  (four-lights [{:class "yellow" :state "on"}
                {:class "red" :state "off"}
                {:class "blue" :state "off"}
                {:class "green" :state "off"}]
               ))

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
  (if (= a 0)
    (= n b)
    (integer? (/ (- n b) a)))
  )

(defn square?
  "returns true if n is a square number"
  [n]
  (cond
    (< n 0) false
    (= n 0) true
    :else (let [root (Math.round (Math.sqrt n))]
            (< (Math.abs (-  (/ (* root root) n) 1)) 1e-8))))

(defn quadratic?
  "returns true if n is ak^2 + bk + c for some integer k"
  [n a b c]
  (let [disc (- (* b b) (* 4 a c))]
    (cond
      (= a 0) (linear? n b c)
      (< disc 0) false
      (square? disc) true
      :else false)))

(defn int-in-range
  "return a function that generates an integer within [a,b)"
  [a b]
  (+ a (rand-int (- b a)))
  )
