(ns lobos.migrations
  (:refer-clojure :exclude [alter drop bigint boolean char double float time])
  (:use (lobos [migration :only [defmigration]] core schema) lobos.config))

(defmigration create-table-properties
  (up []
      (create
       (table :properties
              (integer :id :primary-key :auto-inc)
              (varchar :name 100 :not-null)
              (varchar :description 100 :not-null)
              (varchar :value 100 :not-null))))
  (down []
        (drop (table :properties))))

(defmigration create-table-categories
  (up [] (create (table :categories
                        (integer :id :primary-key :auto-inc)
                        (varchar :slug 100 :not-null)
                        (varchar :name 100 :not-null)
                        (varchar :description 255 :not-null))))
  (down [] (drop (table :categories))))

(defmigration create-table-products
  (up [] (create (table :products
                        (integer :id :primary-key :auto-inc)
                        (varchar :sku 100)
                        (varchar :slug 100 :not-null)
                        (varchar :name 100 :not-null)
                        (varchar :description 255 :not-null)
                        (decimal :price))))
  (down [] (drop (table :products))))

(defmigration create-table-roles
  (up []
    (create
      (table :roles
        (integer :id :primary-key :auto-inc)
        (varchar :name 16))))
  (down [] (drop (table :roles))))

(defmigration create-table-users
  (up [] (create (table :users
                        (integer :id :primary-key :auto-inc)
                        (varchar :name 80)
                        (varchar :lastname 80)
                        (varchar :email 200)
                        (varchar :password 128)
                        (varchar :password_salt 128)
                        (nchar :authentication_token)
                        (varchar :current_sign_in_ip 39)
                        (varchar :last_sign_in_ip 39)
                        (varchar :password_salt 128)
                        (integer :sign_in_count :not-null (default 0))
                        (integer :failed_attempts :not-null (default 0))
                        (timestamp :current_sign_in_at)
                        (timestamp :last_sign_in_at)
                        (timestamp :last_request_at)
                        (timestamp :reset_password_sent_at)
                        (timestamp :created_at (default (now)))
                        (timestamp :updated_at (default (now)))
                        (unique [:email]))))
  (down [] (drop (table :users))))

(defmigration create-table-roles-users
  (up []
    (create
      (table :roles_users
        (integer :user_id :not-null [:refer :users :id])
        (integer :role_id :not-null [:refer :roles :id])
        (primary-key [:user_id :role_id]))))
  (down [] (drop (table :roles_users))))

(defmigration create-table-countries
  (up [] (create (table :countries
                        (integer :id :primary-key :auto-inc)
                        (varchar :name 50)
                        (char :iso 2)
                        (char :iso3 3)
                        (integer :numcode))))
  (down [] (drop (table :countries))))

(defmigration create-table-provinces
  (up []
    (create
      (table :provinces
        (integer :country_id :not-null [:refer :countries :id])
        (varchar :iso 2)
        (varchar :name 50)
        (integer :id :primary-key :auto-inc))))
  (down [] (drop (table :provinces))))

(defmigration create-table-addresses
  (up [] (create (table :addresses
                        (integer :id :primary-key :auto-inc)
                        (varchar :firstname 50)
                        (varchar :lastname 50)
                        (varchar :address1 100)
                        (varchar :address2 100)
                        (varchar :suburb 50)
                        (varchar :postcode 10)
                        (varchar :city 30)
                        (integer :country_id :not-null [:refer :countries :id])
                        (integer :province_id :not-null [:refer :provinces :id])
                        (varchar :phone 10)
                        (timestamp :created_at (default (now)))
                        (timestamp :updated_at (default (now))))))
  (down [] (drop (table :addresses))))

(defmigration create-table-status
  (up [] (create (table :status
                        (integer :id :primary-key :auto-inc)
                        (varchar :name 20)
                        (varchar :machine_readable 20))))
  (down [] (drop (table :status))))

(defmigration create-table-orders
  (up []
    (create
      (table :orders
        (integer :id :primary-key :auto-inc)
        (integer :user_id :null [:refer :users :id])
        (text :email)
        (decimal :total 10 8)
        (decimal :adjustment_total 10 8)
        (decimal :credit_total 10 8)
        (varchar :state 15)
        (integer :status_id :not-null [:refer :status :id])
        (integer :billing_address_id [:refer :addresses :id])
        (integer :shipping_address_id [:refer :addresses :id])
        (varchar :shipment_status 50)
        (varchar :payment_status 50)
        (text :special_instructions)
        (timestamp :purchased_at (default (now)))
        (timestamp :last_modified (default (now))))))
  (down [] (drop (table :orders))))

(defmigration create-table-line-items
  (up [] (create (table :line_items
                        (integer :id :primary-key :auto-inc)
                        (integer :quantity)
                        (decimal :price 10 8 [:not-null])
                        (integer :product_id :not-null [:refer :products :id])
                        (integer :order_id :not-null [:refer :orders :id])
                        (timestamp :created_at (default (now)))
                        (timestamp :updated_at (default (now))))))
  (down [] (drop (table :line_items))))

(defmigration create-table-shipments
  (up [] (create (table :shipments
                        (integer :id :primary-key :auto-inc)
                        (integer :order_id :not-null [:refer :orders :id])
                        (varchar :number 20 :not-null)
                        (text :tracking_number)
                        (timestamp :shiped_at)
                        (varchar :status 15)
                        (text :notes)
                        (timestamp :created_at (default (now)))
                        (timestamp :updated_at (default (now))))))
  (down [] (drop (table :shipments))))

(defmigration create-table-shipment-items
  (up [] (create (table :shipment_items
                        (integer :id :primary-key :auto-inc)
                        (integer :product_id :not-null [:refer :products :id])
                        (integer :order_id :not-null [:refer :orders :id])
                        (timestamp :created_at (default (now)))
                        (timestamp :updated_at (default (now))))))
  (down [] (drop (table :shipment_items))))

(defmigration create-table-payment-methods
  (up [] (create (table :payment_methods
                        (integer :id :primary-key :auto-inc)
                        (varchar :name 30 :not-null)
                        (text :description)
                        (smallint :enabled)
                        (integer :type)
                        (timestamp :created_at (default (now)))
                        (timestamp :updated_at (default (now))))))
  (down [] (drop (table :payment_methods))))

(defmigration create-table-payments
  (up [] (create (table :payments
                        (integer :id :primary-key :auto-inc)
                        (integer :order_id :not-null [:refer :orders :id])
                        (decimal :amount 10 8 [:not-null] (default 0.0))
                        (integer :payment_method_id [:refer :payment_methods :id])
                        (timestamp :created_at (default (now)))
                        (timestamp :updated_at (default (now))))))
  (down [] (drop (table :payments))))

(defmigration create-table-shipping-methods
  (up [] (create (table :shipping_methods
                        (integer :id :primary-key :auto-inc)
                        (varchar :name 30 :not-null)
                        (text :description)
                        (integer :zone_id)
                        (smallint :enabled)
                        (timestamp :created_at (default (now)))
                        (timestamp :updated_at (default (now))))))
  (down [] (drop (table :shipping_methods))))

;;(defmigration add-products-table
  ;; code be executed when migrating the schema "up" using "migrate"
;; (up [] (create
;; (table :products
;;       (integer :id :primary-key :auto-inc)
;;       (varchar :name 100 :not-null)
;;       (varchar :description 255 :not-null)
;;       (varchar :price 255)
;;)))

;; Code to be executed when migrating schema "down" using "rollback"
;;(down [] (drop (table :products ))))

(defn -main []
  (println "Migrating database...") (flush)
  (println "rolling back")
  (rollback :all)
  (println "migrating")
  (migrate)
  (println "done!"))
