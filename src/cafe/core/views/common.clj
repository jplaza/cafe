(ns cafe.core.views.common
  (:use [noir.core])
  (:require [noir.request :as request]
            [noir.response :as resp]
            [net.cgrand.enlive-html :as html]
            [noir.session :as session]))

(defn script-path [view script]
  (str "cafe/views/scripts/" view "/" script ".html"))

;; @TODO: This function can be implemented as a recursive function in favor of a
;;        cleaner implementation
(defn insert-flash-message [html-nodes type messages]
  (if (get messages type)
    (do
      (html/at html-nodes
      [(keyword (str "div.alert-" (name type)))]
        (html/do->
          (html/content (type messages))
          (html/remove-class "hiden"))))
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
              (insert-flash-message :success messages))))))

(defn render-view
  "Returns the appropriate content based on the format parameter"
  [& view-path]
  (let [request (request/ring-request)]
    (if (nil? (((noir.request/ring-request) :headers) "X-Requested-With"))
      (resp/content-type "application/json" "{\"message\": \"This is an JSON response\"}")
      (layout-one-col ["Test"]))))

;; ## User realted functions

(defn register-signin [user]
  (session/put! :user user))

(defn signed-in? []
  (not (nil? (session/get :user))))

; (defn get-view-script [])