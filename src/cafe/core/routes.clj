(ns cafe.core.routes
  (:use [compojure.core])
  (:require [cafe.core.views.home :as home]
            [cafe.core.views.orders :as orders]
            [cafe.core.views.users :as users]
            [compojure.route :as route]))

(defroutes users-routes
  (GET "/" [] "Home")
  (GET "/shop" [] (users/home))
  (GET "/register" [] (users/register))
  (GET "/sign-in" [] users/sign-in)
  (POST "/sign-out" [] users/sign-out)
  (POST "/users/validate" [email password] (users/validate email password))
  (POST "/users" [user] (users/create user))
  (GET "/account" {:keys [params]} (users/account)))

(defroutes orders-routes
  (GET "/cart" [] (orders/cart)))