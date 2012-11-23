(ns cafe.api.views.users
  (:use [noir.core]
        [cafe.core.views.common])
  (:require [noir.request :as req]
            [noir.response :as resp]
            [noir.session :as session]
            [cafe.core.data.user :as users]))

;; # API User resource actions
;; NOTE: This is the first version of the API, hence error handling is pretty basic.
;; Specific error codes and messages will be returned in future releases of this
;; API.

(defpage user-list [:get "/api/users"] {}
  (resp/json (users/find-all)))

(defpage show [:get "/api/users/:id"] {:keys [id]}
  (if-let [user (users/find-by-id id)]
    (resp/json user)
    (resp/status 404 (resp/json {:message "User not found"}))))

(defpage create [:post "/api/users"] {:keys [user]}
  (if (users/create user)
    (do
      (register-signin user)
      (resp/json {:message "User created"}))
    (resp/status 400 (resp/json {:message "Email address already taken"}))))

(defpage update [:put "/api/users/:id"] {:keys [user id]}
  (if (users/update-attributes (assoc user :id id))
    (resp/json {:message "User updated"})
    (resp/status 404 (resp/json {:message "User not found"}))))

(defpage destroy [:delete "/api/users/:id"] {:keys [id]}
  (if (users/destroy id)
    (resp/json {:message "User deleted"})
    (resp/status 404 (resp/json {:message "User not found"}))))

(defpage session-create [:post "/api/session/"] {:keys [email password]}
  (if (users/authenticate email password (:remote-addr req/ring-request))
    (let [user (users/find-by-email email)]
      (do
        (register-signin (users/find-by-email email))
        (resp/json user)))
    (resp/status 403 (resp/json (:message "Unauthorized")))))
