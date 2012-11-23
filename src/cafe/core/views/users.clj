(ns cafe.core.views.users
  (:use [noir.core]
        [cafe.core.views.common])
  (:require [noir.response :as resp]
            [noir.request :as req]
            [noir.session :as session]
            [net.cgrand.enlive-html :as html]
            [cafe.core.data.user :as users]))

;; Temporary test page
(defpage "/shop" {}
  (layout-one-col
    (html/html-resource (script-path "users" "shop"))
    (session/flash-get)))

(defpage register [:get "/register"] {}
  (layout-one-col (html/html-resource (script-path "users" "new"))))

(defpage sign-in [:get "/users/sign-in"] {}
  (layout-one-col
    (html/html-resource (script-path "users" "sign-in"))
    (session/flash-get)))

(defpage [:post "/users/sign-out"] {}
  (session/clear!)
  (resp/redirect "/shop"))

(defpage [:post "/users/validate"] {:keys [email password]}
  (if (users/authenticate email password (:remote-addr req/ring-request))
    (do
      (register-signin (users/find-by-email email))
      (resp/redirect (url-for account)))
    (do
      (session/flash-put! {:error "Correo electrónico o contraseña incorrectos"})
      (resp/redirect (url-for sign-in)))))

(defpage users [:post "/users"] {:keys [user]}
  (if (users/create user)
    (do
      (session/flash-put! {:success (format "¡Bienvenido %s! Tu cuenta ha sido creada" (:name user))})
      (register-signin user)
      (resp/redirect "/shop"))
    (do
      (session/flash-put! {:error "El correo que utilizaste ya esta registrado"})
      (resp/redirect "/users/register"))))

(defpage account "/account" {}
  (if-not (signed-in?)
    (resp/redirect (url-for sign-in))
    (layout-one-col
      (html/html-resource (script-path "users" "account"))
      (session/flash-get))))

(defpage tests "/users/tests" {}
  (render-view))
