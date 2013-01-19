(ns cafe.core.data.order.shipment
  (:use [korma.db]
        [korma.core]
        [cafe.core.data.validation]))

(declare insert-items exists?)

(defentity orders)
(defentity shipment_items)

;; ### Shipments table fields
;; * id :integer
;; * order_id :reference orders
;; * number :string
;; * tracking_number :string
;; * shiped_at :datetime
;; * status :integer
;; * cost :float
;; * method :reference shipping_methods

(defentity shipments
  (belongs-to orders)
  (has-many shipment_items))

(defvalid shipment
  (validate :order_id :presence))

(defn create [new-shipment]
  (if (valid? new-shipment)
    (->
      (:id (insert shipments
        (values new-shipment)))
      (insert-items (:items new-shipment)))
    false))

(defn insert-items [shipment-id items]
  (insert shipment_items
    (values (map #(assoc % :shipment-id shipment-id) items))))

(defn update-attributes [shipment-id attributes]
  (when (map? attributes)
    (update shipments
      (set-fields attributes)
      (where {:id shipment-id}))))

(defn update-item [shipment-id item]
  (update shipment_items
    (set-fields item)
    (where {:id (:id item) :shipment_id shipment-id})))

(defn update-tracking-number [shipment-id tracking-number]
  (if (exists?)
    (update-attributes shipment-id {:tracking_number tracking-number})
    (throw (Exception. (str "Shipment[" shipment-id "] not found")))))

(defn find-by-id [id]
  (first
    (select shipments
      (where {:id id})
      (with shipment_items))))

(defn exists? [shipment-id]
  (if (integer? shipment-id)
    (find-by-id shipment-id)
    false))
