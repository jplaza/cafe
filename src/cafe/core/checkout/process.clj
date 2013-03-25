(ns cafe.core.checkout.process
  (:require [cafe.core.data.order :as order]
            [clojure.pprint :refer :all]
            [cafe.core.data.order.payment :as payment]
            [cafe.core.data.order.shipment :as shipment]
            [noir.session :as session]))

(declare remove-product insert-product update-product)

;; An order checkout process starts in the shopping cart. Then transitions to
;; the following states:
;; cart     -> address
;; address  -> delivery
;; delivery -> payment
;; payment  -> confirm
;; confirm  -> complete

(def sample-order { :id 7,
                    :adjustment_total nil
                    :state "cart"
                    :total 0E-8M
                    :billing_address_id nil
                    :shipment_status nil
                    :user_id 1
                    :status_id 1
                    :line_items {:1 {:id 7
                                     :quantity 4
                                     :price 10.00
                                     :product_id 1
                                     :order_id 7
                                     :created_at "2013-01-18T23:00:18.959014000-00:00"
                                     :updated_at "2013-01-18T23:00:18.959014000-00:00"}
                                 :2 {:id 6
                                     :quantity 4
                                     :price 2.80
                                     :product_id 2
                                     :order_id 7
                                     :created_at "2013-01-18T22:56:18.616241000-00:00"
                                     :updated_at "2013-01-19T00:00:30.293633000-00:00"}},
                    :special_instructions nil,
                    :purchased_at nil,
                    :email nil,
                    :last_modified "2013-01-18T21:11:45.445915000-00:00",
                    :payment_status nil,
                    :shipping_address_id nil,
                    :credit_total nil})

(defn init-order [& [customer-id]]
  (order/create {:user_id customer-id
                 :status_id 1
                 :total 0
                 :purchased_at nil
                 :state "cart"}))

(defn load-order []
  (order/find-by-id 7))
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
      (insert-product order-map product-id qty unit-price options))))

(defn insert-product [order-map product-id qty unit-price options]
  (let [product-id-key (keyword (str product-id))
        new-item (order/insert-item (:id order-map)
                           {:product_id product-id
                            :price unit-price
                            :quantity qty})]
    (order/update-total (assoc-in order-map [:line_items product-id-key] new-item))))

(defn update-product [order-map product-id qty unit-price options]
  (let [items (:line_items order-map)
        product-id-key (keyword (str product-id))
        item (get items product-id-key)]
    (if (> (+ qty (:quantity item)) 0)
      (when-let [updated-item (order/update-item 
                                (update-in
                                  (get items product-id-key)
                                  [:quantity] + qty))]
        (assoc-in order-map [:line_items product-id-key] updated-item))
      (remove-product))))

; (defn update-product [order-map product-id qty unit-price options]
;   (let [items (:line_items order-map)
;         product-id-key (keyword (str product-id))
;         item (get items product-id-key)]
;     (if (> (+ qty (:quantity item)) 0)
;       (when-let [updated-item (order/update-item 
;                                 (update-in
;                                   (get items product-id-key)
;                                   [:quantity] + qty))]
;         (order/update-total (assoc-in order-map [:line_items product-id-key] updated-item)))
;       (remove-product))))

(defn remove-product [order-map product-id]
  (if (order/remove-item (get-in order-map [:line_items (keyword (str product-id))]))
    (let [updated-order ((update-in order-map [:line_items] dissoc (keyword (str product-id))))]
      (order/update-total updated-order))))
