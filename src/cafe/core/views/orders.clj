(ns cafe.core.views.orders
  (:use [cafe.core.views.common])
  (:require [noir.response :as resp]
            [noir.request :as req]
            [noir.session :as session]
            [net.cgrand.enlive-html :as html]
            [cafe.core.checkout.process :as process]
            [cafe.core.data.user :as users]))

(defn cart-template [order items]
  (layout-one-col
    (html/at (html/html-resource (script-path "orders" "cart"))
             [:.cart-item] (html/clone-for [item items]
                           [:.cart-item-description] (html/content (:product_name item))
                           [:.cart-item-price] (html/content (format-currency (:price item)))
                           [:.cart-item-qty :input] (html/set-attr "value" (:quantity item))))))

(defn cart []
  (let [order (process/load-order)
        items (:line_items order)]
    ; (cart-template order items)
    (if-not (session/get :order-id false)
      (session/put! :order-id (str "num-" (+ (rand-int 80) 20))))
    (str (session/get :order-id "SESSION NOT FOUND") " " order)))