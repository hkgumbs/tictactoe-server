(ns tictactoe-server.app
  (:require [webserver.app :as app]
            [webserver.get]
            [tictactoe-server.util :as util]
            [tictactoe-server.json :as json]
            [clojure.java.io :as io]))

(defmulti route :uri)
(defmethod route :default [_])

(def ^:private static
  {"/" [(slurp "assets/index.html") "text/html"]
   "/style.css" [(slurp "assets/style.css") "text/css"]})

(defn map-parameters [request]
  (update request :parameters #(if % (util/parse-parameters %))))

(defn- write [response output-stream]
  (doseq [r response] (io/copy r output-stream)) true)

(defn- get-response [{uri :uri :as request}]
  (if-let [json-response (route (map-parameters request))]
    [(json/encode json-response) "application/json"] (static uri)))

(defn handle [socket request]
  (if-let [response (get-response request)]
    (write (apply util/respond response) (.getOutputStream socket))))
