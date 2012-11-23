;; #API order resource
;; 
;; This resource will provide the entry point to store orders and _transactions_
;; received from clients. One of the first clients of the API will be the mobile
;; application POS that will enable merchants to accept credit cards using their
;; smart phones.
(ns cafe.api.views.orders
  (:use [noir.core]
        [cafe.core.views.common])
  (:require [noir.request :as req]
            [noir.response :as resp]
            [noir.session :as session]
            [cafe.core.data.order :as orders]))

(defpage order-list [:get "/api/orders"] {}
  (resp/json (orders/find-all)))

(defpage show [:get "/api/orders/:id"] {:keys [id]}
  (if-let [order (orders/find-by-id id)]
    (resp/json order)
    (resp/status 404 (resp/json))))

;; ### Create order
;;
;;     POST /api/orders
;;
;; An order will be created with the json data posted in the body of the request
;;
;; __Request__
;;
;;     { 
;;         "order":
;;         "billing_address": {}
;;         "shipping_address": {}
;;         "line-items": [
;;           {
;;             "product_id": 1,
;;             "quantity": 5,
;;             "price": 9.49,
;;             "description": "Snickers"
;;           }
;;         ]
;;     }
;;
;; __Response__
;;
;;     { 
;;         "order": {
;;           "billing_address": {}
;;           "shipping_address": {}
;;           "line-items": [
;;             {
;;               "product_id": 1,
;;               "quantity": 5,
;;               "price": 9.49,
;;               "description": "Snickers"
;;             }
;;           ]
;;         }
;;     }
;;
;; TODO: the actual creation of the order should be performed by a different
;; function that could take care of sanitizing the input.
(defpage create [:post "/api/orders"] {:keys [json]}
  (if-let [order (orders/create (:order json))]
    (resp/json order)
    (resp/status 422 (resp/json {:error "Unprocesable entity"}))))

(defpage update [:put "/api/orders/:id"] {:keys [order]}
  "Under construction")