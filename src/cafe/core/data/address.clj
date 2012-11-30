(ns cafe.core.data.address
  (:use [korma.db]
        [korma.core]
        [cafe.core.data.validation]))

(declare create update-all)

(defvalid address
  (validate [:firstname :lastname :address1 :city :province :country_id] :presence))

(defentity countries)
(defentity provinces)

;; ## Address fields
;; :firstname 
;; :lastname
;; :address1
;; :address2
;; :suburb
;; :city
;; :postcode
;; :province_id
;; :country_id

(defentity address
  (table :addresses)
  (has-one countries)
  (has-one provinces))

(defn save
  "Creates or updates an address in the database"
  [address]
  (if (valid? address)
    (if (contains? address :id)
      (update-all address)
      (create address))
    false))

;; ## Countries and provinces (subdivisions)
(defn find-country [country-id]
  (select countries
    (where {:id [= country-id]})))

(defn get-all-countries
  "Pulls a list of all countries stored in the database. Sample country map:
    { :id 1
      :name \"Ecuador\"
      :iso \"EC\"
      :iso3 \"ECU\"}"
  []
  (select countries))

(defn add-countries [countries-list]
  (insert countries
    (values countries-list)))

(defn add-provinces [provinces-list]
  (insert provinces
    (values provinces-list)))

;; ## Private functions
(defn- prepare-input [address]
  (dissoc address ))

(defn- create [new-address]
  (insert address
    (values new-address)))

(defn- update-all [new-address]
  (update address
    (set-fields (dissoc new-address :id))
    (where {:id [= (:id new-address)]})))