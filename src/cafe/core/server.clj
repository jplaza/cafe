(ns cafe.core.server
  (:use [noir.core]
        [clojure.tools.nrepl.server :only (start-server stop-server)])
  (:require [noir.server :as server]
            [noir.response :as resp]
            [cheshire.core :as json]
            [cafe.core.data.base :as data]))

;; Create repl server
(defonce repl-server (start-server :port 7888))

;; Initialize database connection
(data/init)

;; Middleware that checks the body of the request for a JSON string and appends
;; the parsed data as request parameters as the :params key
(defn json-data [handler]
  (fn [req]
    (let  [new-req
            (if (= "application/json" (get-in req [:headers "content-type"]))
              (update-in req [:params] assoc :json (json/parse-string (slurp (:body req)) true))
              req)]
      (handler new-req))))

(defn -main [& m]
  (let [mode (or (first m) :dev)
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    ;; This would be the desired way to plug the middleware to parse the body
    ;; since it will only affect the API route. This functionality is not correctly
    ;; implemented in Noir so we will have to wait probably until the next stable
    ;; release.
    ; (server/wrap-route "/api/*" json-data)
    (server/add-middleware json-data)
    (server/start port {:mode (keyword mode)
                        :ns 'cafe})
    (server/load-views "src/cafe/core/views" "src/cafe/api/views")))