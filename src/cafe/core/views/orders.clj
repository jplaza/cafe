(ns cafe.core.views.orders
  (:use [cafe.core.views.common])
  (:require [noir.response :as resp]
            [noir.request :as req]
            [noir.session :as session]
            [net.cgrand.enlive-html :as html]
            [cafe.core.checkout.process :as process]
            [cafe.core.data.user :as users]))

(declare render-items cart-template)

;; View routes
(defn cart []
  (let [order (process/load-order)
        items (vals (:line_items order))]
    (cart-template order items)))

;; Helper functions
(defn cart-template [order items]
  (layout-one-col
    (html/at  (html/html-resource (script-path "orders" "cart"))
              [:div.content] 
              (if-not (empty? items)
                (fn [nodes] (html/at nodes
                  [:.cart-item]
                  (html/clone-for [item items]
                    [:.cart-item-description] (html/content (:description item))
                    [:.cart-item-price] (html/content (format-currency (:price item)))
                    [:.cart-item-total] (html/content (format-currency (* (:quantity item) (:price item))))
                    [:.cart-item-qty :input] (html/set-attr "value" (:quantity item)))
                  [:div#empty-cart-message] nil))
                (fn [nodes] (html/at nodes [:div#cart-container] nil))))))

(defn render-items [items]
  (if-not (empty items)
    (html/transform [:.cart-item]
                    (html/clone-for [item items]
                      [:.cart-item-description] (html/content (:description item))
                      [:.cart-item-price] (html/content (format-currency (:price item)))
                      [:.cart-item-total] (html/content (format-currency (* (:quantity item) (:price item))))
                      [:.cart-item-qty :input] (html/set-attr "value" (:quantity item)))
                      [:div.empty-cart-message] nil)
    (html/transform [:div.cart-container] nil)))

; (if-not (session/get :order-id false)
    ;   (session/put! :order-id (str "num-" (+ (rand-int 80) 20)))
    ;   (session/put! :user {:first-name "Juan Antonio" :last-name "Plaza"}))