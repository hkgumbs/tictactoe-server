(ns tictactoe-server.util
  (:require [webserver.response :as response]
            [cheshire.core :as json])
  (:import [me.hkgumbs.tictactoe.main.java.board
            SquareBoard Board$Mark]))

(defn respond
  ([] (response/make 200))
  ([content]
   (let [body (json/encode content)
         headers {:Content-Type "application/json; charset=utf-8"
                  :Content-Length (count body)}]
     (str (response/make 200 headers) body))))

(defn- add-mark [[board i] mark-string]
  (let [marks {\X Board$Mark/X \O Board$Mark/O}
        mark (marks mark-string)]
    [(if mark (.add board i mark) board) (inc i)]))

(defn decode-square-board [encoded-board size]
  (let [marks {"X" Board$Mark/X "O" Board$Mark/O}]
    (first (reduce add-mark [(SquareBoard. 3) 0] encoded-board))))

(defn- set-value [m k v]
  (assoc m (keyword k) (try (Integer. ^String v) (catch Exception _ v))))

(defn parse-parameters [parameters]
  (if parameters
    (loop [p (.split parameters "&") result {}]
      (if (empty? p) result
        (let [[p-name p-value] (.split (first p) "=")]
          (recur (rest p) (set-value result p-name p-value))))) {}))
