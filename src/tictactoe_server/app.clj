(ns tictactoe-server.app
  (:require [cob-app.get]
            [cob-app.core :as backing-app]
            [clojure.java.io :as io]))

(defmulti route :uri)
(defmethod route :default [request] (backing-app/route request nil))

(defn- copy [source socket] (io/copy source (.getOutputStream socket)))
(defn- handle [socket request]
  (let [response (route request)] (doseq [r response] (copy r socket))))
(defn- initialize [] ((:initializer backing-app/responder) ["-d" "assets"]))
(def responder {:valid-request-handler handle :initializer initialize})
