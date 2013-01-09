(ns cafe.core.views.home
  (:require [net.cgrand.enlive-html :as html]))

(def store {:title "Base Stores"
            :meta-author "Juan Antonio Arguello, Eduardo Raad"
            :meta-description "Ecommerce Platform"
            :welcome-message "Welcome customer!"
            :categories [{:name "Category 1" :uri "/category-1" :order 1}
                         {:name "Category 2" :uri "/category-2" :order 2}]
            :featured [{:name "Product 1"
                        :description "Description 1"
                        :image "/img/terror.png"}
                       {:name "Product 2"
                        :description "Description 2"
                        :image "/img/picantes.png"}]
            :products [{}]})

(def layout-template-url "cafe/core/views/scripts/layout/default.html")

(html/deftemplate layout layout-template-url [store]
  [:title]
  (html/content (:title store))
  [:meta (html/attr= (:name "author"))]
  (html/set-attr :content (:meta-author store))
  [:meta (html/attr= (:name "description"))]
  (html/set-attr :content (:meta-description store))
  [:.cafe-category]
  (html/clone-for [i (range 4)]
                  [:.cafe-category :a]
                  (html/do->
                   (html/content (:name ((:categories store) i)))
                   (html/set-attr "href" (:uri ((:categories store) i)))))
  [:div.featured1 #{:.name}]
  (html/content (:name ((:featured store) 0)))
  [:div.featured1 #{:.description}]
  (html/content (:description ((:featured store) 0)))
  [:div.featured1 :img]
  (html/set-attr :src (:image ((:featured store) 0)))
  [:div.featured2 #{:.name}]
  (html/content (:name ((:featured store) 1)))
  [:div.featured2 #{:.description}]
  (html/content (:description ((:featured store) 1)))
  [:div.featured2 :img]
  (html/set-attr :src (:image ((:featured store) 1)))
  [:div.content]
  (html/content (:welcome-message store)))

(defn home "/" []
  (layout store))
