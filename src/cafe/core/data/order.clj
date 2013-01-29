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

(declare prepare-input insert-items order-mapify-items calculate-total)

(defentity line_items)
(defentity payments)
(defentity shipments)

(defvalid order
  (validate :user_id :presence))
  ; (validate [:billing_address_id :shipping_address_id] :presence))

;; ### Orders table fields:
;; * id *integer*
;; * customer_id *integer*
;; * billing_address_id _integer_
;; * shipping_address_id _integer_
;; * purchased_at _datetime_
;; * updated_at _datetime_
;; * total _decimal_
;; * status_id _integer_
;; * special_instructions _varchar_

(defentity orders
  (table :orders)
  (has-many line_items {:fk :order_id})
  (has-many payments {:fk :order_id})
  (has-many shipments {:fk :order_id})
  (belongs-to status/status)
  (belongs-to users/users {:fk :user_id})
  (transform #(order-mapify-items %)))

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
    (set-fields { :total (:total (calculate-total order))
                  :updated_at (sqlfn now)})
    (where {:id (:id order)})))

(defn update-status [order new-status-id]
  (update orders
          (set-fields {:status_id new-status-id})
          (where {:id (:id order)})))

(defn set-billing-address [order address]
  (update-attribute (:id order) :billing_address_id (:id address)))

(defn set-shipping-address [order address]
  (update-attribute (:id order) :shipping_address_id (:id address)))

(defn calculate-total [order]
  (reduce + (map #(* (:price %) (:quantity %)) (-> order :line_items vals))))

(defn find-all []
  (select orders
    (with line_items)))

(defn find-by-id [order-id]
  (first (select orders
           (with line_items)
           (where {:id order-id}))))

(defn order-exists? [order-id]
  (boolean (find-by-id order-id)))

(defn mapify-items-list
  "Converts the items list of maps returned by the select query to a single map"
  [items]
  (into {} (map #(conj [] (keyword (str (:product_id %))) %) items)))

(defn order-mapify-items [order]
  (update-in order [:line_items] mapify-items-list))

(defn mapify-item [item]
  (hash-map (keyword (str (:product_id item))) item))

(defn delete! [id]
  (delete orders
          (where {:id id})))

;; # Line items functions
(defn insert-items
  "Helper function. Inserts line items in the database"
  [order-id items]
  (when items
    (mapify-items-list (insert line_items
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

(defn remove-item [item]
  (when (order-exists? (:order_id item))
    (delete orders
      (where (select-keys item [:id :order_id])))))

(defn update-items [order-id items]
  (when items
    (map #(update-item (assoc % :order_id order-id)) items)))

;; ## Private functions.

(defn- prepare-input
  "Removes related entities and prepares the order map to be inserted into the database"
  [order]
  (dissoc order :line-items))
