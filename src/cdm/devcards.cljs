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

(defcard "##Charlie's Delightful Machine

This is a stack of development cards intended to upgrade the existing js
implementation.

###Aims

* Extend sensibly to non-linear relations.
* Allow configuration using URL parameters.
* Responsive sizing in mobile.
* Keep it pretty.")

(defcard my-first-card
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
  (t/is (not (core/linear? 28 7 6)) "not linear")

  )
