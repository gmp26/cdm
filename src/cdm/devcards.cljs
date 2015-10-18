(ns cdm.devcards

  (:require
   [rum.core :as rum]
   [cljs.test :as t]
   ;[sablono.core :as sab]
   )
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]]))

(rum/defc rum-test []
  [:div
   [:h2 "A devcard created with Rum"]])

#_(defcard "##Charlie's Delightful Machine

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

(deftest test1 []
  (t/is (= 2 3)))

(deftest test2 []
  (t/is (= 2 2)))

(deftest test3 []
  (t/is (even? 2)))

;;
;;------- unused below
;;

#_(rum/defc app-view []
  [:div "Build the app here"])

#_(defn main []
  ;; conditionally start the app based on wether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (rum/mount (app-view) node)))

#_(main)
