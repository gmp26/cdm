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
     (light spec state))))

(rum/defc coloured-bulb [class state deg]
  (let [url (str  "url(/assets/" class "-" state ".png)")]
    [:div.bulb {:style {:background-image url
                        :-webkit-transform (str "rotateZ(" deg "deg)")
                        :transform (str "rotateZ(" deg ")")
                        }}]))

(declare static-content)
(declare game-state)
(declare handle-change)
(declare handle-reload)

(rum/defc four-lights < rum/reactive [[a b c d]]
  [:div.full-square
   [:.quarter-square.top.left
    (coloured-light (:class a) (:state a))]
   [:.quarter-square.top.right
    (coloured-light (:class b) (:state b))]
   [:.quarter-square.bottom.left
    (coloured-light (:class c) (:state c))]
   [:.quarter-square.bottom.right
    (coloured-light (:class d) (:state d))]
   [:.static {:style {:zoom 0.8
                      :right "28%"
                      :top "28%"
                      :border "none"
                      :background-color "rgba(0,0,0,0.7)"}}
    (static-content)
    [:input.num {:value (:n (rum/react game-state))
                 :type "number"
                 :on-change handle-change}]
    [:button.rules {:on-click handle-reload
                    :on-touch-end handle-reload} "Change rules"]]])

(declare game-state)
(declare new-gen-set)

(defn handle-reload [event]
  (swap! game-state #(assoc % :generators (new-gen-set)))
  (.preventDefault event)
  (.stopPropagation event)
  )

(rum/defc title []
  [:.title "Charlie's Delightful Machine"

   ])

(rum/defc static-content []
  [:p
   "Enter some whole numbers in the box and so discover
the rules which switch on each of the lights."])

(defn handle-change [event]
  (let [n (.-value (.-target event))]
    (swap! game-state #(assoc % :n n)))
  #_(.log js/console (.-value (.-target event))))

(rum/defc four-bulbs < rum/reactive [[a b c d]]
  [:.game-root
   (title)
   (coloured-bulb (:class a) (:state a) 0)
   (coloured-bulb (:class b) (:state b) 0)
   (coloured-bulb (:class c) (:state c) 0)
   (coloured-bulb (:class d) (:state d) 0)
   [:.static
    (static-content)
    [:input.num {:value (:n (rum/react game-state))
                 :type "number"
                 :pattern "d*"
                 :on-change handle-change}]
    [:button.rules {:on-click handle-reload
             :on-touch-end handle-reload} "Change rules"]]])

(defn linear?
  "returns true if n = ka + b for some integer k"
  [n a b]
  (if (= a 0)
    (= n b)
    (integer? (/ (- n b) a)))
  )

(def epsilon 1e-10)

#_(defn square?
  "returns true if n is a square number"
  [n]
  (cond
    (< n 0) false
    (= n 0) true
    :else (let [root (Math.round (Math.sqrt n))]
            (< (Math.abs (-  (/ (* root root) n) 1)) epsilon))))

(defn square?
  "returns true if n is a square number"
  [n]
  (cond
    (< n 0) false
    (= n 0) true
    :else (let [root (Math.round (Math.sqrt n))]
            (< (Math.abs (- (* root root) n)) epsilon))))




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

ƒ_(defn random-quadratic-test-generator
  "doc-string"
  []
  (let [a (int-in-range -3 4)
        b (int-in-range -5 6)
        c (int-in-range -20 21)]
    (fn [n] (if (quadratic? n a b c) "on" "off"))))

(defn random-quadratic-test-generator
  "doc-string"
  []
  (let [two-a (int-in-range -6 7)
        a (/ two-a 2)
        b (if (integer? a)
            (int-in-range -5 6)
            (+ 0.5 (int-in-range -5 5)))
        c (int-in-range -20 21)]
    (fn [n] (if (quadratic? n a b c) "on" "off"))))




(defn new-gen-set []
  (vec (for [i (range 4)]
         (random-quadratic-test-generator)
         )))

(def game-state (atom {:n 0
                       :generators (new-gen-set)}))

(rum/defc cdm1 < rum/reactive []
  (let [gs (rum/react game-state)
        n (:n gs)
        tests (:generators gs)]

    (four-lights [{:class "yellow" :state ((tests 0) n)}
                  {:class "red" :state ((tests 1) n)}
                  {:class "green" :state ((tests 2) n)}
                  {:class "blue" :state ((tests 3) n)}])))

(rum/defc cdm2 < rum/reactive []
  (let [gs (rum/react game-state)
        n (:n gs)
        tests (:generators gs)]

    (four-bulbs [{:class "yellow" :state ((tests 0) n)}
                 {:class "red" :state ((tests 1) n)}
                 {:class "blue" :state ((tests 2) n)}
                 {:class "green" :state ((tests 3) n)}])))


 ;;
 ;; Put the app/game in here
 ;;

(rum/defc game-container < rum/reactive []
  (cdm2)
  #_(four-lights [{:class "yellow" :state "on"}
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
