(ns tictactoe-server.app
  (:require [webserver.app :as app]
            [webserver.get]
            [tictactoe-server.util :as util]
            [clojure.java.io :as io]))

(defmulti route :uri)
(defmethod route :default [_])
(defmethod route "/" [_] (slurp "assets/index.html"))

(defn map-parameters [request]
  (update request :parameters #(if % (util/parse-parameters %))))

(defn handle [socket request]
  (if-let [response (util/respond (route (map-parameters request)))]
    (do (doseq [r response] (io/copy r (.getOutputStream socket))) true)))
