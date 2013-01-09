(ns cafe.core.data.order.shipping-method
  (:use [korma.db]
        [korma.core]))

(defentity shipping_methods)

(defn create [method]
  (insert shipping_methods
    (values method)))

(defn find-by-name [value]
  (first
    (select shipping_methods
      (where {:name value}))))