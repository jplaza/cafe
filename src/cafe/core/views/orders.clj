(ns cafe.core.views.orders
  (:use [cafe.core.views.common])
  (:require [noir.response :as resp]
            [noir.request :as req]
            [noir.session :as session]
            [net.cgrand.enlive-html :as html]
            [cafe.core.data.user :as users]))

(defn cart []
  (layout-one-col
    (html/html-resource (script-path "orders" "cart"))))