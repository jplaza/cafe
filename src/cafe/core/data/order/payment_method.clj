(ns cafe.core.data.order.payment-method
  (:use [korma.db]
        [korma.core]))

(defentity payment_methods)

(defn create [method]
  (insert payment_methods
    (values method)))

(defn find-by-name [value]
  (first
    (select payment_methods
      (where {:name value}))))