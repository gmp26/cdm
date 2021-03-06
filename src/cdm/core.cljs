(ns ^:figwheel-always cdm.core
    (:require [rum.core :as rum]
              [cljs.reader :as reader]
              [cljs.core.async :as async :refer [<! >! chan close! sliding-buffer put! alts! timeout]]
              [clojure.set :refer (intersection)]
              [cljsjs.react]
              )
    (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
)

(enable-console-print!)

;;;
;; define game state once so it doesn't re-initialise on reload.
;; figwheel counter is a placeholder for any state affected by figwheel live reload events
;;;

(defn el [id] (.getElementById js/document id))

(defn pc [n] (str (.toFixed n 2) "%"))

(rum/defc light [light-class on-class]
  [:div
   [:div {:class light-class}
    [:div {:class (str on-class " " light-class)}]]
   [:span.etching {:class (if (= on-class "on") "etch-on" "etch-off")} "40W ECO"]])

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
(declare int-in-range)

(defn choose-b
  [a]
  (cond
    (= a 0)
    (int-in-range 2 13)

    (integer? a)
    (int-in-range -5 6)

    :else
    (/  (inc (int-in-range 1 12)) 2)
            ))
(defn choose-c
  [a b]
  (if (= a 0)
    (int-in-range 2 13)
    (Math.round (+ (/ (* b b 0.25) a) (int-in-range -12 13)))))

(defn new-quadratic
  "generate new random quadratic coefficients"
  [level]
  (let [a (cond
            (= level :lev3)
            (/ (int-in-range -9 10) 2)

            (= level :lev2)
            (rand-nth [0 0.5 1 2 3])

            :else 0
            )
        b (choose-b a)
        c (choose-c a b)]
    {:a a :b b :c c}))

(defn new-quadratics
  "assoc a new set of 4 distinct rules onto given map for given level"
  [level]
  (let [qrules (into [] (take 4 (distinct (map #(new-quadratic level) (range 7)))))]
    (conj {:level level}
          {:yellow (qrules 0)
           :red (qrules 1)
           :blue (qrules 2)
           :green (qrules 3)})))

(defn handle-reload [event level]
  (swap! game-state #(conj % (new-quadratics level)))
  (.preventDefault event)
  (.stopPropagation event))

(rum/defc title []
  [:.title.no-select "Charlie's Delightful Machine"])

(rum/defc static-content []
  [:p.no-select "Enter some whole numbers in the box and so discover the rules which switch on each of the lights."])

(defn handle-change [event]
  (let [n (int (.-value (.-target event)))]
    (swap! game-state #(assoc % :n n))))


(defn handle-plus
  "plus clicked"
  [event]
  (swap! game-state #(update % :n inc))
  (.preventDefault event)
  (.stopPropagation event))

(defn handle-minus
  "minus clicked"
  [event]
  (swap! game-state #(update % :n dec))
  (.preventDefault event)
  (.stopPropagation event))

(defn toggle-class
  "get a css class "
  [level]
  (if (= (:level @game-state) level)
    "rules lit"
    "rules"))

(rum/defc four-bulbs < rum/reactive [[a b c d]]
  [:.game-root
   (title)
   (coloured-bulb (:class a) (:state a) 0)
   (coloured-bulb (:class b) (:state b) 0)
   (coloured-bulb (:class c) (:state c) 0)
   (coloured-bulb (:class d) (:state d) 0)
   [:.static.no-select
    (static-content)
    [:.spinner
     [:button.up.no-select {:on-click handle-plus
                  :on-touch-start handle-plus} "+"]
     [:button.down.no-select {on-click handle-minus
                    on-touch-start handle-minus} "-"]
     [:input.num {:value (:n (rum/react game-state))
                  :type "number"
                  :pattern "\\d*"
                  :on-change handle-change}]]
    [:button#lev0.no-select {:class (toggle-class :lev1)
                         :on-click #(handle-reload % :lev1)
                         :on-touch-end #(handle-reload % :lev1)} "Level 1 - linear only"]
    [:button#lev1.rules.no-select {:class (toggle-class :lev2)
                         :on-click #(handle-reload % :lev2)
                         :on-touch-end #(handle-reload % :lev2)} "Level 2"]
    [:button#lev2.rules.no-select {:class (toggle-class :lev3)
                         :on-click #(handle-reload % :lev3)
                                   :on-touch-end #(handle-reload % :lev3)} "Level 3"]
    [:p {:style {:clear "both"
                 :padding-top "10px"
                 }} "Pressing a level button generates a new rule"]
    #_[:p {:style
           {:color "#888"}}
       (str (condp = (:level (rum/react game-state))
              :lev1 "Linear "
              :lev2 "Linear or quadratic"
              :lev3 "Linear or quadratic") " rule selected")]
]])

(defn linear?
  "returns true if n = ka + b for some integer k"
  [n a b]
  (if (= a 0)
    (= n b)
    (integer? (/ (- n b) a))))

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
  (+ a (rand-int (- b a))))

(defn quadratic-list
  "List the numbers that are in the quadratic sequence where |a_k| < n-max"
  [n-max a b c]
  (sort (take 20 (sort-by Math.abs (for [k (range n-max)
                                          :let [pos (quadratic? k a b c)
                                                neg (quadratic? (- k) a b c)]
                                          :when (or pos neg)]
                                      (if pos k (- k)))))))

(defn bool->on-off
  [b]
  (if b "on" "off"))

(def game-state (atom (conj (new-quadratics :lev1) {:n 0 :level :lev1})))


(defn bulb-state
  "return a map containing class and on-off state of a bulb"
  [gs color n]
  (let [rule (color gs)]
    {:class (name color) :state (bool->on-off (quadratic? n (:a rule) (:b rule) (:c rule)))}))

(rum/defc cdm2 < rum/reactive []
  (let [gs (rum/react game-state)
        n (:n gs)]

    (four-bulbs [(bulb-state gs :yellow n)
                 (bulb-state gs :red n)
                 (bulb-state gs :blue n)
                 (bulb-state gs :green n)])))

 ;;
 ;; Put the app/game in here
 ;;
(rum/defc game-container < rum/reactive []
  "Charlies Delightful Machine - 2"
  (cdm2))

 ;;
 ;; mount main component on html game element
 ;;


(if-let [node (el "game")]
  (rum/mount (game-container) node))

 ;;
 ;; optionally do something on game reload
 ;;

(defn on-js-reload []
  (swap! game-state update-in [:__figwheel_counter] inc))
