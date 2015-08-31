(ns tictactoe-server.app
  (:require [webserver.response :as response]
            [clojure.java.io :as io]))

(defmulti route :uri)
(defmethod route :default [_] [(response/make 404)])

(defn- handle [socket request]
  (doall (for [r (route request)] (io/copy r (.getOutputStream socket)))))

(def responder
  {:valid-request-handler handle})
