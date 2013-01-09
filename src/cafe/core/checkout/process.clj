(ns cafe.core.checkout.process
  (:use )
  (:require [cafe.core.data.order :as order]
            [cafe.core.data.order.payment :as payment]
            [cafe.core.data.order.shipment :as shipment]))

(declare ^:dynamic customer-order)

(defn start-order [& [customer-id]]
  (order/create { :user_id customer-id
                  :status_id "cart"
                  }))

(defn add-to-cart [order]
  )

(defn add-product [product-id qty options]
  (let [items (:line-items @customer-order)
        product-id-keyword (keyword (str product-id))]
    (if (contains? items product-id-keyword)
      (swap! customer-order
             #(assoc % :line-items (update-in items [product-id-keyword :quantity] + qty) ))))
  (insert-item order-id (dissoc product :order-id)))

(defn swap-order []
  (let [items]
    (swap! customer-order #((:line-items %)))))

{ :user_id 1
  :total 7.98
  :billing_address_id 1
  :shipping_address_id 1
  :status_id 1
  :special_instructions "Some instructions"
  :line-items '(
  {:product_id 1 :quantity 3 :price 2.99}
  {:product_id 2 :quantity 1 :price 4.99}
)}

;; An order checkout process starts in the shopping cart. Then transitions to
;; the following states:
;; cart     -> address
;; address  -> delivery
;; delivery -> payment
;; payment  -> confirm
;; confirm  -> complete