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
            [cafe.core.data.address :as address]
            [cafe.core.data.status :as status]))

(declare prepare-input insert-items)

(defentity line_items)
(defentity payments)
(defentity shipments)

(defvalid order 
  (validate [:billing_address_id :shipping_address_id] :presence))

(defn get-errors []
  (errors))

;; ### Orders table fields:
;; * id *integer*
;; * customer_id *integer*
;; * billing_address_id _integer_
;; * shipping_address_id _integer_
;; * date_purchased _datetime_
;; * last_modified _datetime_
;; * total _decimal_
;; * status_id _integer_
;; * special_instructions _varchar_
;; * shipping_method_id _integer_

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
  (if (valid? new-order)
    (->
      (:id (insert orders
        (values (prepare-input new-order))))
      (insert-items (:line-items new-order)))
    false))

(defn update-total [order]
  (update orders
          (set-fields { :total (:total order)
                        :updated_at "NOW()"})
          (where {:id (:id order)})))

(defn update-status [order status-id]
  (update orders
          (set-fields {:status_id status-id})
          (where {:id (:id order)})))

(defn find-all []
  (select orders
    (with line_items)))

(defn find-by-id [order-id]
  (first (select orders
    (with line_items)
    (where {:id (Integer/parseInt order-id)}))))

(defn delete! [id]
  (delete orders
          (where {:id id})))

;; # Line items functions
(defn insert-items
  "Helper function. Inserts items in the database"
  [order-id items]
  (insert line_items
    (values (map #(assoc % :order_id order-id) items))))

;; ## Private functions.

(defn- prepare-input
  "Removes related entities and prepares the order map to be inserted into the database"
  [order]
  (dissoc order :line-items))
