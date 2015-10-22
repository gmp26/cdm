(ns cdm.devcards

  (:require
   [rum.core :as rum]
   [cljs.test :as t]
   [cdm.core :as core]
   ;;[sablono.core :as sab]
   )
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]]))

(rum/defc rum-test []
  [:div
   [:h2 "A devcard created with Rum"]])
()
(defcard "##Charlie's Delightful Machine

This is a stack of development cards intended to upgrade the existing js
implementation.

###Aims

* Extend sensibly to non-linear relations.
* Allow configuration using URL parameters.
* Responsive sizing in mobile.
* Keep it pretty.")

#_(defcard my-first-card
  "Test that Tonsky __Rum__ based cards work"
  (rum-test)
  {}
  {:hidden false}
  )

(deftest should-fail []
  (t/is (= 2 3)))

(deftest should-pass []
  (t/is (even? 2)))

(defcard single-light
  (core/coloured-light "yellow" "on")
  {}
  {:padding false})

(defcard four-lights-on-off
  (core/four-lights [{:class "yellow" :state "on"}
                {:class "red" :state "off"}
                {:class "blue" :state "on"}
                {:class "green" :state "off"}]
               )
  {}
  {:padding false})

(defcard four-lights-off-on
  (core/four-lights [{:class "yellow" :state "off"}
                {:class "red" :state "on"}
                {:class "blue" :state "off"}
                {:class "green" :state "on"}]
               )
  {}
  {:padding false})

;;;
;; Rules
;;;
(deftest linearity-tests
  "n = ka + b for some k if (linear? n a b)"
  (t/is (core/linear? 27 7 6) "k=3")
  (t/is (core/linear? -1 0 -1) "k=anything")
  (t/is (core/linear? -31 1 -32) "k=1")
  (t/is (core/linear? 8 -3 2) "k=1")
  (t/is (not (core/linear? 28 7 6)) "not linear"))

(deftest int-in-range
  "test 100 random integers in range [-5,5)"
  (t/is (every?
         (fn [j] (and (>= j -5) (<= j 5)))
         (for [i (range 100)] (core/int-in-range -5 6)))))

(deftest square?
  "square? should identify square numbers"
  (t/is (not (core/square? -1)))
  (t/is (core/square? 0))
  (t/is (core/square? 1))
  (t/is (not (core/square? 2)))
  (t/is (not (core/square? 3)))
  (t/is (core/square? 4))
  (t/is (core/square? 1e10))
  (t/is (core/square? 1e100))
  (t/is (not (core/square? 1e19))  "tolerance limit around here")
  (t/is (core/square? 1e21) "wrong due to choice of ε = 1e-10")
  )

(deftest disc
  "should calculate the discriminant"
  (t/is (core/disc 0 0 0) 0)
  (t/is (core/disc 2 7 3) 25)
  )

(deftest near-integer
  "should detect integers masquerading as reals"
  (t/is (core/near-integer? (/ 21.0 7)))
  (t/is (core/near-integer? (Math.sqrt (* 99 99))))
  )

(deftest quadratic-tests
  "n = ak^2 + bk + c for some integer k if (quadratic? n a b c)"
  (t/is (core/quadratic? 0 0 0 0))
  (t/is (core/quadratic? 1 1 0 0))
  (t/is (core/quadratic? 4 1 0 0))
  (t/is (core/quadratic? 2 2 0 0))
  (t/is (core/quadratic? 8 2 0 0))
  (t/is (core/quadratic? 18 2 0 0))
  (t/is (core/quadratic? 25 1 0 0))
  (t/is (core/quadratic? 64 1 0 0))
  (t/is (core/quadratic? 65 1 0 1))
  (t/is (not (core/quadratic? 65 1 0 2)))
  (t/is (core/quadratic? 64 1 2 1))
  (t/is (core/quadratic? 64 1 4 4))
  (t/is (core/quadratic? 16 1 4 4))
  (t/is (core/quadratic? 4 1 4 4))
  (t/is (core/quadratic? 1 1 4 4))
  (t/is (core/quadratic? 10 0.5 0.5 0))
  (t/is (core/quadratic? 6 0.5 0.5 0))
  (t/is (not (core/quadratic? 7 0.5 0.5 0)))
  (t/is (core/quadratic? 6 1 1 0))
  )

(defcard twenty-quadratic-terms
  [
   (core/quadratic-list 1000 1 0 0)
   (core/quadratic-list 1000 2 0 0)
   (core/quadratic-list 1000 3 0 0)
   (core/quadratic-list 1000 2 2 0)
   (core/quadratic-list 1000 1 0 1)
   (core/quadratic-list 1000 1 0 2)
   (core/quadratic-list 1000 1 2 1)
   (core/quadratic-list 1000 1 4 4)
   (core/quadratic-list 1000 0.5 0.5 0)
   (core/quadratic-list 1000 1 -3 0)
   (core/quadratic-list 1000 3 -10 0)
   ])

(defcard ten-random-quadratic-sequences
  (for [q (range 10)]
    (let [a (rand-nth [(core/int-in-range 0 3)])
          b (core/int-in-range 0 10)
          c (core/int-in-range 0 20)]
      {:a a :b b :c c :seq (core/quadratic-list 1000 a b c)})))

(defcard generator
  (core/random-quadratic-test-generator))

(defcard machine-1
  (core/cdm1))

(defcard bulb-on
  (core/coloured-bulb "yellow" "on" ))

(defcard bulb-off
  (core/coloured-bulb "yellow" "off" ))

(defcard title
  (core/title))

(defcard static-content
  (core/static-content))

(defcard machine-2
  (core/cdm2))

#_(defcard four-bulbs
  (core/four-bulbs [{:class "yellow" :state "off"}
                    {:class "red" :state "on"}
                    {:class "blue" :state "on"}
                    {:class "green" :state "on"}]))
