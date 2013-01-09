(ns cafe.core.data.order-test
  (:use clojure.test
        cafe.core.data.order))

(deftest test-order-creation
  (let [order {:total 1}]
    (is (true) "COMMENT")))