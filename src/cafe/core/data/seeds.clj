(ns cafe.core.data.seeds
  (:use [korma.core]
        [korma.db])
  (:require [cafe.core.data.base :as data]
            [cafe.core.data.order :as orders]
            [cafe.core.data.product :as products]
            [cafe.core.data.user :as users]
            [cafe.core.data.status :as status]
            [cafe.core.data.address :as address]))

;; # Fixtures to test data functions
(def product-fixtures '(
  { :name "Cafe Latte"
    :sku "cl-123"
    :description "Cafe con leche"
    :slug "cafe-latte"
    :price 2.99}
  { :name "Milkshake de Chocolate"
    :sku "ms-456"
    :description "Batido de leche con helado de chocolate"
    :slug "milkshake-chocolate"
    :price 4.99}))

(def order-fixture
  {:user_id 1
   :total 7.98
   ;:purchased_at "2012-09-24 12:00"
   ;:updated_at "2012-09-24 12:00"
   :billing_address_id 1
   :shipping_address_id 1
   :status_id 1
   :state "cart"
   :special_instructions "Some instructions"
   :line-items '(
    {:product_id 1 :quantity 3 :price 2.99}
    {:product_id 2 :quantity 1 :price 4.99}
   )})

(def roles-fixtures '(
  {:name "admin"}
  {:name "user"}))

(def user-fixture {
  :name "Juan Antonio"
  :lastname "Plaza"
  :email "jplazaarguello@gmail.com"
  :password "12345678"})

(def address-fixture {
  :firstname "Juan Antonio"
  :lastname "Plaza"
  :address1 "Calle 9na Este #212"
  :suburb "Kennedy Nueva"
  :city "Guayaquil"
  :postcode "EC090112"
  :province_id 1
  :country_id 1})

(def countries-fixtures '(
  {:name "Ecuador" :iso "EC" :iso3 "ECU" :numcode 283}
  {:name "United States" :iso "US" :iso3 "USA" :numcode 700}))

(def provinces-fixtures '(
  {:name "Guayas" :iso "G" :country_id 1}
  {:name "Pichincha" :iso "P" :country_id 1}
  {:name "Manabí" :iso "M" :country_id 1}
  {:name "California" :iso "CA" :country_id 2}
  {:name "Oregon" :iso "OR" :country_id 2}
  {:name "Florida" :iso "FL" :country_id 2}))

(def status-fixtures '(
  {:name "Pending" :machine_readable "pending"}
  {:name "Processing" :machine_readable "procesing"}
  {:name "On Hold" :machine_readable "on-hold"}
  {:name "Assigned" :machine_readable "assigned"}))

(defn seed-db []
  (print "Seeding data base...")
  (products/add (first product-fixtures))
  (products/add (last product-fixtures))
  (users/create user-fixture)
  (users/add-roles roles-fixtures)
  (address/add-countries countries-fixtures)
  (address/add-provinces provinces-fixtures)
  (address/save address-fixture)
  (status/add status-fixtures)
  (orders/create order-fixture))

(defn -main []
  (data/init)
  (seed-db)
  (println "done!"))