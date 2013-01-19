(ns cafe.core.views.common
  (:require [noir.request :as request]
            [noir.response :as resp]
            [net.cgrand.enlive-html :as html]
            [noir.session :as session]))

(declare signed-in-user-name)

(defn script-path [view script]
  (str "cafe/core/views/scripts/" view "/" script ".html"))

;; @TODO: This function can be implemented as a recursive function in favor of a
;;        cleaner implementation
(defn insert-flash-message [html-nodes type messages]
  (if-let [msgs (get (merge messages (session/flash-get :messages)) type)]
    (do
      (html/at html-nodes
      [(keyword (str "div.alert-" (name type)))]
        (html/do->
          (html/content (type messages))
          (html/remove-class "hide"))))
    html-nodes))

;; ## Default (one column) layout
;; Injects the body and the title of the page using the provided parameters
(html/deftemplate layout-one-col "cafe/core/views/scripts/layout/default.html" [body & [messages]]
  [:div#body]
    (html/content
      (html/at body
        [:div#content-header]
        (html/content
          (-> (insert-flash-message
                (html/html-resource (script-path "layout" "_flash_messages")) :error messages)
              (insert-flash-message :warning messages)
              (insert-flash-message :success messages)))))
  [:a#user-name] (html/content (signed-in-user-name)))

(defn render-view
  "Returns the appropriate content based on the format parameter"
  [& view-path]
  (let [request (request/*request*)]
    (if (nil? ((request :headers) "X-Requested-With"))
      (resp/content-type "application/json" "{\"message\": \"This is an JSON response\"}")
      (layout-one-col ["Test"]))))

;; ## User realted functions

(defn register-signin [user]
  (session/put! :user user))

(defn signed-in? []
  (not (nil? (session/get :user))))

(defn signed-in-user-name []
  (if (signed-in?)
    (:name (session/get :user))
    "invitado"))

(defn format-currency [amount]
  (str "$" (format "%.2f" amount)))

; (defn get-view-script [])