(ns cafe.core.data.order.payment
  (:use [korma.db]
        [korma.core]
        [cafe.core.data.validation]))

(defentity orders)

;; ### Payments table fields
;; * id :integer
;; * order_id :reference orders
;; * payment_method :reference payment_methods
;; * amount :float
;; * status :string
;; * created_at :datetime
;; * updated_at :datetime

(defentity payments
  (belongs-to orders {:fk :order_id}))

(defn create [payment order-id]
  (insert payments
    (values (assoc-in payment :order_id order-id))))

(defn get-payments [order-id]
  (select payments
    (where {:order_id order-id})))