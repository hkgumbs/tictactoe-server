(ns tictactoe-server.components.util
  (:require [webserver.response :as response])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard Board$Mark]))

(defn respond [content content-type]
  (let [headers {:Content-Type (str content-type "; charset=utf-8")
                 :Content-Length (count content)}]
    [(response/make 200 headers) content]))

(defn parse-int [number default]
  (try (Integer. ^String number) (catch Exception _ default)))

(defn- set-value [m k v] (assoc m (keyword k) (parse-int v v)))

(defn parse-parameters [parameters]
  (if parameters
    (loop [p (.split parameters "&") result {}]
      (if (empty? p) result
        (let [[p-name p-value] (.split (first p) "=")]
          (recur (rest p) (set-value result p-name p-value))))) {}))
