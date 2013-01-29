(ns cafe.core.checkout.process
  (:require [cafe.core.data.order :as order]
            [clojure.pprint :refer :all]
            [cafe.core.data.order.payment :as payment]
            [cafe.core.data.order.shipment :as shipment]
            [noir.session :as session]))

(declare remove-product update-product)

;; An order checkout process starts in the shopping cart. Then transitions to
;; the following states:
;; cart     -> address
;; address  -> delivery
;; delivery -> payment
;; payment  -> confirm
;; confirm  -> complete

(def sample-order {:id 7
                   :user_id 1
                   :billing_address_id 1
                   :shipping_address_id 1
                   :status_id 1
                   :state "cart"
                   :special_instructions "Some instructions"
                   :line-items {
                     :1 {:product_id 1 :quantity 3 :price 2.99 :description "HipHat"}
                     :2 {:product_id 2 :quantity 1 :price 4.99 :description "Other Product"}}})

(defn init-order [& [customer-id]]
  (order/create {:user_id customer-id
                 :status_id 1
                 :total 0
                 :purchased_at nil
                 :state "cart"}))

(defn load-order []
  sample-order)
  ; (order/find-by-id 7))
  ; (if-let [order-id (session/get :order-id)]
  ;   (order/find-by-id order-id)
  ;   (let [new-order (init-order (session/get :user-id))]
  ;     (session/put! :order-id (:id new-order))
  ;     new-order)))

(defn add-product [order-map product-id qty unit-price options]
  (let [items (:line_items order-map)
        product-id-key (keyword (str product-id))]
    (if (contains? items product-id-key)
      (update-product order-map product-id qty unit-price options)
      (order/insert-item (:id order-map)
                         {:product_id product-id
                          :price unit-price
                          :quantity qty}))))

(defn insert-product [order-map product-id qty unit-price options]
  (let [product-id-key (keyword (str product-id))
        new-item (order/insert-item (:id order-map)
                           {:product_id product-id
                            :price unit-price
                            :quantity qty})]
    (order/update-total (assoc-in order-map [:line_items product-id-key] new-item)))))

(defn update-product [order-map product-id qty unit-price options]
  (let [items (:line_items order-map)
        product-id-key (keyword (str product-id))
        item (get items product-id-key)]
    (if (> (+ qty (:quantity item)) 0)
      (when-let [updated-item (order/update-item 
                                (update-in
                                  (get items product-id-key)
                                  [:quantity] + qty))]
        (order/update-total (assoc-in order-map [:line_items product-id-key] updated-item)))
      (remove-product))))

(defn remove-product [order-map product-id]
  (if (order/remove-item (get-in order-map [:line_items (keyword (str product-id))]))
    (let [updated-order ((update-in order-map [:line_items] dissoc (keyword (str product-id))))]
      (order/update-total updated-order))))
