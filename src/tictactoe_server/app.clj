(ns tictactoe-server.app
  (:require [webserver.get]
            [webserver.app :as app]
            [clojure.java.io :as io]))

(def most-recent-socket (atom nil))
(defmulti route :uri)
(defmethod route :default [request] (app/route @most-recent-socket request))

(defn- copy [source socket] (io/copy source (.getOutputStream socket)))
(defn handle [socket request]
  (reset! most-recent-socket socket)
  (doseq [response (route request)] (copy response socket)) true)

(defn initialize [] (app/initialize ["-d" "assets"]))
