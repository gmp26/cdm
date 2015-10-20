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

(def epsilon 1e-10)

(defn square?
  "returns true if n is a square number"
  [n]
  (cond
    (< n 0) false
    (= n 0) true
    :else (let [root (Math.round (Math.sqrt n))]
            (< (Math.abs (-  (/ (* root root) n) 1)) epsilon))))

(defn disc [a b c]
  (- (* b b) (* 4 a c)))

(defn near-integer? [n]
  (< (Math.abs (- n (Math.round n))) epsilon))

(defn quadratic?
  "returns true if n is ak^2 + bk + c for some integer k"
  [n a b c]
  (cond
    (= a 0) (linear? n b c)
    (< disc 0) false
    (let [rdisc (Math.sqrt (disc a b (- c n)))
          two-a (* 2 a)]
      (or (near-integer? (/ (- rdisc b) two-a)) (near-integer? (/ (+ rdisc b) two-a)))) true
      :else false))


(defn int-in-range
  "return a function that generates an integer within [a,b)"
  [a b]
  (+ a (rand-int (- b a)))
  )

(defn quadratic-list
  "List the numbers that are in the quadratic sequence where |a_k| < n-max"
  [n-max a b c]
  (sort (take 20 (sort-by Math.abs (for [k (range n-max)
                                          :let [pos (quadratic? k a b c)
                                                neg (quadratic? (- k) a b c)]
                                          :when (or pos neg)]
                                      (if pos k (- k)))))))

(defn random-quadratic-test-generator
  "doc-string"
  []
  (let [a (int-in-range 0 3)
        b (int-in-range 0 10)
        c (int-in-range 0 20)]
    (fn [n] (if (quadratic? n a b c) "on" "off"))))

(def game-state (atom {:n 0
                       :generators (for [i (range 4)]
                                    (random-quadratic-test-generator)
                                    )}))


(rum/defc cdm1 < rum/reactive []
  (let [gs (rum/react game-state)
        n (:n gs)
        tests (:generators gs)]
    (four-lights [{:class "yellow" :state ((nth tests 0) n)}
                  {:class "red" :state ((nth tests 1) n)}
                  {:class "green" :state ((nth tests 2) n)}
                  {:class "blue" :state ((nth tests 3) n)}])))
