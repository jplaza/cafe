;; # Orders
;;
;; Orders business logic. An order groups all the information related to a
;; purchase. Products (line items), billing address, payments,
;; shipping address, shipments.
;;
(ns cafe.core.data.order
  (:use [korma.db]
        [korma.core]
        [cafe.core.data.validation])
  (:require [cafe.core.data.user :as users]
            [cafe.core.data.order.shipment :as shipment]
            [cafe.core.data.order.payment :as payment]
            [cafe.core.data.address :as address]
            [cafe.core.data.status :as status]))

(declare prepare-input insert-items mapify-items mapify-item)

(defentity line_items
  (transform mapify-item))
(defentity payments)
(defentity shipments)

(defvalid order
  (validate :user_id :presence))
  ; (validate [:billing_address_id :shipping_address_id] :presence))
(defn validation-rules []
  (deref rules))

;; ### Orders table fields:
;; * id *integer*
;; * customer_id *integer*
;; * billing_address_id _integer_
;; * shipping_address_id _integer_
;; * purchased_at _datetime_
;; * last_modified _datetime_
;; * total _decimal_
;; * status_id _integer_
;; * special_instructions _varchar_

(defentity orders
  (table :orders)
  (has-many line_items {:fk :order_id})
  (has-many payments {:fk :order_id})
  (has-many shipments {:fk :order_id})
  (belongs-to status/status)
  (belongs-to users/users {:fk :user_id}))

;; ## Data store functions

; (defn create [new-order]
;   (try
;     (if (valid? new-order)
;       (->
;         (:id (insert orders
;           (values (prepare-input new-order))))
;         (insert-items (:line-items new-order))))
;     (catch Exception e
;       ;; log error
;       ;; (println "Error SQL: " (.getMessage e))
;       false)))

(defn create [new-order]
  (when (valid? new-order)
    (when-let [order-record (insert orders
                              (values (prepare-input new-order)))]
      (assoc order-record :line-items (insert-items (:id order-record) (:line-items new-order))))))

(defn update-attribute [record-id attribute new-value]
  (update orders
    (set-fields { attribute new-value
                  :updated_at (sqlfn now)})
    (where {:id record-id})))

(defn update-total [order]
  (update orders
          (set-fields { :total (:total order)
                        :updated_at (sqlfn now)})
          (where {:id (:id order)})))

(defn update-status [order status-id]
  (update orders
          (set-fields {:status_id status-id})
          (where {:id (:id order)})))

(defn set-billing-address [order-id address-id]
  (update-attribute order-id :billing_address_id address-id))

(defn set-shipping-address [order-id address-id]
  (update-attribute order-id :shipping_address_id address-id))

(defn calculate-total [items]
  (reduce + (map #(* (:price %) (:quantity %)) items)))

(defn find-all []
  (select orders
    (with line_items)))

(defn find-by-id [order-id]
  (first (select orders
           (with line_items)
           (where {:id (Integer/parseInt order-id)}))))

(defn mapify-item [item]
  (hash-map (keyword (str (:product_id item))) item))

(defn mapify-items
  [items]
  "Returns a map of items, using the product (variant id) as the key for each item"
  (if (or (list? items) (coll? items))
    (map mapify-item items)
    items))

(defn delete! [id]
  (delete orders
          (where {:id id})))

;; # Line items functions
(defn insert-items
  "Helper function. Inserts line items in the database"
  [order-id items]
  (when items
    (mapify-items (insert line_items
                    (values (map #(assoc % :order_id order-id) items))))))

(defn insert-item
  "Inserts one line item in the database"
  [order-id item]
  (mapify-item (insert line_items
                 (values (assoc item :order_id order-id)))))

(defn update-item [item]
  (when (:order_id item)
    (update line_items
      (set-fields (assoc item :updated_at (sqlfn now)))
      (where (select-keys item [:id :order_id])))))

(defn update-items [order-id items]
  (when items
    (map #(update-item (assoc % :order_id order-id)) items)))

;; ## Private functions.

(defn- prepare-input
  "Removes related entities and prepares the order map to be inserted into the database"
  [order]
  (dissoc order :line-items))
