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

(defn- write [response output-stream]
  (doseq [r response] (io/copy r output-stream)))
(defn handle [socket request]
  (if-let [response (route (map-parameters request))]
    (do (write (util/respond response) (.getOutputStream socket)) true)))
