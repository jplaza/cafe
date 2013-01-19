(ns cafe.core.data.validation-test
  (:use clojure.test
        cafe.core.data.validation))

(deftest test-rule-insertion
  (do
    (validate :user :name :presence)
    (is (contains? (:user @rules) :name) "The rules map should contain the :name key")))

(deftest test-defvalid
  (testing "defvalid macro"
    (do
      (defvalid user)
      (is (fn? valid?)))
    (is (fn? get-errors))))