(ns tictactoe-server.util
  (:require [webserver.response :as response]
            [tictactoe-server.json :as json])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard Board$Mark]))

(defn- respond-400 [] [(response/make 400)])
(defn- respond-with-content [content content-type]
  (let [headers {:Content-Type (str content-type "; charset=utf-8")
                 :Content-Length (count content)}]
    [(response/make 200 headers) content]))
(defn- respond-with-body [content]
  (if (map? content)
    (respond-with-content (json/encode content) "application/json")
    (respond-with-content content "text/html")))
(defn respond [content]
  (if content (respond-with-body content) (respond-400)))

(defn parse-int [number default]
  (try (Integer. ^String number) (catch Exception _ default)))

(defn- set-value [m k v] (assoc m (keyword k) (parse-int v v)))
(defn parse-parameters [parameters]
  (if parameters
    (loop [p (.split parameters "&") result {}]
      (if (empty? p) result
        (let [[p-name p-value] (.split (first p) "=")]
          (recur (rest p) (set-value result p-name p-value))))) {}))
