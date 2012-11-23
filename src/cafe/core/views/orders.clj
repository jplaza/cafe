(ns cafe.core.views.orders
  (:use [noir.core]
        [cafe.core.views.common])
  (:require [noir.response :as resp]
            [noir.request :as req]
            [noir.session :as session]
            [net.cgrand.enlive-html :as html]
            [cafe.core.data.user :as users]))

(defpage cart [:get "/cart"] {}
  (layout-one-col
    (html/html-resource (script-path "orders" "cart"))
    (session/flash-get)))