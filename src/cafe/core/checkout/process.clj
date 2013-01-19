(ns cafe.core.checkout.process
  (:require [cafe.core.data.order :as order]
            [clojure.pprint :refer :all]
            [cafe.core.data.order.payment :as payment]
            [cafe.core.data.order.shipment :as shipment]
            [noir.session :as session]))

; (declare ^:dynamic customer-order)

(defn order-errors []
  (pprint (order/get-errors)))

(defn init-order [& [customer-id]]
  (order/create {:user_id customer-id
                 :status_id 1
                 :total 0
                 :purchased_at nil
                 :state "cart"}))

(defn load-order []
  (order/find-by-id "7"))
  ; (if-let [order-id (session/get :order-id)]
  ;   (order/find-by-id "7")
  ;   (let [new-order (init-order (session/get :user-id))]
  ;     (session/put! :order-id (:id new-order))
  ;     new-order)))

(defn add-product [product-id qty unit-price options]
  (let [order (load-order)
        items (:line_items order)
        product-id-key (keyword (str product-id))]
    (if (contains? items product-id-key)
      (order/update-item (update-in (get items product-id-key) [:quantity] + qty))
      (order/insert-item (:id order)
                         {:product_id product-id
                          :price unit-price
                          :quantity qty}))))

; (defn add-product [product-id qty unit-price options]
;   (let [items (:line-items @customer-order)
;         product-id-keyword (keyword (str product-id))]
;     (if (contains? items product-id-keyword)
;       (swap! customer-order
;              #(assoc % :line-items (update-in items [product-id-keyword :quantity] + qty) ))
;       (swap! customer-order
;              #(assoc % :line-items (conj items (order/insert-item (:id %) {:product_id product-id :quantity qty})))))))

(def sample-order { :user_id 1
                    :total 7.98
                    :billing_address_id 1
                    :shipping_address_id 1
                    :status_id 1
                    :special_instructions "Some instructions"
                    :line-items '(
                    {:product_id 1 :quantity 3 :price 2.99}
                    {:product_id 2 :quantity 1 :price 4.99}
                  )})

;; An order checkout process starts in the shopping cart. Then transitions to
;; the following states:
;; cart     -> address
;; address  -> delivery
;; delivery -> payment
;; payment  -> confirm
;; confirm  -> complete