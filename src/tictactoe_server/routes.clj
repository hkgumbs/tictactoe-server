(ns tictactoe-server.routes
  (:require [tictactoe-server.app :as app]
            [tictactoe-server.storage :as storage]
            [webserver.response :as response]
            [cheshire.core :as json]))

(defn- set-value [m k v]
  (assoc m (keyword k) (try (Integer. ^String v) (catch Exception _ v))))

(defn parse-parameters [{parameters :parameters :or {parameters ""}}]
  (loop [p (.split parameters "&") result {}]
    (if (empty? p) result
      (let [[p-name p-value] (.split (first p) "=")]
        (recur (rest p) (set-value result p-name p-value))))))

(defmethod app/route "/new" [request]
  [(response/make 200)
   (storage/create (parse-parameters request))])
