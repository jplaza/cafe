(ns cafe.core.data.base
  (:require [cafe.util.config :as util])
  (:use [korma.db]))

(defn init []
  (defdb cafedb (util/get-db-config)))
