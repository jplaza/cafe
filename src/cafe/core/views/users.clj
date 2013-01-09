(ns cafe.core.views.users
  (:use [cafe.core.views.common])
  (:require [noir.response :as resp]
            [noir.request :as req]
            [noir.session :as session]
            [net.cgrand.enlive-html :as html]
            [cafe.core.data.user :as users]))

;; Temporary test page
(defn home []
  (layout-one-col
    (html/html-resource (script-path "users" "shop"))))

(defn register [& request]
  (layout-one-col (html/html-resource (script-path "users" "new"))))

(defn sign-in [& request]
  (if (signed-in?)
    (resp/redirect "/account")
    (layout-one-col
      (html/html-resource (script-path "users" "sign-in")))))

(defn sign-out []
  (session/clear!)
  (resp/redirect "/shop"))

(defn validate [email password]
  (if (users/authenticate email password "0.0.0.0")
    (do
      (register-signin (users/find-by-email email))
      (resp/redirect "/account"))
    (do
      (session/flash-put! :messages {:error "Correo electrónico o contraseña incorrectos"})
      (resp/redirect "/sign-in"))))

(defn create [user]
  (if (users/create user)
    (do
      (session/flash-put! :messages {:success (format "¡Bienvenido %s! Tu cuenta ha sido creada" (:name user))})
      (register-signin user)
      (resp/redirect "/shop"))
    (do
      (session/flash-put! :messages {:error "El correo que utilizaste ya está en nuestros
                                   registros. Si olvidaste tu contraseña da clic
                                   <a href=\"\">aquí</a>"})
      (resp/redirect "/users/register"))))

(defn account []
  (if-not (signed-in?)
    (resp/redirect "/sign-in")
    (layout-one-col
      (html/html-resource (script-path "users" "account")))))
