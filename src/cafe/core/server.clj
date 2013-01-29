(ns cafe.core.server
  (:require [noir.util.middleware :refer [app-handler wrap-strip-trailing-slash wrap-force-ssl]]
            [noir.session :refer [wrap-noir-flash wrap-noir-session mem]]
            [ring.middleware.session.memory :refer [memory-store]]
            [compojure.core :refer [routes]]            
            [compojure.handler :as handler]
            [compojure.route :as route]
            [cheshire.core :as json]
            [cafe.core.data.base :as data]
            [cafe.core.routes :refer :all]
            [clojure.tools.nrepl.server :refer :all]))

; [ring.middleware :refer [multipart-params]]

;; Create repl server
; (defonce repl-server (start-server :port 7888))

;; Initialize database connection
(data/init)

;; Middleware that checks the body of the request for a JSON string and appends
;; the parsed data as request parameters as the :params key
; (defn json-data [handler]
;   (fn [req]
;     (let [new-req (if (= "application/json" (get-in req [:headers "content-type"]))
;                   (update-in req [:params] assoc :json (json/parse-string (slurp (:body req)) true))
;                   req)]
;       (handler new-req))))

(def app
  (-> (routes users-routes
              orders-routes
              (route/resources "/")
              (route/not-found "Not Found"))
      (handler/api)
      (wrap-noir-flash)
      (wrap-noir-session {:store (memory-store mem)})))