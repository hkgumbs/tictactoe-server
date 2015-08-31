(ns tictactoe-server.app
  (:require [webserver.response :as response]
            [clojure.java.io :as io]))

(defmulti route :uri)
(defmethod route :default [_] (response/make 404))

(defn- handle [socket request]
  (io/copy (route request) (.getOutputStream socket)))

(def responder
  {:valid-request-handler handle})
