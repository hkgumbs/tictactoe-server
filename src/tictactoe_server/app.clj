(ns tictactoe-server.app
  (:require [webserver.app :as app]
            [webserver.get]
            [clojure.java.io :as io]))

(defmulti route :uri)
(defmethod route :default [request])

(defn handle [socket request]
  (if-let [response (route request)]
    (do (doseq [r response] (io/copy r (.getOutputStream socket))) true)))
